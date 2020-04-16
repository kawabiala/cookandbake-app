package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.pingwinek.jens.cookandbake.*

class ManageAccountActivity : BaseActivity(), RequirePasswordFragment.RequirePasswordListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_manage_account)

        findViewById<TextView>(R.id.maUnsubscribeView).setOnClickListener {
            if (! AuthService.getInstance(application).hasStoredAccount()) {
                return@setOnClickListener
            }

            val requirePasswordFragment = RequirePasswordFragment()
            requirePasswordFragment.arguments = Bundle().apply {
                putString("message", "Konto wirklich löschen?")
            }
            requirePasswordFragment.show(supportFragmentManager, "requirePasswordManager")
        }

        findViewById<TextView>(R.id.maChangePasswordView).setOnClickListener {
            if (! AuthService.getInstance(application).hasStoredAccount()) {
                return@setOnClickListener
            }

            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        findViewById<TextView>(R.id.maLoginView).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<TextView>(R.id.maLogoutView).setOnClickListener {
            if (! AuthService.getInstance(application).hasStoredAccount()) {
                return@setOnClickListener
            }

            AuthService.getInstance(application).logout { code, _ ->
                fillEmail()
                if (code != 200) {
                    AlertDialog.Builder(this).apply {
                        setMessage(resources.getString(R.string.logoutFailed))
                        setPositiveButton("Ok") { _, _ ->
                            // do nothing
                        }
                    }
                }
            }
        }

        findViewById<TextView>(R.id.maImpressumView).setOnClickListener {
            startActivity(Intent(this, ImpressumActivity::class.java)
                .putExtra("url", IMPRESSUMPATH))
        }

        findViewById<TextView>(R.id.maDatenschutzView).setOnClickListener {
            startActivity(Intent(this, ImpressumActivity::class.java)
                .putExtra("url", DATAPROTECTIONPATH))
        }

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

    override fun onResume() {
        super.onResume()
        fillEmail()
    }

    override fun onPositiveButton(password: String?) {
        password?.let {
            AuthService.getInstance(application).unsubscribe(password) { code, response ->
                when (code) {
                    200 -> fillEmail()
                    else -> Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onNegativeButton() {
        // Do nothing
    }

    private fun fillEmail() {
        val authService = AuthService.getInstance(application)

        val emailText = when {
            authService.isLoggedIn() -> {
                resources.getString(R.string.is_logged_in, authService.getStoredAccount()?.getEmail())
            }
            authService.hasStoredAccount() -> {
                resources.getString(R.string.is_not_logged_in, authService.getStoredAccount()?.getEmail())
            }
            else -> {
                resources.getString(R.string.no_account)
            }
        }

        findViewById<TextView>(R.id.maEmailView).text = emailText
    }
}
