package com.application.genzhouse.ui.loginregistration

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.application.genzhouse.data.remote.model.UserRequest
import com.application.genzhouse.databinding.ActivitySignupBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.addproperty.SellRentPropertyForm
import com.application.genzhouse.utils.CustomProgressDialog
import com.application.genzhouse.utils.Resource
import com.application.genzhouse.viewmodel.CreateUserViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivitySignupBinding

    // Firebase Authentication
    private val auth = FirebaseAuth.getInstance()

    // ViewModel
    private lateinit var viewModel: CreateUserViewModel

    // UI Components
    private lateinit var progressDialog: CustomProgressDialog

    // Data fields
    private lateinit var phNumber: String
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews()
        initToolBar()
        setupViewModel()
        setupClickListeners()
        checkCurrentUser()
    }

    private fun initializeViews() {
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = CustomProgressDialog(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CreateUserViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.createUserResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data.statusCode == 200) {
                        AlertDialog.Builder(this)
                            .setTitle("Account Already Exists!")
                            .setMessage(result.data.message)
                            .setCancelable(false)
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                navigateToLogin()
                            }
                            .show()
                        progressDialog.dismiss()
                    } else {
                        sendOtp(
                            "${
                                binding.countryCodeButton.text.toString().trim()
                            }${binding.phoneNumberInput.text.toString().trim()}"
                        )
                    }

                }

                is Resource.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> progressDialog.show()
            }
        }
    }


    private fun setupClickListeners() {
        binding.apply {
            sendOtpBtn.setOnClickListener { validateAndProceed() }
            loginRedirectText.setOnClickListener { navigateToLogin() }
        }
    }

    private fun validateAndProceed() {
        val number = binding.phoneNumberInput.text.toString().trim()
        name = binding.nameInput.text.toString().trim()

        when {
            name.isEmpty() -> showToast("Please enter your name!")
            number.isEmpty() || number.length != 10 -> showToast("Please enter a valid 10-digit phone number!")
            else -> viewModel.checkUser(UserRequest(name = name, phoneNumber = "+91$number"))
        }
    }

    private fun sendOtp(phoneNumber: String) {
        progressDialog.setMessage("Sending OTP...")
        phNumber = phoneNumber
        auth.useAppLanguage()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(verificationCallbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val verificationCallbacks = object : OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted: $credential")
            progressDialog.dismiss()
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            progressDialog.dismiss()
            Log.w("TAG", "onVerificationFailed", e)
            showToast(getErrorMessage(e))
        }

        override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
            progressDialog.dismiss()
            Log.d("TAG", "onCodeSent: $verificationId")
            showToast("OTP sent successfully")
            navigateToOtpVerification(verificationId, token)
        }
    }

    private fun getErrorMessage(e: FirebaseException): String = when (e) {
        is FirebaseAuthInvalidCredentialsException -> e.message ?: "Invalid credentials"
        is FirebaseTooManyRequestsException -> e.message ?: "Too many requests"
        is FirebaseAuthMissingActivityForRecaptchaException -> e.message ?: "Recaptcha error"
        else -> e.message ?: "Verification failed"
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("TAG", "signInWithCredential:success")
                showToast("Authentication successful")
                navigateToPropertyForm()
            } else {
                Log.w("TAG", "signInWithCredential:failure", task.exception)
                showToast(getAuthErrorMessage(task.exception))
            }
        }
    }

    private fun getAuthErrorMessage(exception: Exception?): String = when (exception) {
        is FirebaseAuthInvalidCredentialsException -> "Invalid Credential. Please try again."
        else -> "Authentication failed. Please try again."
    }

    private fun navigateToOtpVerification(verificationId: String, token: ForceResendingToken) {
        Intent(this, OtpVerificationActivity::class.java).apply {
            putExtra("OTP", verificationId)
            putExtra("resendToken", token)
            putExtra("phoneNumber", phNumber)
            putExtra("name", name)
            startActivity(this)
        }
    }

    private fun navigateToPropertyForm() {
        startActivity(Intent(this, SellRentPropertyForm::class.java))
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun checkCurrentUser() {
        if (auth.currentUser != null) {
            navigateToPropertyForm()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initToolBar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    override fun onStart() {
        super.onStart()
        checkCurrentUser()
    }
}