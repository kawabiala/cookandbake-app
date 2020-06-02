package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R

class ChangePasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_change_password)

        fillEmail()

        optionMenu.apply {
            addMenuEntry(
                R.id.OPTION_MENU_CLOSE,
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

        val authService = (application as PingwinekCooksApplication).getServiceLocator()
            .getService(AuthService::class.java)
        authService.changePassword(
            findViewById<TextView>(R.id.cpaOldPassword).text.toString(),
            findViewById<TextView>(R.id.cpaNewPassword).text.toString()
        ) { code, _ ->
            when (code) {
                200 -> {
                    setMessage(getString(R.string.PasswordChanged))
                    authService.logout { _, _ ->
                        fillEmail()
                    }
                }
                else -> {
                    setMessage(getString(R.string.changePasswordFailed))
                }
            }
        }
    }

    private fun fillEmail() {
        val authService = (application as PingwinekCooksApplication).getServiceLocator()
            .getService(AuthService::class.java)
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