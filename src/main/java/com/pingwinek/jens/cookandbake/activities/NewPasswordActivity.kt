package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel

class NewPasswordActivity : BaseActivity() {

    private lateinit var authenticationViewModel: AuthenticationViewModel

    private var tempCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_new_password)

        authenticationViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        ).get(AuthenticationViewModel::class.java)

        authenticationViewModel.response.observe(this) { response ->
            when (response.action) {
                AuthService.AuthenticationAction.NEW_PASSWORD -> {
                    if (response.code == 200) {
                        goToLogin()
                    } else {
                        setMessage(resources.getString(R.string.lostPasswordFailed))
                    }
                }
                else -> {}
            }
        }

        val segments = intent?.data?.pathSegments
        if (segments != null && segments.contains("temp_code")) {
            tempCode = segments.last()
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

    fun onNewPasswordButton(view: View) {
        tempCode?.let {
            authenticationViewModel.newPassword(
                it,
                findViewById<TextView>(R.id.npaPassword).text.toString()
            )
        }
    }

    @Suppress("Unused")
    private fun deleteMessage() {
        findViewById<TextView>(R.id.npaMessageView).apply {
            text = null
            visibility = View.INVISIBLE
        }
    }

    private fun setMessage(message: String) {
        findViewById<TextView>(R.id.npaMessageView).apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}