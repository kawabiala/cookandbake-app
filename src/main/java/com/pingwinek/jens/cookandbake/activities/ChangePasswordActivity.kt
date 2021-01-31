package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel

class ChangePasswordActivity : BaseActivity() {

    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_change_password)

        authenticationViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        ).get(AuthenticationViewModel::class.java)

        authenticationViewModel.response.observe(this) { response ->
            when (response.action) {
                AuthService.AuthenticationAction.CHANGE_PASSWORD -> {
                    if (response.code == 200) {
                        setMessage(getString(R.string.PasswordChanged))
                        authenticationViewModel.logout()
                        fillEmail()
                    } else {
                        setMessage(getString(R.string.changePasswordFailed))
                    }
                }
                else -> {}
            }
        }

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
        authenticationViewModel.changePassword(
            findViewById<TextView>(R.id.cpaOldPassword).text.toString(),
            findViewById<TextView>(R.id.cpaNewPassword).text.toString()
        )
    }

    private fun fillEmail() {
        val emailView = findViewById<TextView>(R.id.cpaEmailView)
        if (authenticationViewModel.hasStoredAccount()) {
            emailView.text = getString(R.string.logged_in_as, authenticationViewModel.getStoredAccount()?.getEmail())
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