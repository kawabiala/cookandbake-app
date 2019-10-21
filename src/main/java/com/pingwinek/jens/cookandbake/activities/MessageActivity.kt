package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.widget.TextView
import com.pingwinek.jens.cookandbake.R

class MessageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_message)

    }

    override fun getOptionsMenu(): OptionMenu {
        return OptionMenu()
    }

    override fun onResume() {
        super.onResume()
        findViewById<TextView>(R.id.textMessage).text =
            intent?.extras?.getString("message", "")
    }
}