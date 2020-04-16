package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.OPTION_MENU_CLOSE
import com.pingwinek.jens.cookandbake.R

class ChangePasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_change_password)

        fillEmail()

        optionMenu.apply {
            addMenuEntry(
                OPTION_MENU_CLOSE,
                resources.getString(R.string.close),
                R.drawable.ic_action_close_black,
                true
            ) {
                finish()
                true
            }
        }
    }

    fun onNewPasswordButton(view: View) {
        deleteMessage()

        AuthService.getInstance(application).changePassword(
            findViewById<TextView>(R.id.cpaOldPassword).text.toString(),
            findViewById<TextView>(R.id.cpaNewPassword).text.toString()
        ) { code, _ ->
            when (code) {
                200 -> {
                    setMessage("Password geändert")
                    AuthService.getInstance(application).logout { _, _ ->
                        fillEmail()
                    }
                }
                else -> {
                    setMessage("Password konnte nicht geändert werden")
                }
            }
        }
    }

    private fun fillEmail() {
        val authService = AuthService.getInstance(application)
        val emailView = findViewById<TextView>(R.id.cpaEmailView)
        if (authService.hasStoredAccount()) {
            emailView.text = getString(R.string.logged_in_as, authService.getStoredAccount()?.getEmail())
        } else {
            emailView.text = resources.getString(R.string.no_account)
        }
    }

    private fun setMessage(message: String) {
        findViewById<TextView>(R.id.cpaMessageView).apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun deleteMessage() {
        findViewById<TextView>(R.id.cpaMessageView).apply {
            text = null
            visibility = View.INVISIBLE
        }
    }
}