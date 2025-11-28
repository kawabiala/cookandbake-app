package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.pingwinek.jens.cookandbake.uiComponents.spacing

@Composable
fun ListPane(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    margin: Dp = MaterialTheme.spacing.standardPadding,
    paddingValues: PaddingValues? = null,
    content: @Composable () -> Unit
    ) {
    Surface(
        color = color,
        contentColor = contentColor,
        shape = ShapeDefaults.Small,
        modifier = modifier
    ) {
        val paddingValues: PaddingValues = paddingValues ?: PaddingValues(margin)

        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            content()
        }
    }
}