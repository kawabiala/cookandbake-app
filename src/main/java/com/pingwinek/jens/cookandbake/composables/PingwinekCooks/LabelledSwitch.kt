package com.pingwinek.jens.cookandbake.composables.PingwinekCooks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun LabelledSwitch(
    checked : Boolean = false,
    label : String,
    labelTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    onCheckedChange : (checked: Boolean) -> Unit = {}
) {
    var checkedLocal by remember { mutableStateOf(checked) }
    Row(
        modifier = Modifier
            .toggleable(
                value = checkedLocal,
                onValueChange = {
                    checkedLocal = it
                    onCheckedChange(it)
                }
            )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(0.6F),
            text = label,
            style = labelTextStyle,
        )
        Spacer(
            modifier = Modifier.weight(0.1F)
        )
        Switch(
            checked = checkedLocal,
            onCheckedChange = {
                checkedLocal = it
                onCheckedChange(it)
            })
    }
}
