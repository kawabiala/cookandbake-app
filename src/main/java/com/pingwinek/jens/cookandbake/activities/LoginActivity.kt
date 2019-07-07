package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRouter

class LoginActivity : BaseActivity() {

    private lateinit var emailView: TextView
    private lateinit var passwordView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_login)

        emailView = findViewById(R.id.email)
        passwordView = findViewById(R.id.password)
    }

    fun loginButton(view: View) {
        val method = NetworkRequest.Method.POST
        val contentType = NetworkRequest.ContentType.APPLICATION_URLENCODED
        val params = HashMap<String, String>()
        params["email"] = emailView.text.toString()
        params["password"] = passwordView.text.toString()

        val networkRequest = NetworkRequest.getInstance(application)
        val networkResponseRouter = NetworkResponseRouter()
        networkResponseRouter.registerSuccessRoute(200) {
            startActivity(Intent(this, RecipeListingActivity::class.java))
        }

        networkRequest.runRequest(loginPath, method, contentType, params,
            networkResponseRouter
        )
    }

}
