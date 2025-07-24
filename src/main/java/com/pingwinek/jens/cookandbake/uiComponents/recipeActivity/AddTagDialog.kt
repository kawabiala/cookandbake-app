package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.models.TagFB
import com.pingwinek.jens.cookandbake.uiComponents.spacing

@Composable
fun AddTagDialog(
    tags: List<Tag>,
    onSave: (List<Tag>) -> Unit,
    onClose: () -> Unit
) {
    val tagsTemp: MutableMap<Tag, Boolean> = remember {
        mutableStateMapOf<Tag, Boolean>().apply {
            tags.forEach { tag ->
                this[tag] = false
            }
        }
    }

    val isSavable: Boolean by remember {
        derivedStateOf {
            tagsTemp.values.contains(true)
        }
    }

    val onClick = fun(tag: Tag) {
        tagsTemp[tag] = tagsTemp[tag] != true
    }

    val onSaveSelected = fun() {
        onSave(tagsTemp.filter { entry ->
            entry.value
        }.keys.toList())
    }

    Dialog(
        onDismissRequest = onClose
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .weight(2f)
                ) {
                    Text(
                        text = stringResource(R.string.add_label),
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(6f)
                ) {
                    if (tags.isEmpty()) {
                        Text(stringResource(R.string.no_labels_available))
                    } else {

                        LazyColumn(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spacerSmall)
                        ) {
                            items(
                                items = tags.sortedBy { tag -> tag.label.lowercase() },
                                key = { tag -> tag.id }
                            ) { tag ->

                                FilterChip(
                                    selected = tagsTemp[tag] ?: false,
                                    onClick = { onClick(tag) },
                                    label = {
                                        Text(
                                            text = tag.label,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        ) },
                                    modifier = Modifier
                                        .width(180.dp),
                                    leadingIcon = {
                                        if (tagsTemp[tag] == true) {
                                            Icon(
                                                Icons.Outlined.Check,
                                                stringResource(R.string.add_label)
                                            )
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )

                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onClose,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(stringResource(R.string.close))
                    }

                    Button(
                        enabled = (isSavable),
                        onClick = onSaveSelected,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(stringResource(R.string.add_label))
                    }
                }
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

    AddTagDialog(
        tags = parameters.labels,
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
                TagFB(id = "1", label ="Label 1"),
                TagFB(id = "2", label = "Label 2")
            ),
            onClose = {},
            onSave = {}
        )
    )
}

data class Parameters4PreviewAddLabelDialog (
    val labels: List<Tag>,
    val onClose: () -> Unit,
    val onSave: (List<Tag>) -> Unit
)