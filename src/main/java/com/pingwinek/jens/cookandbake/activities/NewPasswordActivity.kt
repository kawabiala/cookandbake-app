package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.R

class NewPasswordActivity : BaseActivity() {

    private var tempCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_new_password)

        val segments = intent.data.pathSegments
        if (segments.contains("temp_code")) {
            tempCode = segments.last()
        }
    }

    fun onNewPasswordButton(view: View) {
        tempCode?.let { AuthService.getInstance(application).newPassword(it, findViewById<TextView>(R.id.newPassword).text.toString()) }
    }
 }