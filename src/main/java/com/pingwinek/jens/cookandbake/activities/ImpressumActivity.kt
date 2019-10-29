package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.webkit.WebView
import com.pingwinek.jens.cookandbake.OPTION_MENU_CLOSE
import com.pingwinek.jens.cookandbake.R

class ImpressumActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_impressum)

        val url = intent?.extras?.getString("url") ?: ""
        findViewById<WebView>(R.id.impressumWebView).loadUrl(url)
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
}
