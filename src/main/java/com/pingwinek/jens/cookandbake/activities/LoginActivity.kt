package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
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
    }

    override fun getOptionsMenu() : OptionMenu {
        return OptionMenu().apply {
            addMenuEntry(OPTION_MENU_REGISTER, "Anmelden") {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                true
            }
            addMenuEntry(OPTION_MENU_LOST_PASSWORD, "Passwort vergessen") {
                startActivity(Intent(this@LoginActivity, LostPasswordActivity::class.java))
                    true
            }
            addMenuEntry(OPTION_MENU_RECIPES, "Rezepte") {
                startActivity(Intent(this@LoginActivity, RecipeListingActivity::class.java))
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
        AuthService.getInstance(application).login(emailView.text.toString(), passwordView.text.toString()){ code, _ ->
            when (code) {
                200 -> {
                    startActivity(Intent(this, RecipeListingActivity::class.java))
                    finish()
                }
                206 -> {
                    startActivity(Intent(this, ConfirmRegistrationActivity::class.java).apply {
                        action =ACTION_LOGIN_CONFIRMATION_SENT
                    })
                }
                else -> {
                    AlertDialog.Builder(this).apply {
                        setMessage("Login fehlgeschlagen")
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
