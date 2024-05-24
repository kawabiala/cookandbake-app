package com.pingwinek.jens.cookandbake.theming

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing (
    val standardPadding: Dp = 10.dp,
    val extraSmallPadding: Dp = 5.dp,
    val mainWindowPadding: Dp = 40.dp,
    val spacerSmall: Dp = 10.dp,
    val spacerMedium: Dp = 20.dp,
    val spacerLarge: Dp = 30.dp,
    val standardIcon: Dp = 48.dp
)