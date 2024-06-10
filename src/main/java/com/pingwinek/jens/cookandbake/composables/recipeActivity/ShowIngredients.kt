package com.pingwinek.jens.cookandbake.composables.recipeActivity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.spacing
import com.pingwinek.jens.cookandbake.models.Ingredient

@Composable
fun ShowIngredients(
    paddingValues: PaddingValues,
    ingredients: List<Ingredient>,
    onIngredientsFunctionsMode: (Boolean) -> Unit,
    onEditIngredient: (String) -> Unit,
    onDeleteIngredient: (String) -> Unit,
    onChangeSort: (Map<Ingredient, Int>) -> Unit
) {
    val height = MaterialTheme.spacing.standardIcon
    val paddingBelow = MaterialTheme.spacing.extraSmallPadding
    val switchPositionOffset = (height + paddingBelow).value

    var activePane by remember(ingredients) {
        mutableIntStateOf(-1)
    }

    var ingredientsSorted by remember(ingredients) {
        mutableStateOf(ingredients.sortedBy { ingredient -> ingredient.sort })
    }

    var offset by remember(activePane) { mutableFloatStateOf(0f) }
    var posDelta by remember(activePane) { mutableIntStateOf(0) }

//    val scrollState = rememberScrollState()

    val onChangeActivePane: (activePane: Int) -> Unit = { pane ->
        activePane = pane
        onIngredientsFunctionsMode(pane >= 0)
    }

    val onDrag: (Float) -> Unit = { newOffset ->
        offset = newOffset
        posDelta = (newOffset / switchPositionOffset).toInt()
    }

    val onDragStopped: () -> Unit = {
        val ingredientsLocallyResorted = ingredientsSorted.toMutableList()
        val ingredientsResorted = mutableMapOf<Ingredient, Int>()

        ingredientsSorted.forEachIndexed { index, ingredient ->
            var sort = if (index == activePane) {
                index + posDelta
            } else if(index < activePane && index >= activePane + posDelta) {
                index + 1
            } else if (index > activePane && index <= activePane + posDelta) {
                index - 1
            } else {
                index
            }

            sort = when {
                sort < 0 -> 0
                sort > ingredientsSorted.size -1 -> ingredientsSorted.size -1
                else -> sort
            }

            ingredientsLocallyResorted[sort] = ingredient
            if (sort != ingredient.sort) ingredientsResorted[ingredient] = sort
        }

        onChangeActivePane(-1)
        ingredientsSorted = ingredientsLocallyResorted
        onChangeSort(ingredientsResorted)
    }

    LazyColumn {
        item {
            PingwinekCooksComposables.SpacerSmall()
        }

        ingredientsSorted.forEachIndexed { index, ingredient ->
            val conditionalOffset = if (index == activePane) {
                offset
            } else if (index < activePane && index >= activePane + posDelta) {
                switchPositionOffset
            } else if (index > activePane && index <= activePane + posDelta) {
                switchPositionOffset * -1
            } else {
                0f
            }

            val onChangeActive: () -> Unit = {
                onChangeActivePane(if (activePane == index) -1 else index)
            }

            item {
                key(index) {
                    IngredientPane(
                        paddingValues = paddingValues,
                        height = height,
                        paddingBelow = paddingBelow,
                        zIndex = if (index == activePane) 1f else 0f,
                        elevation = if (index == activePane) 10f else 0f,
                        offset = conditionalOffset,
                        paneColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        showButtons = (activePane == index),
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
    }
/*
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        PingwinekCooksComposables.SpacerSmall()

        ingredientsSorted.forEachIndexed { index, ingredient ->
            val conditionalOffset = if (index == activePane) {
                offset
            } else if(index < activePane && index >= activePane + posDelta) {
                switchPositionOffset
            } else if (index > activePane && index <= activePane + posDelta) {
                switchPositionOffset * -1
            } else {
                0f
            }
            key(index) {
                IngredientPane(
                    paddingValues = paddingValues,
                    height = height,
                    paddingBelow = paddingBelow,
                    zIndex = if (index == activePane) 1f else 0f,
                    elevation = if (index == activePane) 10f else 0f,
                    offset = conditionalOffset,
                    paneColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    showButtons = (activePane == index),
                    onChangeActivePane = {
                        activePane = if (activePane == index) -1 else index
                    },
                    onEditIngredient = onEditIngredient,
                    onDeleteIngredient = onDeleteIngredient,
                    onDrag = onDrag,
                    onDragStopped = onDragStopped,
                    ingredient = ingredient
                )
            }
        }
    }*/
}
