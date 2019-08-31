package com.pingwinek.jens.cookandbake.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.TextView
import com.pingwinek.jens.cookandbake.viewModels.ConfigurationViewModel
import com.pingwinek.jens.cookandbake.R

class MainActivity : BaseActivity() {

/*
    protected val domain = "https://www.pingwinek.de"
    protected val baseUrl = "$domain/cookandbake"
    protected val authPath = "$baseUrl/authenticate"
*/
    private lateinit var message: TextView
    private lateinit var configuration: ConfigurationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addContentView(R.layout.activity_main)

        message = findViewById(R.id.message)

        configuration = ViewModelProviders.of(this).get(ConfigurationViewModel::class.java)
        configuration.uid.observe(this, Observer {
            uid ->
            message.apply {
                text = uid
            }
        })

        message.apply {
            //text = authentication.getAccount()?.idToken ?: "null Token"
            //text = gso.toString();
        }
    }
/*
    override fun onLogin(intent: Intent) {
        super.onLogin(intent)
        message.apply {
            text = authentication.getAccount()?.idToken ?: "null Token"
        }
    }

    override fun onLogout(intent: Intent) {
        super.onLogout(intent)
        message.apply {
            text = authentication.getAccount()?.idToken ?: "null Token"
        }
    }

    private fun authenticate() {

        val method = "POST"
        val params = HashMap<String, String>()
        params["id_token"] = "null Token"
        val responseHandler = ResponseHandler { response -> configuration.uid.value = response }

        val networkRequest = NetworkRequest.getInstance(application)

        networkRequest.runRequest(authPath, method, params) { status, code, response ->
            Log.i("requestButton", "Callback - status: " + status + " code: " + code.toString() + " response: " + response)
            val msg = responseHandler.obtainMessage()
            msg.obj = response
            responseHandler.sendMessage(msg)
        }
    }
*/
}

class ResponseHandler(val updateUI: (response: String) -> Unit) : Handler() {
    override fun handleMessage(msg: Message?) {
        updateUI(msg?.obj.toString())
        super.handleMessage(msg)
    }
}
