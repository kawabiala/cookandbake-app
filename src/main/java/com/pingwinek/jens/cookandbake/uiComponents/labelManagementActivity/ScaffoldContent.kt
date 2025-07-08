package com.pingwinek.jens.cookandbake.uiComponents.labelManagementActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.DeleteDialog
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall
import java.util.LinkedList

@Composable
fun ScaffoldContent(
    paddingValues: PaddingValues,
    labels: LinkedList<Tag>?,
    onAddLabel: (String) -> Unit,
    onDeleteLabel: (Tag) -> Unit
) {
    val scrollState = rememberScrollState()

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

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(scrollState)
    ) {
        SpacerSmall()

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){

                TextField(
                    value = labelTemp,
                    onValueChange = onValueChange,
                )

                Button(
                    onClick = addLabel
                ) {
                    Text("add")
                }
        }

        SpacerSmall()

        labels?.forEachIndexed { index, label ->

            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(label.label)

                IconButton(
                    onClick = { deleteLabel(label) }
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