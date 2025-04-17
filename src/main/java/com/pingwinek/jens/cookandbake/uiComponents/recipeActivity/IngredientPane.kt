package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.Utils
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.uiComponents.spacing

@Composable
fun IngredientPane(
    paddingValues: PaddingValues,
    height: Dp,
    paddingBelow: Dp,
    zIndex: Float,
    elevation: Float,
    offset: Float,
    paneColor: Color,
    contentColor: Color,
    showButtons: Boolean,
    onChangeActive: () -> Unit,
    onEditIngredient: (String) -> Unit,
    onDeleteIngredient: (String) -> Unit,
    onDrag: (Float) -> Unit,
    onDragStopped: () -> Unit,
    ingredient: Ingredient
){
    Surface(
        color = paneColor,
        contentColor = contentColor,
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .padding(bottom = paddingBelow)
            .height(height)
            .zIndex(zIndex)
            .offset(Dp(0f), Dp(offset))
            .shadow(
                elevation = Dp(elevation)
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    start = paddingValues.calculateStartPadding(
                        LayoutDirection.Ltr
                    ),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(80f)
                    .fillMaxWidth()
                    .clickable { onChangeActive() },
            ) {
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
                Text(
                    modifier = Modifier
                        .height(height/2),
                    //fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = ingredient.name
                )
                Text(
                    modifier = Modifier
                        .height(height / 2)
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
                    val density = LocalDensity.current
                    val draggableState = rememberDraggableState { delta ->
                        val deltaDPValue = density.run { delta.toDp().value }
                        onDrag(offset + deltaDPValue)
                    }
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
}
