package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane

@Composable
fun EditTags(
    paddingValues: PaddingValues = PaddingValues.Absolute(),
    tags: Map<Tag, Boolean>,
    onChangeTags: (Map<Tag, Boolean>) -> Unit,
    onClose: () -> Unit
) {
    val localTags: SnapshotStateMap<Tag, Boolean> = remember {
        mutableStateMapOf<Tag, Boolean>().apply {
            this.putAll(tags)
        }
    }

    val onClick = fun(tag: Tag) {
        localTags[tag]?.let { selected ->
            localTags[tag] = !selected
        }
    }

    EditPane(
        paddingValues = paddingValues,
        onCancel = onClose,
        onSave = { onChangeTags(localTags) }
    ) {

        LazyColumn {
            items(
                items = localTags.toList().sortedBy { pair -> pair.first.sort },
                key = { pair -> pair.first.id }
            ) { pair ->

                val tag = pair.first
                val selected = pair.second

                FilterChip(
                    selected = selected,
                    onClick = { onClick(tag) },
                    label = {
                        Text(
                            text = tag.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .width(180.dp),
                    leadingIcon = {
                        if (selected) {
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