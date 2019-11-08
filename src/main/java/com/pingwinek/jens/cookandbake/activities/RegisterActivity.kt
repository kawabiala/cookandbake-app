package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.ACTION_REGISTER_CONFIRMATION_SENT
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.OPTION_MENU_CLOSE
import com.pingwinek.jens.cookandbake.R

class RegisterActivity : BaseActivity() {

    private lateinit var emailView: TextView
    private lateinit var passwordView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_register)

        emailView = findViewById(R.id.raEmail)
        passwordView = findViewById(R.id.raPassword)
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

    fun registerButton(view: View) {
        deleteMessage()

        if (AuthService.getInstance(application).hasStoredAccount()) {
            AlertDialog.Builder(this).apply {
                setMessage("Soll der aktuelle Benutzer ausgeloggt werden?")
                setPositiveButton("Ja") { _, _ ->
                    AuthService.getInstance(application).logout { _, _ ->
                        register()
                    }
                }
                setNegativeButton("Nein") { _, _ -> }
                create()
                show()
            }
        } else {
            register()
        }

    }

    private fun register() {
        AuthService.getInstance(application).register(emailView.text.toString(),passwordView.text.toString()) { code, _ ->
            if (code == 200) {
                setMessage(resources.getString(R.string.confirmationSent))
                /*
                startActivity(Intent(this, ConfirmRegistrationActivity::class.java).apply {
                    action = ACTION_REGISTER_CONFIRMATION_SENT
                })
                finish()

                 */
            } else {
                setMessage(resources.getString(R.string.registrationFailed))
                /*
                AlertDialog.Builder(this).apply {
                    setMessage("Die Registrierung ist fehlgeschlagen")
                    setPositiveButton("Ok") { _, _ -> }
                    create()
                    show()
                }

                 */
            }
        }
    }

    private fun deleteMessage() {
        findViewById<TextView>(R.id.raMessageView).apply {
            text = null
            visibility = View.INVISIBLE
        }
    }

    private fun setMessage(message: String) {
        findViewById<TextView>(R.id.raMessageView).apply {
            text = message
            visibility = View.VISIBLE
        }
    }
}
