package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.composables.PingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.composables.PingwinekCooks.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.composables.PingwinekCooks.WebView
import com.pingwinek.jens.cookandbake.composables.PingwinekCooksComposableHelpers

class ImpressumActivity : AppCompatActivity() {

    private var url: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = intent?.extras?.getString("title") ?: ""
        url = intent?.extras?.getString("url") ?: ""

        setContent {
            PingwinekCooksAppTheme {
                PingwinekCooksScaffold(
                    title = title,
                    optionItemRight = PingwinekCooksComposableHelpers.OptionItem(
                        R.string.close,
                        Icons.Filled.Close
                    ) { finish() }
                ) { paddingValues ->
                    ScaffoldContent(paddingValues = paddingValues)
                }
            }
        }
    }

    @Composable
    private fun ScaffoldContent(paddingValues: PaddingValues) {
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            WebView(url)
        }
    }
}
