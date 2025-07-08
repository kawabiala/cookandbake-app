package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.zIndex
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.Utils
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.ListPane
import com.pingwinek.jens.cookandbake.uiComponents.spacing

@Composable
fun IngredientPane(
    zIndex: Float,
    elevation: Float,
    offset: Offset,
    paneColor: Color,
    contentColor: Color,
    showButtons: Boolean,
    onChangeActive: () -> Unit,
    onEditIngredient: (String) -> Unit,
    onDeleteIngredient: (String) -> Unit,
    onDrag: (Float) -> Unit,
    onDragStopped: () -> Unit,
    reportY: (Float, Float) -> Unit,
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

    ListPane(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                reportY(coordinates.positionInRoot().y, coordinates.boundsInRoot().height)
            }
            .zIndex(zIndex)
            .offset { offset.round() }
            .shadow(
                elevation = Dp(elevation)
            ),
        color = paneColor,
        contentColor = contentColor
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(80f)
                    .fillMaxWidth()
                    .clickable { onChangeActive() },
            ) {
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = ingredient.name
                )
                Text(
                    modifier = Modifier
//                        .height(height/2)
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
/*                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .draggable(
                                state = draggableState,
                                orientation = Orientation.Vertical
                            )
                            .dragAndDropSource(
                                transferData = { _ ->
                                    Log.i(this::class.java.name, "source")
                                    DragAndDropTransferData(ClipData.newPlainText("", ""))
                                },
                                //drawDragDecoration = {

                                //}
                            ),
                        imageVector =  Icons.Filled.Menu,
                        contentDescription = "DragAndDrop"
                    )*/
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