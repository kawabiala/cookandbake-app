package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var activeItem: Ingredient? by remember(ingredients) {
        mutableStateOf(null)
    }

    val onChangeActiveItem = fun(ingredient: Ingredient?) {
        onIngredientsFunctionsMode(ingredient != null)
        activeItem = ingredient
    }

    DragAndDropList(
        spacing = MaterialTheme.spacing.spacerSmall,
        listContent = ingredients.sortedBy { ingredient -> ingredient.sort },
        key = { ingredient -> ingredient.id },
        activeItem = ingredients.find { it == activeItem },
        onChangeActiveItem = onChangeActiveItem,
        onChangeSort = onChangeSort,
        ) { ingredient, active, onChangeActive, onDrag, onDragStopped ->

        ListPane(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            IngredientPane(
                showButtons = active,
                onChangeActive = onChangeActive,
                onEditIngredient = onEditIngredient,
                onDeleteIngredient = onDeleteIngredient,
                onDrag = onDrag,
                onDragStopped = onDragStopped,
                ingredient = ingredient
            )
        }
    }
}