package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.pingwinek.jens.cookandbake.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.networkRequest.NetworkResponseRouter
import com.pingwinek.jens.cookandbake.R

class RegisterActivity : BaseActivity() {

    private lateinit var emailView: TextView
    private lateinit var passwordView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_register)

        emailView = findViewById(R.id.email)
        passwordView = findViewById(R.id.password)
    }

    fun registerButton(view: View) {
        Toast.makeText(this, "register", Toast.LENGTH_LONG).show()

        val method = NetworkRequest.Method.POST
        val contentType = NetworkRequest.ContentType.APPLICATION_URLENCODED
        val params = HashMap<String, String>()
        params["email"] = emailView.text.toString()
        params["password"] = passwordView.text.toString()

        Log.i("RegisterActivity", "email: " + params["email"] + " - password: " + params["password"])

        val networkRequest = NetworkRequest.getInstance(application)

        networkRequest.runRequest(registerPath, method, contentType, params,
            NetworkResponseRouter()
        )
    }

}
