package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.Utils
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.uiComponents.spacing

@Composable
fun IngredientPane(
    showButtons: Boolean,
    onEditIngredient: (String) -> Unit,
    onDeleteIngredient: (String) -> Unit,
    onDrag: (Float) -> Unit,
    onDragStopped: () -> Unit,
    ingredient: Ingredient
){

    val draggableState = rememberDraggableState { delta ->
        onDrag(delta)
    }

    val quantity =
        if (!ingredient.quantityVerbal.isNullOrEmpty()) {
            ingredient.quantityVerbal!!
        } else {
            val quantityAsString =
                Utils.quantityToString(ingredient.quantity)
            if (quantityAsString.isEmpty()) {
                ""
            } else if (ingredient.unity.isNullOrEmpty()) {
                quantityAsString
            } else {
                "$quantityAsString ${ingredient.unity}"
            }
        }

    Row(
//        modifier = Modifier
//            .clickable { onChangeActive() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(80f)
        ) {
            Text(
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = ingredient.name
            )
            Text(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.spacerSmall
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = quantity
            )
        }

        Row(
            modifier = Modifier
                .weight(40f)
        ) {
            if (showButtons) {
                IconButton(onClick = {
                    onEditIngredient(ingredient.id)
                }) {
                    Icon(
                        Icons.Filled.Edit,
                        stringResource(R.string.edit_ingredient)
                    )
                }
                IconButton(onClick = {
                    onDeleteIngredient(ingredient.id)
                }) {
                    Icon(
                        Icons.Filled.Delete,
                        stringResource(R.string.delete_ingredient)
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