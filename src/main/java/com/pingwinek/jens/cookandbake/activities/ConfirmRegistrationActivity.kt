package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.R

class ConfirmRegistrationActivity : BaseActivity() {

    private var tempCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_confirm_registration)

        val segments = intent?.data?.pathSegments
        if (segments != null && segments.contains("temp_code")) {
            tempCode = segments.last()
        }
    }

    override fun onResume() {
        super.onResume()
        tempCode?.let { AuthService.getInstance(application).confirmRegistration(it) }
    }

}