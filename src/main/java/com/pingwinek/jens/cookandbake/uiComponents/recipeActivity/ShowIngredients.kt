package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.uiComponents.spacing

@Composable
fun ShowIngredients(
    ingredients: List<Ingredient>,
    onIngredientsFunctionsMode: (Boolean) -> Unit,
    onEditIngredient: (String) -> Unit,
    onDeleteIngredient: (String) -> Unit,
    onChangeSort: (Map<Ingredient, Int>) -> Unit
) {
    val density = LocalDensity.current

    val ingredientsOriginalSorted by remember(ingredients) {
        mutableStateOf(ingredients.sortedBy { ingredients -> ingredients.sort })
    }

    var ingredientsSorted by remember(ingredients) {
        mutableStateOf(ingredientsOriginalSorted)
    }

    val ingredientsYMap: MutableMap<String, IngredientTabCalculation.Y> by remember(ingredients) {
        mutableStateOf(
            mutableMapOf<String, IngredientTabCalculation.Y>().also { map ->
                ingredientsOriginalSorted.forEach { ingredient ->
                    map[ingredient.id] = IngredientTabCalculation.Y(null, 0f)
                }
            }
        )
    }

    val ingredientsOffsetYMap: MutableMap<String, Float> by remember(ingredients) {
        mutableStateOf(
            mutableMapOf<String, Float>().also { map ->
                ingredientsOriginalSorted.forEach { ingredient ->
                    map[ingredient.id] = 0f
                }
            }
        )
    }

    var activePane: String? by remember(ingredients) {
        mutableStateOf(null)
    }
    var temporaryRank: Int? by remember { mutableStateOf(null) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var basePosY: Float? by remember { mutableStateOf(null) }

    val resetActivePane: () -> Unit = {
        activePane = null
        basePosY = null
        offsetY = 0f
        temporaryRank = null
        onIngredientsFunctionsMode(false)
    }

    val updateOffsets = fun(nonNullActivePane: String) {

        ingredientsYMap[nonNullActivePane]?.let { y ->
            val height = y.height + with(density) { MaterialTheme.spacing.spacerSmall.toPx() }

            ingredientsOriginalSorted.forEachIndexed { index, ingredient ->
                ingredientsOffsetYMap[ingredient.id] = if (ingredient.id == nonNullActivePane) {
                    offsetY
                } else if (index < ingredientsSorted.indexOf(ingredient)) {
                    height
                } else if (index > ingredientsSorted.indexOf(ingredient)) {
                    height * -1
                } else {
                    0f
                }

//                Log.i("updateOffsets", "orig: $index, new: ${ingredientsSorted.indexOf(ingredient)}, offset: ${ingredientsOffsetYMap[ingredient.id]}")
            }
        }
    }

    val onChangeActive: (Ingredient) -> Unit = { ingredient ->
        if (activePane == null) {
            activePane = ingredient.id
            basePosY = ingredientsYMap[ingredient.id]?.positionY
            temporaryRank = ingredientsOriginalSorted.indexOf(ingredient)
        } else {
            resetActivePane()
        }
    }

    val onDrag: (Float) -> Unit = { deltaOffset ->
        activePane?.let { ap ->
            basePosY?.let { bpY ->
                offsetY += deltaOffset

                val newRank = IngredientTabCalculation.getRank(
                    ingredientsYMap.values.mapNotNull { y -> y.positionY }.toList(), bpY + offsetY
                )

                if (temporaryRank != newRank) {
                    val oldRank = ingredientsOriginalSorted.indexOfFirst { ingredient -> ingredient.id == ap }

                    ingredientsSorted = IngredientTabCalculation.resort(
                        ingredientsOriginalSorted, oldRank, newRank
                    )

                    updateOffsets(ap)

                    temporaryRank = newRank

                }
            }
        }
    }

    val onDragStopped: () -> Unit = {
        offsetY = 0f
        resetActivePane()
        onChangeSort(
            IngredientTabCalculation.compare(
                ingredientsOriginalSorted,
                ingredientsSorted
            )
        )
    }

    val reportPosition: (Ingredient, Float, Float) -> Unit = { ingredient, positionY, height ->
        ingredientsYMap[ingredient.id] = IngredientTabCalculation.Y(positionY, height)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spacerSmall)
    ) {
        /*
        items(
            count = ingredientsSorted.size,
            key = ({ id ->
                Log.i("key", "for $id: $id - ${ingredients[id].id}")
                ingredientsSorted[id].id
            }),
            contentType = { id ->
                ingredientsSorted[id]
            }
        ) { id ->
            val ingredient = ingredientsSorted[id]
            Log.i("LazyColumn", "$id - ${ingredient.name}")

            val conditionalOffsetY = if (ingredient.id == activePane) {
                offsetY
            } else {
                0f
            }

            val conditionalOffset = Offset(
                0f,
                conditionalOffsetY
            )

            IngredientPane(
//                        height = height,
                zIndex = if (ingredient.id == activePane) 1f else 0f,
                elevation = if (ingredient.id == activePane) 10f else 0f,
                offset = conditionalOffset,
                paneColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onBackground,
                showButtons = (activePane == ingredient.id),
                onChangeActive = { onChangeActive(ingredient.id) },
                onEditIngredient = onEditIngredient,
                onDeleteIngredient = onDeleteIngredient,
                draggableState = draggableState,
                onDrag = onDrag,
                onDragStopped = onDragStopped,
//                        dragging = { onDragging(index) },
//                        entered = { onEntered(index) },
//                        dropped = { onDropped(index) },
                reportOffset = { offset -> reportPosition(ingredient, offset) },
                ingredient = ingredient
            )
        }*/

        ingredientsOriginalSorted.forEach { ingredient ->
            val conditionalOffsetY = if (ingredient.id == activePane) {
                offsetY
            } else {
                ingredientsOffsetYMap[ingredient.id]
            }

            val conditionalOffset = Offset(
                0f,
                conditionalOffsetY ?: 0f
            )

            item {
                key(ingredient.id) {
                    IngredientPane(
                        zIndex = if (ingredient.id == activePane) 1f else 0f,
                        elevation = if (ingredient.id == activePane) 10f else 0f,
                        offset = conditionalOffset,
                        paneColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        showButtons = (activePane == ingredient.id),
                        onChangeActive = { onChangeActive(ingredient) },
                        onEditIngredient = onEditIngredient,
                        onDeleteIngredient = onDeleteIngredient,
                        onDrag = onDrag,
                        onDragStopped = onDragStopped,
                        reportY = { positionY, height -> reportPosition(ingredient, positionY, height) },
                        ingredient = ingredient
                    )
                }
            }
        }
    }
}

