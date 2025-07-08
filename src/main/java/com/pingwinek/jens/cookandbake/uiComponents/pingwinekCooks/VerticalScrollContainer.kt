package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun VerticalScrollContainer(
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()

    SpacerSmall()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        content()
    }
}
