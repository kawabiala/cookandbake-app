package com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooks

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun LabelledCheckBox(
    checked : Boolean = false,
    label : String,
    onCheckedChange : (checked: Boolean) -> Unit = {}
) {
    var checkedLocal by remember { mutableStateOf(checked) }
    Row(
        Modifier.toggleable(
            value = checked,
            onValueChange = {
                checkedLocal = it
                onCheckedChange(it)
            }
        )
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                checkedLocal = it
                onCheckedChange(it)
            }
        )
        Text(text = label)
    }
}
