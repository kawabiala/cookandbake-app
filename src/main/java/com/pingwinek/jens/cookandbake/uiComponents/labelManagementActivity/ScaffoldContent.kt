package com.pingwinek.jens.cookandbake.uiComponents.labelManagementActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.DragAndDropList
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.ListPane
import com.pingwinek.jens.cookandbake.uiComponents.spacing
import com.pingwinek.jens.cookandbake.viewModels.LabelManagementViewModel

@Composable
fun ScaffoldContent(
    paddingValues: PaddingValues,
    tagsWithCount: List<LabelManagementViewModel.TagWithCount>?,
    tagEditMode: LabelManagementActivity.TagEditMode,
    onChangeTagEditMode: (LabelManagementActivity.TagEditMode) -> Unit,
    onAddLabel: (label: String, color: String, sort: Int) -> Unit,
    onDeleteLabel: (Tag) -> Unit,
    onUpdateLabel: (tag: Tag, label: String, color: String, sort: Int) -> Unit
) {
    val tagsSortMax by remember(tagsWithCount) {
        derivedStateOf {
            tagsWithCount?.maxOfOrNull { twc ->
                twc.tag.sort
            }?.plus(1)
        }
    }

    var activeItem: LabelManagementViewModel.TagWithCount? by remember {
        mutableStateOf(null)
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var labelTemp by remember { mutableStateOf("") }
    var tagTemp: Tag? by remember { mutableStateOf(null) }

    val onValueChange: (String) -> Unit = { changedValue ->
        labelTemp = changedValue
    }

    val reset: () -> Unit = {
        tagTemp = null
        labelTemp = ""
        onChangeTagEditMode(LabelManagementActivity.TagEditMode.SHOW)
        activeItem = null
        showDeleteDialog = false
    }

    val addLabel: () -> Unit = {
        onAddLabel(labelTemp, "", tagsSortMax ?: -1)
    }

    val deleteLabel: (Tag) -> Unit = { tag ->
        tagTemp = tag
        showDeleteDialog = true
    }

    val updateLabel: () -> Unit = {
        tagTemp?.let { tt ->
            onUpdateLabel(tt, labelTemp, tt.color, tt.sort)
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
        reset()
    }

    val onChangeSort: (Map<LabelManagementViewModel.TagWithCount, Int>) -> Unit = { twcs ->
        twcs.forEach { entry ->
            onUpdateLabel(entry.key.tag, entry.key.tag.label, entry.key.tag.color, entry.value)
        }
    }

    if (showDeleteDialog) {
        DeleteDialog(
            stringResource(R.string.label_delete_confirm),
            onClose = { reset() }
        ) {
            tagTemp?.let { onDeleteLabel(it) }
            reset()
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
    ) {

        if (tagEditMode == LabelManagementActivity.TagEditMode.SHOW) {

            tagsWithCount?.let { nonNullListOfTagsWithCount ->

                DragAndDropList(
                    spacing = MaterialTheme.spacing.spacerSmall,
                    listContent = nonNullListOfTagsWithCount.sortedBy { twc -> twc.tag.sort },
                    key = { twc -> twc.tag.id },
                    sort = { twc -> twc.tag.sort },
                    activeItem = activeItem,
                    onChangeActiveItem = { twc ->
                        activeItem = if (activeItem == twc) null else twc
                    },
                    onChangeSort = onChangeSort
                ) { tagWithCount, active, onChangeActive, onDrag, onDragStopped ->

                    ListPane(
                        modifier = Modifier
                            .clickable { onChangeActive() },
                    ) {

                        val draggableState = rememberDraggableState { delta ->
                            onDrag(delta)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "${tagWithCount.tag.label} (${tagWithCount.count})",
                                modifier = Modifier
                                    .weight(0.6f)
                            )

                            if (active) {
                                Row {
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

                                    IconButton(
                                        modifier = Modifier
                                            .draggable(
                                                state = draggableState,
                                                orientation = Orientation.Vertical,
                                                onDragStopped = { onDragStopped() }
                                            ),
                                        onClick = {}
                                    ) {
                                        Icon(
                                            Icons.Filled.Menu,
                                            "DragAndDrop"
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
                onCancel = { reset() },
                onSave = onSaveTag
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    TextField(
                        value = labelTemp,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

        }
    }
}