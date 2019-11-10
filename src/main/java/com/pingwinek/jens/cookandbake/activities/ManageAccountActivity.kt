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

            AuthService.getInstance(application).logout { code, response ->
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
    }

    override fun onResume() {
        super.onResume()
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

        val emailText = if (authService.isLoggedIn()) {
            resources.getString(R.string.logged_in_as, authService.getStoredAccount()?.getEmail())
        } else if (authService.hasStoredAccount()) {
            resources.getString(R.string.not_logged_in_as, authService.getStoredAccount()?.getEmail())
        } else {
            resources.getString(R.string.no_account)
        }

        findViewById<TextView>(R.id.maEmailView).text = emailText
    }
}
