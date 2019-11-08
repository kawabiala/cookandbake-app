package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
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

    fun onNewPasswordButton(view: View) {
        deleteMessage()

        AuthService.getInstance(application).changePassword(
            findViewById<TextView>(R.id.cpaOldPassword).text.toString(),
            findViewById<TextView>(R.id.cpaNewPassword).text.toString()
        ) { code, response ->
            when (code) {
                200 -> {
                    setMessage("Password geändert")
                    AuthService.getInstance(application).logout(){ code, response ->
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
            emailView.text = "Angemeldet als: ${authService.getStoredAccount()?.getEmail()}"
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