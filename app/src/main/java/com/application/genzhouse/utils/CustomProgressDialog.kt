package com.application.genzhouse.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Window
import android.widget.TextView
import com.application.genzhouse.R
import androidx.core.graphics.drawable.toDrawable

class CustomProgressDialog(context: Context) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE) // Remove the default title
        setContentView(R.layout.progress_dialog)
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable()) // Transparent background
        setCancelable(false) // Prevent dismissal by tapping outside
    }

    fun setMessage(message: String) {
        findViewById<TextView>(R.id.tvMessage).text = message
    }
}