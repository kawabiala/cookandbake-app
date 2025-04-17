package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.pingwinek.jens.cookandbake.R

@Composable
fun EditPane(
    paddingValues: PaddingValues,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
    ) {
        SpacerSmall()

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .clickable { onCancel() },
                text = stringResource(R.string.close)
            )
            Text(
                modifier = Modifier
                    .clickable { onSave() },
                text = stringResource(R.string.save)
            )
        }

        SpacerMedium()

        content()
    }
}

@Preview(
    device = "id:pixel_9",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewEditPane(@PreviewParameter(PPPeditPane::class) parameters: Parameters4EditPane) {

    EditPane(parameters.paddingValues, parameters.onCancel, parameters.onSave, parameters.content)
}

class PPPeditPane: PreviewParameterProvider<Parameters4EditPane> {
    override val values = sequenceOf(
        Parameters4EditPane(PaddingValues.Absolute(), {}, {}, { Text("This is example content") })
    )
}

data class Parameters4EditPane(
    val paddingValues: PaddingValues = PaddingValues.Absolute(),
    val onCancel: () -> Unit = {},
    val onSave: () -> Unit = {},
    val content: @Composable () -> Unit
)
