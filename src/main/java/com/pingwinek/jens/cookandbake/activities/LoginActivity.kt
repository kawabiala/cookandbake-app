package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.Menu.NONE
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.*

class LoginActivity : BaseActivity() {

    private lateinit var emailView: TextView
    private lateinit var passwordView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_login)

        emailView = findViewById(R.id.laEmail)
        passwordView = findViewById(R.id.laPassword)

        doConfirm()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        doConfirm()
    }

    override fun getOptionsMenu() : OptionMenu {
        return OptionMenu().apply {
            addMenuEntry(OPTION_MENU_REGISTER, resources.getString(R.string.register)) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                true
            }
            addMenuEntry(OPTION_MENU_LOST_PASSWORD, resources.getString(R.string.lostPassword)) {
                startActivity(Intent(this@LoginActivity, LostPasswordActivity::class.java))
                true
            }
            addMenuEntry(OPTION_MENU_IMPRESSUM, resources.getString(R.string.impressum)) {
                startActivity(Intent(this@LoginActivity, ImpressumActivity::class.java)
                    .putExtra("url", resources.getString(R.string.impressum_url)))
                true
            }
            addMenuEntry(OPTION_MENU_DATAPROTECTION, resources.getString(R.string.dataprotection)) {
                startActivity(Intent(this@LoginActivity, ImpressumActivity::class.java)
                    .putExtra("url", resources.getString(R.string.dataprotection_url)))
                true
            }
            addMenuEntry(OPTION_MENU_MANAGE_ACCOUNT, resources.getString(R.string.close)) {
                finish()
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        if (menu?.findItem(OPTION_MENU_LOST_PASSWORD) == null) {
            menu?.add(NONE, OPTION_MENU_LOST_PASSWORD, NONE, "Passwort vergessen")
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == OPTION_MENU_LOST_PASSWORD) {
            startActivity(Intent(this, LostPasswordActivity::class.java))
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    fun loginButton(view: View) {
        deleteMessage()

        AuthService.getInstance(application).login(emailView.text.toString(), passwordView.text.toString()){ code, _ ->
            when (code) {
                200 -> {
                    startActivity(Intent(this, RecipeListingActivity::class.java))
                    finish()
                }
                206 -> {
                    setMessage(resources.getString(R.string.confirmationSent))
                }
                else -> {
                    setMessage(resources.getString(R.string.loginFailed))
                }
            }
        }
    }

    private fun deleteMessage() {
        findViewById<TextView>(R.id.laMessageView).apply {
            text = null
            visibility = View.INVISIBLE
        }
    }

    private fun setMessage(message: String) {
        findViewById<TextView>(R.id.laMessageView).apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun doConfirm() {
        retrieveTempCode()?.let { tempCode ->
            confirmTempCode(tempCode)
        }
    }

    private fun retrieveTempCode() : String? {
        var tempCode: String? = null
        val segments = intent?.data?.pathSegments
        if (segments != null && segments.contains("temp_code")) {
            tempCode = segments.last()
        }
        return tempCode
    }

    private fun confirmTempCode(tempCode: String) {
        AuthService.getInstance(application).confirmRegistration(tempCode) { code, _ ->
            when (code) {
                200 -> {
                    setMessage(resources.getString(R.string.confirmationSucceeded))
                }
                else -> {
                    setMessage(resources.getString(R.string.confirmationFailed))
                }
            }
        }
    }
}
