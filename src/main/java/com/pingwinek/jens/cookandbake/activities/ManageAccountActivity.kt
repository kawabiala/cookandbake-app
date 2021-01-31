package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel
import kotlinx.coroutines.*

class ManageAccountActivity : BaseActivity(), RequirePasswordFragment.RequirePasswordListener {

    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_manage_account)

        authenticationViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        ).get(AuthenticationViewModel::class.java)

        authenticationViewModel.response.observe(this) { response ->
            when (response.action) {
                AuthService.AuthenticationAction.LOGOUT -> {
                    fillEmail()
                    if (response.code != 200) {
                        alert(resources.getString(R.string.logoutFailed))
                    }
                }
                AuthService.AuthenticationAction.UNSUBSCRIBE -> {
                    if (response.code == 200) {
                        fillEmail()
                    } else {
                        alert(response.msg ?: "")
                    }
                }
                else -> {}
            }
        }

        findViewById<TextView>(R.id.maUnsubscribeView).setOnClickListener {
            if (! authenticationViewModel.hasStoredAccount()) {
                return@setOnClickListener
            }

            val requirePasswordFragment = RequirePasswordFragment()
            requirePasswordFragment.arguments = Bundle().apply {
                putString("message", getString(R.string.confirmUnsubscribe))
            }
            requirePasswordFragment.show(supportFragmentManager, "requirePasswordManager")
        }

        findViewById<TextView>(R.id.maChangePasswordView).setOnClickListener {
            if (! authenticationViewModel.hasStoredAccount()) {
                return@setOnClickListener
            }

            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        findViewById<TextView>(R.id.maLoginView).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<TextView>(R.id.maLogoutView).setOnClickListener {
            if (! authenticationViewModel.hasStoredAccount()) {
                return@setOnClickListener
            }

            authenticationViewModel.logout()
        }

        findViewById<TextView>(R.id.maImpressumView).setOnClickListener {
            startActivity(Intent(this, ImpressumActivity::class.java)
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)))
        }

        findViewById<TextView>(R.id.maDatenschutzView).setOnClickListener {
            startActivity(Intent(this, ImpressumActivity::class.java)
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)))
        }

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

    override fun onResume() {
        super.onResume()
        fillEmail()
    }

    override fun onPositiveButton(password: String?) {
        if (password == null) return

        authenticationViewModel.unsubscribe(password)
    }

    override fun onNegativeButton() {
        // Do nothing
    }

    private fun alert(msg: String) {
        AlertDialog.Builder(this).apply {
            setMessage(msg)
            setPositiveButton("Ok") { _, _ ->
                // do nothing
            }
        }
    }

    private fun fillEmail() {
        val emailText = when {
            authenticationViewModel.isLoggedIn() -> {
                resources.getString(R.string.is_logged_in, authenticationViewModel.getStoredAccount()?.getEmail())
            }
            authenticationViewModel.hasStoredAccount() -> {
                resources.getString(R.string.is_not_logged_in, authenticationViewModel.getStoredAccount()?.getEmail())
            }
            else -> {
                resources.getString(R.string.no_account)
            }
        }

        findViewById<TextView>(R.id.maEmailView).text = emailText
    }
}
