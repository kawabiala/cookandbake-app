package com.pingwinek.jens.cookandbake.composables.PingwinekCooks

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.pingwinek.jens.cookandbake.theming.DarkColors
import com.pingwinek.jens.cookandbake.theming.LightColors
import com.pingwinek.jens.cookandbake.theming.Typography

@Composable
fun PingwinekCooksAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
