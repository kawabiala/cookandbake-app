package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import androidx.compose.runtime.Composable
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables

class ImpressumActivity : BaseActivity() {

    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        addContentView(R.layout.activity_impressum)

        url = intent?.extras?.getString("url") ?: ""
//        findViewById<WebView>(R.id.impressumWebView).loadUrl(url)
/*
        optionMenu.apply {
            addMenuEntry(
                R.id.OPTION_MENU_CLOSE,
                resources.getString(R.string.close),
                R.drawable.ic_action_close_black,
                true
            ) {
                finish()
                true
            }
        }

 */
    }

    @Composable
    override fun ScaffoldContent() {
        PingwinekCooksComposables.WebView(url)
    }
}
