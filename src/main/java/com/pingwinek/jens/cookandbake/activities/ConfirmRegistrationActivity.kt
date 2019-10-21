package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.widget.TextView
import com.pingwinek.jens.cookandbake.*

class ConfirmRegistrationActivity : BaseActivity() {

    private lateinit var messageField: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_confirm_registration)

        messageField = findViewById(R.id.craMessage)

        when (intent?.action) {
            ACTION_REGISTER_CONFIRMATION_SENT -> {
                messageField.text = resources.getString(R.string.confirmationSent)
            }
            ACTION_LOGIN_CONFIRMATION_SENT -> {
                messageField.text = resources.getString(R.string.confirmationSent)
            }
            else -> {
                retrieveTempCode()?.let { tempCode ->
                    confirmTempCode(tempCode)
                }
            }
        }

    }

    override fun getOptionsMenu(): OptionMenu {
        return OptionMenu().apply {
            addMenuEntry(OPTION_MENU_CLOSE, resources.getString(R.string.close)) {
                finish()
                true
            }.apply {
                iconId = R.drawable.ic_action_close
                ifRoom = true
            }
        }
    }

    private fun retrieveTempCode() : String? {
        var tempCode: String? = null
        val segments = intent?.data?.pathSegments
        if (segments != null && segments.contains("temp_code")) {
            tempCode = segments.last()
        }
        return tempCode
    }

    private fun confirmTempCode(tempCode: String) {
        AuthService.getInstance(application).confirmRegistration(tempCode) { code, _ ->
             when (code) {
                 200 -> {
                    messageField.text = resources.getString(R.string.confirmationSucceeded)
                 }
                 else -> {
                     messageField.text = resources.getString(R.string.confirmationFailed)
                 }
             }
        }
    }
}