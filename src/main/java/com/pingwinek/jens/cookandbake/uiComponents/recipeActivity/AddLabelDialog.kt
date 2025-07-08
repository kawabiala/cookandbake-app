package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.models.TagFB
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun AddLabelDialog(
    labels: List<Tag>,
    onSave: (Tag) -> Unit,
    onClose: () -> Unit
) {
    var labelTemp: Tag? by remember {
        mutableStateOf(null)
    }

    Dialog(
        onDismissRequest = onClose
    ) {
        val scrollState = rememberScrollState()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onClose
                    ) {
                        Icon(
                            Icons.Outlined.Cancel,
                            stringResource(R.string.close)
                        )
                    }
                }

                SpacerSmall()

                Text(
                    text = stringResource(R.string.add_label),
                    style = MaterialTheme.typography.headlineMedium
                )

                SpacerSmall()

                Row() {
                    if (labels.isEmpty()) {
                        Text(stringResource(R.string.no_labels_available))
                    } else {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                        ) {
                            labels.forEachIndexed { index, tag ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (tag == labelTemp) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            /*
                                        if (labelTemp == null) {
                                            labelTemp = tag
                                        } else {
                                            labelTemp = null
                                        }*/
                                            onSave(tag)
                                        }
                                ) {
                                    Text(tag.label)

                                    SpacerSmall()
                                }
                            }

                            SpacerSmall()
                        }
                    }
                }
/*
                Row(
                    modifier = Modifier
                        .weight(30f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onClose
                    ) {
                        Text(stringResource(R.string.close))
                    }

                    Button(
                        enabled = (labelTemp != null),
                        onClick = {
                            labelTemp?.let { onSave(it) }
                        }
                    ) {
                        Text(stringResource(R.string.add_label))
                    }
                }

 */
            }
        }
    }
}

@Preview(
    device = "id:pixel_9",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewAddLabelDialog(@PreviewParameter(PPAddLabelDialog::class) parameters: Parameters4PreviewAddLabelDialog) {

    AddLabelDialog(
        labels = parameters.labels,
        onClose = parameters.onClose,
        onSave = parameters.onSave
    )
}

class PPAddLabelDialog: PreviewParameterProvider<Parameters4PreviewAddLabelDialog> {
    override val values = sequenceOf(
        Parameters4PreviewAddLabelDialog(
            labels = listOf(),
            onClose = {},
            onSave = {}
        ),
        Parameters4PreviewAddLabelDialog(
            labels = listOf(
                TagFB("Label 1"),
                TagFB( "Label 2")
            ),
            onClose = {},
            onSave = {}
        )
    )
}

data class Parameters4PreviewAddLabelDialog (
    val labels: List<Tag>,
    val onClose: () -> Unit,
    val onSave: (Tag) -> Unit
)