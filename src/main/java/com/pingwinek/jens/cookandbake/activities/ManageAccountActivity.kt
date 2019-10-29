package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.pingwinek.jens.cookandbake.Account
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.OPTION_MENU_CLOSE
import com.pingwinek.jens.cookandbake.R
import kotlinx.android.synthetic.main.dialog_view_password.view.*
import org.w3c.dom.Text

class ManageAccountActivity : BaseActivity(), RequirePasswordFragment.RequirePasswordListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_manage_account)

        if (AuthService.getInstance(application).hasStoredAccount()) {
            findViewById<TextView>(R.id.maEmailView).text = AuthService.getInstance(application).getStoredAccount()?.getEmail()
        }

        findViewById<TextView>(R.id.maUnsubscribeView).setOnClickListener() {
            val requirePasswordFragment = RequirePasswordFragment()
            requirePasswordFragment.arguments = Bundle().apply {
                putString("message", "Konto wirklich löschen?")
            }
            requirePasswordFragment.show(supportFragmentManager, "requirePasswordManager")
        }
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
                    200 -> Toast.makeText(this, "Konto erfolgreich gelöscht", Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onNegativeButton() {
        // Do nothing
    }
}
