package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.ownerprofile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.application.genzhouse.MyApp
import com.application.genzhouse.R
import com.application.genzhouse.WelcomeActivity
import com.application.genzhouse.databinding.ActivityProfileBinding
import com.application.genzhouse.ui.loginregistration.CustomProgressDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var progressDialog: CustomProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = CustomProgressDialog(this@ProfileActivity)
        initToolbar()
        getUserDetails()
        initUi()
        initOnClick()

    }

    private fun initOnClick() {
        binding.apply {
            logoutCard.setOnClickListener {
                progressDialog.show()
                // Get the SharedPreferences directory
                val sharedPrefsDir = File(this@ProfileActivity.applicationInfo.dataDir, "shared_prefs")

                // If the directory exists, delete all XML files inside it
                if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory) {
                    sharedPrefsDir.listFiles()?.forEach { file ->
                        if (file.name.endsWith(".xml")) {
                            file.delete()
                        }
                    }
                }
                MyApp.get(this@ProfileActivity).logout()
                progressDialog.hide()
                Log.d("LaunchDebug", "Before startActivity")
                startActivity(Intent(this@ProfileActivity, WelcomeActivity::class.java))
                Log.d("LaunchDebug", "After startActivity")
                finish()
            }
        }
    }

    private fun initUi() {
        binding.apply {
            tvUserName.text = getUserDetails().third.toString()
            tvPhoneNumber.text = getUserDetails().second.toString()
            tvJoinedDate.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                .format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    .apply { timeZone = TimeZone.getTimeZone("UTC") }
                    .parse(getUserDetails().first)!!)
        }
    }

    private fun getUserDetails(): Triple<String?, String?, String?> {
        val sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "----------")
        val phoneNumber = sharedPreferences.getString("phoneNumber", "----------")
        val joinedDate = sharedPreferences.getString("joinedDate","----------")
        return Triple(joinedDate,phoneNumber,name)
    }
    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}