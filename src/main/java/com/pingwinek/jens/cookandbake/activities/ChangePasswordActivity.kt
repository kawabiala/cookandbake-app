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
        addContentView(R.layout.activity_new_password)
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
        AuthService.getInstance(application).changePassword(
            findViewById<TextView>(R.id.cpaOldPassword).text.toString(),
            findViewById<TextView>(R.id.cpaNewPassword).text.toString()
        ) { code, response ->
            when (code) {
                200 -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else -> {
                    AlertDialog.Builder(this).apply {
                        setMessage("Passwort konnte nicht gesetzt werden")
                        setPositiveButton("Ok") { _, _ ->
                            // do nothing
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }
 }