package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel

class LostPasswordActivity : BaseActivity() {

    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_lost_password)

        authenticationViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        ).get(AuthenticationViewModel::class.java)

        authenticationViewModel.response.observe(this) { response ->
            when (response.action) {
                AuthService.AuthenticationAction.LOST_PASSWORD -> {
                    if (response.code == 200) {
                        setMessage(resources.getString(R.string.confirmationSent))
                    } else {
                        setMessage(resources.getString(R.string.lostPasswordFailed))
                    }
                }
                else -> {}
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

    fun onLostPasswordButton(view: View) {
        deleteMessage()
        authenticationViewModel.lostPassword(findViewById<TextView>(R.id.lpaEmail).text.toString())
    }

    private fun deleteMessage() {
        findViewById<TextView>(R.id.lpaMessageView).apply {
            text = null
            visibility = View.INVISIBLE
        }
    }

    private fun setMessage(message: String) {
        findViewById<TextView>(R.id.lpaMessageView).apply {
            text = message
            visibility = View.VISIBLE
        }
    }
}