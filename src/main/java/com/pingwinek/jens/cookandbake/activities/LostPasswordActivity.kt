package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.OPTION_MENU_CLOSE
import com.pingwinek.jens.cookandbake.R

class LostPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_lost_password)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

    fun onLostPasswordButton(view: View) {
        deleteMessage()

        AuthService.getInstance(application).lostPassword(findViewById<TextView>(R.id.lpaEmail).text.toString()) { code, _ ->
            when (code) {
                200 -> {
                    setMessage(resources.getString(R.string.confirmationSent))
                }
                else -> {
                    setMessage(resources.getString(R.string.lostPasswordFailed))
                }
            }
        }
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