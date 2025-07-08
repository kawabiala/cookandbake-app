package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.pingwinek.jens.cookandbake.theming.Typography
import com.pingwinek.jens.cookandbake.theming.darkScheme
import com.pingwinek.jens.cookandbake.theming.lightScheme

@Composable
fun PingwinekCooksAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!useDarkTheme) {
        lightScheme
    } else {
        darkScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
