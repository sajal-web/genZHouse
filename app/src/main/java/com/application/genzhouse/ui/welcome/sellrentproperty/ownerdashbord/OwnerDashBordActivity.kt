package com.application.genzhouse.ui.welcome.sellrentproperty.ownerdashbord

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.application.genzhouse.R

class OwnerDashBordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_owner_dash_bord)
    }
}