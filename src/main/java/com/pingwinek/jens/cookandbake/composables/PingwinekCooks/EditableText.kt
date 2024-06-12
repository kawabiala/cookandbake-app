package com.pingwinek.jens.cookandbake.composables.PingwinekCooks

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun EditableText(
    label: String? = null,
    text: String,
    supportingText: String? = null,
    editable: Boolean = false,
    onValueChange: (text: String) -> Unit = {},
    onSupportingTextClicked: () -> Unit = {}
) {
    if (editable) {
        TextField(
            value = text,
            label = {
                if (!label.isNullOrEmpty()) {
                    Text(label)
                }
            },
            supportingText = {
                if (!supportingText.isNullOrEmpty()) {
                    Text(
                        text = supportingText,
                        modifier = Modifier.clickable { onSupportingTextClicked() },
                    )
                }
            },
            onValueChange = onValueChange,
        )
    } else {
        Text(text)
    }
}
