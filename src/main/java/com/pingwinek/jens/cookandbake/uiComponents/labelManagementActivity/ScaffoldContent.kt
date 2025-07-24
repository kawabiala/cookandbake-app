package com.pingwinek.jens.cookandbake.uiComponents.labelManagementActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.activities.LabelManagementActivity
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.DeleteDialog
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.ListPane
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall
import com.pingwinek.jens.cookandbake.uiComponents.spacing
import com.pingwinek.jens.cookandbake.viewModels.LabelManagementViewModel
import java.util.LinkedList

@Composable
fun ScaffoldContent(
    paddingValues: PaddingValues,
    tagsWithCount: LinkedList<LabelManagementViewModel.TagWithCount>?,
    tagEditMode: LabelManagementActivity.TagEditMode,
    onChangeTagEditMode: (LabelManagementActivity.TagEditMode) -> Unit,
    onAddLabel: (String) -> Unit,
    onDeleteLabel: (Tag) -> Unit,
    onUpdateLabel: (Tag, String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var labelTemp by remember { mutableStateOf("") }
    var tagTemp: Tag? by remember { mutableStateOf(null) }

    val onValueChange: (String) -> Unit = { changedValue ->
        labelTemp = changedValue
    }

    val addLabel: () -> Unit = {
        onAddLabel(labelTemp)
        labelTemp = ""
    }

    val deleteLabel: (Tag) -> Unit = { tag ->
        tagTemp = tag
        showDeleteDialog = true
    }

    val updateLabel: () -> Unit = {
        tagTemp?.let { tt ->
            onUpdateLabel(tt, labelTemp)
        }
    }

    val onEditTag: (Tag) -> Unit = { tag ->
        tagTemp = tag
        labelTemp = tag.label
        onChangeTagEditMode(LabelManagementActivity.TagEditMode.UPDATE)
    }

    val onSaveTag: () -> Unit = {
        if (tagEditMode == LabelManagementActivity.TagEditMode.ADD) {
            addLabel()
        } else if (tagEditMode == LabelManagementActivity.TagEditMode.UPDATE) {
            updateLabel()
        }
        tagTemp = null
        labelTemp = ""
        onChangeTagEditMode(LabelManagementActivity.TagEditMode.SHOW)
    }

    if (showDeleteDialog) {
        DeleteDialog(
            stringResource(R.string.label_delete_confirm),
            onClose = { showDeleteDialog = false }
        ) {
            tagTemp?.let { onDeleteLabel(it) }
            showDeleteDialog = false
            tagTemp = null
        }
    }

    if (tagEditMode == LabelManagementActivity.TagEditMode.SHOW) {

        tagsWithCount?.let { nonNullListOfTagsWithCount ->

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spacerSmall)
            ) {
                item {
                    SpacerSmall()
                }

                items(
                    nonNullListOfTagsWithCount.sortedBy { twc -> twc.tag.label.lowercase() },
                    key = { tag -> tag.tag.id }
                ) { tagWithCount ->
                    ListPane {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "${tagWithCount.tag.label} (${tagWithCount.count})",
                                modifier = Modifier
                                    .weight(0.6f)
                            )

                            Row() {
                                IconButton(
                                    onClick = { onEditTag(tagWithCount.tag) }
                                ) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        stringResource(R.string.edit_label)
                                    )
                                }

                                IconButton(
                                    onClick = { deleteLabel(tagWithCount.tag) }
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        stringResource(R.string.delete_label)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    } else {

        EditPane(
            paddingValues = paddingValues,
            onCancel = { onChangeTagEditMode(LabelManagementActivity.TagEditMode.SHOW) },
            onSave = onSaveTag
        ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextField(
                        value = labelTemp,
                        onValueChange = onValueChange,
                    )
                }
            }

    }
}