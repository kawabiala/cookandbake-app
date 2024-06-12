package com.pingwinek.jens.cookandbake.composables.PingwinekCooks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Expandable(
    headerText: String,
    headerTextStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    headerTextColor: Color = MaterialTheme.colorScheme.onSurface,
    contentTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    boxColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    padding: Dp = Dp(0F),
    content: @Composable (TextStyle) -> Unit
) {
    var isOpen by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(color = boxColor)
            .padding(padding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isOpen = !isOpen },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = headerText,
                style = headerTextStyle,
                color = headerTextColor,
            )
            if (isOpen) {
                Icon(
                    Icons.Filled.ArrowDropUp,
                    ""
                )
            } else {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    ""
                )
            }
        }

        if (isOpen) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                content(contentTextStyle)
            }
        }
    }
}
