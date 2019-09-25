package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.R

class RegisterActivity : BaseActivity(), ConfirmDialogFragment.ConfirmDialogListener {

    private lateinit var emailView: TextView
    private lateinit var passwordView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_register)

        emailView = findViewById(R.id.email)
        passwordView = findViewById(R.id.newPassword)
    }

    fun registerButton(view: View) {
        if (AuthService.getInstance(application).isRemembered()) {
            val confirmDialogFragment = ConfirmDialogFragment()
            confirmDialogFragment.arguments = Bundle().also {
                it.putString("message", "Soll der aktuelle Benutzer ausgeloggt werden?")
                it.putString("id", "id")
            }
            confirmDialogFragment.show(supportFragmentManager, "tag")
        } else {
            register()
        }

    }

    override fun onLogout(intent: Intent) {
        // do nothing
    }

    override fun onPositiveButton(confirmItemId: String?) {
        AuthService.getInstance(application).logout() { _, _ ->
            register()
        }
    }

    override fun onNegativeButton(confirmItemId: String?) {
        // do nothing
    }

    private fun register() {
        AuthService.getInstance(application).register(emailView.text.toString(),passwordView.text.toString()) { code, response ->
            if (code == 200) {
                val message = "Registrierung erfolgreich. Es wurde eine Bestätigungsmail an die angegebene Email gesandt."
                startActivity(Intent(this, MessageActivity::class.java)
                    .putExtra("message", message))
                finish()
            } else {
                ErrorMessage().show(supportFragmentManager, "errorMessage")
            }
        }
    }

    class ErrorMessage : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Die Registrierung ist fehlgeschlagen")
                builder.setPositiveButton("Ok") { _, _ ->
                    // do nothing
                }
                builder.create()
            } ?: throw IllegalStateException("ErrorMessage cannot be built due to missing activity")
        }
    }
}
