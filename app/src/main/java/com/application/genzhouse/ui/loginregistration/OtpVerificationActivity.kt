package com.application.genzhouse.ui.loginregistration

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.application.genzhouse.R
import com.application.genzhouse.data.model.UserRequest
import com.application.genzhouse.databinding.ActivityOtpVerificationBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.OwnerDashBoard
import com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.SellRentPropertyForm
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.viewmodel.CreateUserViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var activityOtpVerificationBinding: ActivityOtpVerificationBinding
    private lateinit var progressDialog: CustomProgressDialog
    private val auth = FirebaseAuth.getInstance()
    private lateinit var OTP: String
    private lateinit var name : String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber: String
    private lateinit var viewmodel: CreateUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityOtpVerificationBinding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(activityOtpVerificationBinding.root)
        viewmodel = ViewModelProvider(this)[CreateUserViewModel::class.java]
        // Initialize progress dialog
        progressDialog = CustomProgressDialog(this)

        initUI()
        onClick()
    }

    private fun initUI() {
        // Get OTP, resend token, and phone number from intent
        OTP = intent.getStringExtra("OTP").toString()

        resendToken = intent.getParcelableExtra("resendToken")!!
        name = intent.getStringExtra("name")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!

        // Set the phone number in the UI
        activityOtpVerificationBinding.txtMobileNo.text = phoneNumber

        // Set up OTP input fields
        activityOtpVerificationBinding.apply {
            otpDigit1.addTextChangedListener(EditTextWatcher(otpDigit1, otpDigit2))
            otpDigit2.addTextChangedListener(EditTextWatcher(otpDigit2, otpDigit3))
            otpDigit3.addTextChangedListener(EditTextWatcher(otpDigit3, otpDigit4))
            otpDigit4.addTextChangedListener(EditTextWatcher(otpDigit4, otpDigit5))
            otpDigit5.addTextChangedListener(EditTextWatcher(otpDigit5, otpDigit6))
            otpDigit6.addTextChangedListener(EditTextWatcher(otpDigit6, null))
        }
    }

    private fun onClick() {
        activityOtpVerificationBinding.apply {
            verifyOtpBtn.setOnClickListener {
                val typedOtp = (otpDigit1.text.toString() + otpDigit2.text.toString() +
                        otpDigit3.text.toString() + otpDigit4.text.toString() +
                        otpDigit5.text.toString() + otpDigit6.text.toString())

                if (typedOtp.isNotEmpty() && typedOtp.length == 6) {
                    progressDialog.setMessage("Verifying OTP...")
                    progressDialog.show()

                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(OTP, typedOtp)
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(
                        this@OtpVerificationActivity,
                        "Please enter a valid 6-digit OTP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Resend OTP button click
            activityOtpVerificationBinding.txtResendOtp.setOnClickListener {
                resendVerificationCode()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                progressDialog.dismiss() // Hide progress dialog

                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()

                    // ✅ Call API to register the user in backend
                    registerUserInBackend(name, phoneNumber)

                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    val errorMessage = if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        "${task.exception?.message}"
                    } else {
                        "${task.exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun registerUserInBackend(name: String, phoneNumber: String) {
        progressDialog.setMessage("Registering user...")
        progressDialog.show()

        val request = UserRequest(name = name, phoneNumber = phoneNumber)

        viewmodel.createUser(request)

        viewmodel.createUserResult.observe(this, Observer { result ->
            progressDialog.dismiss()
            when (result) {
                is Resource.Success -> {
                    Log.d("TAG", "User registered successfully")
                    saveUserDetails(result.data.data.userId, result.data.data.name, result.data.data.phoneNumber,result.data.data.totalRooms)
                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(
                        this@OtpVerificationActivity,
                        if (result.data.data.totalRooms == 0) SellRentPropertyForm::class.java
                        else OwnerDashBoard::class.java
                    ).apply {
                        putExtra("phoneNumber", phoneNumber)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    Log.e("TAG", "Error: ${result.message}")
                    Toast.makeText(this, "Failed to register user: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    progressDialog.setMessage("Registering user...")
                    progressDialog.show()
                }
            }
        })
    }

    private fun saveUserDetails(user_id: Int, name: String, phoneNumber: String, totalRooms: Int) {
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.putString("phoneNumber", phoneNumber)
        editor.putInt("user_id", user_id)
        editor.putInt("total_rooms",totalRooms)
        editor.apply()
    }




    private fun resendVerificationCode() {
        progressDialog.setMessage("Resending OTP...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to resend OTP to
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
            .setActivity(this) // Activity for callback binding
            .setCallbacks(callbacks) // Callbacks for verification state changes
            .setForceResendingToken(resendToken) // Resend token from previous attempt
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted: $credential")
            progressDialog.dismiss()
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            progressDialog.dismiss()
            Log.w("TAG", "onVerificationFailed", e)
            val errorMessage = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "Invalid phone number format."
                is FirebaseTooManyRequestsException -> "Too many requests. Please try again later."
                else -> "OTP verification failed. Please try again."
            }
            Toast.makeText(this@OtpVerificationActivity, errorMessage, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            progressDialog.dismiss()
            Log.d("TAG", "onCodeSent: $verificationId")
            Toast.makeText(this@OtpVerificationActivity, "OTP resent successfully", Toast.LENGTH_SHORT).show()

            // Update the OTP and resend token
            OTP = verificationId
            resendToken = token
        }
    }

    inner class EditTextWatcher(
        private val currentView: View,
        private val nextView: View?
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            if (text.length == 1) {
                // Move to next view if available
                nextView?.requestFocus()
            } else if (text.isEmpty()) {
                // Move to previous view if backspace is pressed
                when (currentView.id) {
                    R.id.otpDigit2 -> activityOtpVerificationBinding.otpDigit1.requestFocus()
                    R.id.otpDigit3 -> activityOtpVerificationBinding.otpDigit2.requestFocus()
                    R.id.otpDigit4 -> activityOtpVerificationBinding.otpDigit3.requestFocus()
                    R.id.otpDigit5 -> activityOtpVerificationBinding.otpDigit4.requestFocus()
                    R.id.otpDigit6 -> activityOtpVerificationBinding.otpDigit5.requestFocus()
                }
            }
        }
    }
}