package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
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

    fun onLostPasswordButton(view: View) {
        AuthService.getInstance(application).lostPassword(findViewById<TextView>(R.id.lpaPassword).text.toString()) { code, response ->
            when (code) {
                200 -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else -> {
                    AlertDialog.Builder(this).apply {
                        setMessage("Rücksetzung des Passworts fehlgeschlagen")
                        setPositiveButton("Ok") { _, _ ->
                            // do nothing
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }
 }