object IngredientTabCalculation {

    data class Y(val positionY: Float?, val height: Float)

    fun compare (originalSorted: List<Ingredient>, resorted: List<Ingredient>) : Map<Ingredient, Int> {
        val toChange: MutableMap<Ingredient, Int> = mutableMapOf()

        originalSorted.forEachIndexed { index, ingredient ->
            val newIndex = resorted.indexOf(ingredient)
            if (index != newIndex) {
                toChange[ingredient] = newIndex
            }
        }

        return toChange
    }

    fun getRank (positionsY: List<Float>, positionY: Float): Int {
        var rank = 0

        for (item in 1..<positionsY.size) {
            val posYLower = positionsY[item - 1]
            val posYHigher = positionsY[item]
            val midPosY = posYLower + (posYHigher - posYLower) / 2
            if (positionY >= midPosY) {
                rank = item
            }
        }

        return rank
    }

    fun resort (ingredientsSorted: List<Ingredient>, oldPosition: Int, newPosition: Int): List<Ingredient> {
        val ingredientsResorted: MutableList<Ingredient> = ingredientsSorted.toMutableList()

        ingredientsSorted.forEachIndexed { index, ingredient ->
            if (index == oldPosition) {
                ingredientsResorted[newPosition] = ingredient
            } else if (index in (oldPosition + 1)..newPosition) {
                ingredientsResorted[index - 1] = ingredient
            } else if (index in newPosition..<oldPosition) {
                ingredientsResorted[index + 1] = ingredient
            } else {
                ingredientsResorted[index] = ingredient
            }
        }

        return ingredientsResorted
    }
}
