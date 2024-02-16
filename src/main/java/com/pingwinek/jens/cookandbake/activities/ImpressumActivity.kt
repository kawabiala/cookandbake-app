package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables

class ImpressumActivity : BaseActivity() {

    private var url: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = intent?.extras?.getString("title") ?: ""
        url = intent?.extras?.getString("url") ?: ""

        configureTopBar(
            title = title,
            optionItemRight = PingwinekCooksComposables.OptionItem(
                "close",
                Icons.Filled.Close
            ) { finish() })

        navigationBarItemsEnabled.value = false
    }

    @Composable
    override fun ScaffoldContent() {
        PingwinekCooksComposables.WebView(url)
    }
}
