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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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
    availableTags: List<Tag>,
    attachedTags: List<Tag>,
    onSave: (Map<Tag, Boolean>) -> Unit,
    onClose: () -> Unit
) {
    val tags = remember(availableTags, attachedTags) {
        val attachedIds = attachedTags.map { it.id }.toSet()
        val sortedList = availableTags.sortedBy { it.sort }

        mutableStateMapOf<Tag, Boolean>().apply {
            sortedList.forEach { tag ->
                this[tag] = tag.id in attachedIds
            }
        }
    }

    val tagList by remember { derivedStateOf { tags.toList() } }

    val onClick: (Tag) -> Unit = { tag ->
        tags[tag]?.let { selected ->
            tags[tag] = !selected
        }
    }

    EditPane(
        paddingValues = paddingValues,
        onCancel = onClose,
        onSave = { onSave(tags) }
    ) {

        LazyColumn {
            items(
                items = tagList,
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
                                stringResource(R.string.label_selected)
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}