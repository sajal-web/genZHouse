package com.application.genzhouse.ui.loginregistration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.application.genzhouse.data.model.UserRequest
import com.application.genzhouse.databinding.ActivityLoginBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.SellRentProperty
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.viewmodel.CreateUserViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var activityLoginBinding: ActivityLoginBinding
    private lateinit var phNumber: String
    private lateinit var name : String
    private lateinit var viewModel: CreateUserViewModel
    private lateinit var progressDialog: CustomProgressDialog
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding.root)
        viewModel = ViewModelProvider(this)[CreateUserViewModel::class.java]
        // Initialize the custom progress dialog
        progressDialog = CustomProgressDialog(this)

        initUI()
        onClick()

        viewModel.createUserResult.observe(this) { result ->
            when(result) {
                is Resource.Loading -> {
                    progressDialog.show()
                }
                is Resource.Success -> {
                }
                is Resource.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun initUI() {
        // Any additional UI initialization
    }

    private fun createUserRequest() : UserRequest {
        val number = activityLoginBinding.phoneNumberInput.text.toString().trim()
        return UserRequest(userType = "",name = name, phoneNumber = number)

    }

    private fun onClick() {
        activityLoginBinding.sendOtpBtn.setOnClickListener {
            val number = activityLoginBinding.phoneNumberInput.text.toString().trim()
            name = activityLoginBinding.nameInput.text.toString().trim()
            if (number.isNotEmpty() && number.length == 10 && name.isNotEmpty()) {
                progressDialog.setMessage("Sending OTP...")
                progressDialog.show()
                sendOtp("+91$number")
                viewModel.createUser(createUserRequest())
            } else {
                Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOtp(phoneNumber: String) {
        auth.useAppLanguage()
        phNumber = phoneNumber
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted: $credential")
            progressDialog.dismiss()
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            progressDialog.dismiss()
            Log.w("TAG", "onVerificationFailed", e)
            val errorMessage = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "${e.message}"
                is FirebaseTooManyRequestsException -> "${e.message}"
                is FirebaseAuthMissingActivityForRecaptchaException -> "${e.message}"
                else -> "${e.message}"
            }
            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
            progressDialog.dismiss()
            Log.d("TAG", "onCodeSent: $verificationId")
            Toast.makeText(this@LoginActivity, "OTP sent successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@LoginActivity, OtpVerificationActivity::class.java).apply {
                putExtra("OTP", verificationId)
                putExtra("resendToken", token)
                putExtra("phoneNumber", phNumber)
                putExtra("name",name )
            }
            startActivity(intent)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SellRentProperty::class.java))
                    finish()
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    val errorMessage = if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        "Invalid Credential. Please try again."
                    } else {
                        "Authentication failed. Please try again."
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, SellRentProperty::class.java))
            finish()
        }
    }
}