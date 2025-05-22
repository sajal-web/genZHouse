package com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.application.genzhouse.R
import com.application.genzhouse.databinding.ActivityEditPropertyBinding
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.fragments.PropertyDetailsFragment
import com.application.genzhouse.ui.welcome.sellrentproperty.views.dashboard.managerooms.editproperty.fragments.UpdatePropertyFragment

class EditPropertyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPropertyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Only add fragment if this is the first time opening the activity
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UpdatePropertyFragment())
                .commit()
        }

    }

}