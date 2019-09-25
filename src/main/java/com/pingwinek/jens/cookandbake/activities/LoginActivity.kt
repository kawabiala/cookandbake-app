package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.R

class LoginActivity : BaseActivity() {

    private lateinit var emailView: TextView
    private lateinit var passwordView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_login)

        emailView = findViewById(R.id.email)
        passwordView = findViewById(R.id.newPassword)
    }

    fun loginButton(view: View) {
        AuthService.getInstance(application).login(emailView.text.toString(), passwordView.text.toString())
    }

    override fun onLogin(intent: Intent) {
        finish()
//        startActivity(Intent(this, RecipeListingActivity::class.java))
    }

    override fun onLogout(intent: Intent) {
        // do nothing
    }

}
