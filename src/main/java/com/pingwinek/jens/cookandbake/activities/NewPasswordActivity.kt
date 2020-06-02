package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R

class NewPasswordActivity : BaseActivity() {

    private var tempCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_new_password)

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
        val authService = (application as PingwinekCooksApplication).getServiceLocator()
            .getService(AuthService::class.java)

        tempCode?.let {
            authService.newPassword(it, findViewById<TextView>(R.id.npaPassword).text.toString()) { code, _ ->
                when (code) {
                    200 -> {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    else -> {
                        setMessage(resources.getString(R.string.lostPasswordFailed))
                    }
                }
            }
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
}