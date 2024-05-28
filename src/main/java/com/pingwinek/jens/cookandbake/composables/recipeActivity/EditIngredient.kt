package com.pingwinek.jens.cookandbake.composables.recipeActivity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.utils.Utils

@Composable
fun EditIngredient(
    paddingValues: PaddingValues,
    ingredientName: String?,
    ingredientQuantity: Double?,
    ingredientQuantityVerbal: String?,
    ingredientUnity: String?,
    onIngredientNameChange: (String) -> Unit,
    onIngredientQuantityChange: (Double?) -> Unit,
    onIngredientQuantityVerbalChange: (String) -> Unit,
    onIngredientUnityChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val isQuantityAsNumberMissing = (ingredientQuantity == null && !ingredientQuantityVerbal.isNullOrEmpty())

    PingwinekCooksComposables.EditPane(
        paddingValues = paddingValues,
        onCancel = onCancel,
        onSave = { if (!isQuantityAsNumberMissing) onSave() }
    ) {
        Column {
            TextField(
                value = ingredientName ?: "",
                label = {
                    Text(stringResource(R.string.ingredientName))
                },
                onValueChange = { changedString ->
                    onIngredientNameChange(changedString)
                }
            )
            TextField(
                value = Utils.quantityToString(ingredientQuantity),
                label = {
                    Text(stringResource(R.string.quantity_number))
                },
                isError = isQuantityAsNumberMissing,
                supportingText = {
                    if (isQuantityAsNumberMissing) {
                        Text(stringResource(R.string.ingredient_quantity_unequal_verbal))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { changedString ->
                    onIngredientQuantityChange(Utils.quantityToDouble(changedString))
                }
            )
            TextField(
                value = ingredientQuantityVerbal ?: "",
                label = {
                    Text(stringResource(R.string.quantity_verbal))
                },
                onValueChange = { changedString ->
                    onIngredientQuantityVerbalChange(changedString)
                }
            )
            TextField(
                value = ingredientUnity ?: "",
                label = {
                    Text(stringResource(R.string.unity))
                },
                onValueChange = { changedString ->
                    onIngredientUnityChange(changedString)
                }
            )
        }
    }
}
