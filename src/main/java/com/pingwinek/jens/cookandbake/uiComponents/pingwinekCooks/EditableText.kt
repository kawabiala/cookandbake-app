package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

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

@Preview(
    device = "id:pixel_9",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewEditableText(@PreviewParameter(PPPeditable::class) parameters:Parameters4EditableText) {

    var txt by remember { mutableStateOf("Text") }

    EditableText(
        label = parameters.label,
        text = txt,
        supportingText = parameters.supportingText,
        editable = parameters.editable,
        onValueChange = {text -> txt = text},
        onSupportingTextClicked = {}
    )
}

class PPPeditable: PreviewParameterProvider<Parameters4EditableText> {
    override val values = sequenceOf(
        Parameters4EditableText("Label","supporting Text", false),
        Parameters4EditableText("Label","supporting Text", true),
        Parameters4EditableText("", "", true)
    )
}

data class Parameters4EditableText(
    val label: String,
    val supportingText: String,
    val editable: Boolean
)