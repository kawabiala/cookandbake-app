package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.DragAndDropList
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.ListPane
import com.pingwinek.jens.cookandbake.uiComponents.spacing

@Composable
fun ShowIngredients(
    ingredients: List<Ingredient>,
    onIngredientsFunctionsMode: (Boolean) -> Unit,
    onEditIngredient: (String) -> Unit,
    onDeleteIngredient: (String) -> Unit,
    onChangeSort: (Map<Ingredient, Int>) -> Unit
) {
    var sortedIngredients by remember(ingredients) {
        mutableStateOf(ingredients.sortedBy { ingredient -> ingredient.sort })
    }

    var activeItem: Ingredient? by remember(ingredients) {
        mutableStateOf(null)
    }

    val onChangeActiveItem: (Ingredient?) -> Unit = { ingredient ->
        if (activeItem == ingredient) {
            activeItem = null
            onIngredientsFunctionsMode(false)
        } else {
            onIngredientsFunctionsMode(ingredient != null)
            activeItem = ingredient
        }
    }

    Column {

        DragAndDropList(
            spacing = MaterialTheme.spacing.spacerSmall,
            listContent = sortedIngredients,
            key = { ingredient -> ingredient.id },
            sort = { ingredient -> ingredient.sort },
            activeItem = ingredients.find { it == activeItem },
            onChangeActiveItem = onChangeActiveItem,
            onChangeSort = onChangeSort,
            ) { ingredient, active, onChangeActive, onDrag, onDragStopped ->

            val color = if (ingredient.isGroupHeader) Color.Unspecified else MaterialTheme.colorScheme.surfaceContainerHigh
            val paddingValues = PaddingValues(
                start = MaterialTheme.spacing.standardPadding,
                top = if (ingredient.isGroupHeader) MaterialTheme.spacing.standardPadding * 2 else MaterialTheme.spacing.standardPadding,
                end = MaterialTheme.spacing.standardPadding,
                bottom = if (ingredient.isGroupHeader) 0.dp else MaterialTheme.spacing.standardPadding
            )

            ListPane(
                modifier = Modifier
                    .clickable { onChangeActive() },
                color = color,
                contentColor = MaterialTheme.colorScheme.onBackground,
                paddingValues = paddingValues
            ) {
                IngredientPane(
                    showButtons = active,
                    onEditIngredient = onEditIngredient,
                    onDeleteIngredient = onDeleteIngredient,
                    onDrag = onDrag,
                    onDragStopped = onDragStopped,
                    ingredient = ingredient
                )
            }
        }
    }
}