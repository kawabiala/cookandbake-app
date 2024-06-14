package com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooks

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pingwinek.jens.cookandbake.uiComponents.spacing

@Composable
fun SpacerSmall() {
    Spacer(modifier = Modifier.padding(MaterialTheme.spacing.spacerSmall))
}

@Composable
fun SpacerMedium() {
    Spacer(modifier = Modifier.padding(MaterialTheme.spacing.spacerMedium))
}

@Composable
fun SpacerLarge() {
    Spacer(modifier = Modifier.padding(MaterialTheme.spacing.spacerLarge))
}
