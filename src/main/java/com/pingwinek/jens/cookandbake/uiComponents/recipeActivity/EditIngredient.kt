package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.Utils
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.LabelledCheckBox

@Composable
fun EditIngredient(
    paddingValues: PaddingValues,
    ingredientName: String?,
    ingredientQuantity: Double?,
    ingredientQuantityVerbal: String?,
    ingredientUnity: String?,
    ingredientIsGroupHeader: Boolean,
    onCancel: () -> Unit,
    onSave: (String, Double?, String?, String?, Boolean) -> Unit
) {
    var ingredientNameTemp by remember {
        mutableStateOf(ingredientName)
    }
    var ingredientQuantityTemp by remember {
        mutableStateOf(ingredientQuantity)
    }
    var ingredientQuantityVerbalTemp by remember {
        mutableStateOf(ingredientQuantityVerbal)
    }
    var ingredientUnityTemp by remember {
        mutableStateOf(ingredientUnity)
    }
    var ingredientIsGroupHeaderTemp by remember {
        mutableStateOf(ingredientIsGroupHeader)
    }

    /*
    Temporarily save text value ending with a dot. Replace comma by dot. Ensure max. 1 dot in the number string.
     */
    var tmpQuantity: String by remember { mutableStateOf(Utils.quantityToString(ingredientQuantity)) }

    val isQuantityAsNumberMissing by remember {
        derivedStateOf { (ingredientQuantityTemp == null && !ingredientQuantityVerbalTemp.isNullOrEmpty()) }
    }

    val onIngredientQuantityTextFieldChange: (String?) -> Unit = { changedString ->
        changedString?.let { changedString ->
            val editedString = changedString.replace(",", ".").replace("..", ".")
            if (editedString.endsWith(".")) {
                tmpQuantity = editedString
            } else {
                try {
                    val changedDouble = Utils.quantityToDouble(editedString)
                    tmpQuantity = Utils.quantityToString(changedDouble)
                    ingredientQuantityTemp = changedDouble
                } catch (e: Exception) {
                    Log.e("EditIngredient", e.toString())
                }
            }
        }
    }

    val onSave: () -> Unit = {
        if (!ingredientNameTemp.isNullOrEmpty()) {
            onSave(
                ingredientNameTemp!!,
                ingredientQuantityTemp,
                ingredientQuantityVerbalTemp,
                ingredientUnityTemp,
                ingredientIsGroupHeaderTemp
            )
        }
    }

    EditPane(
        paddingValues = paddingValues,
        onCancel = onCancel,
        onSave = { if (!isQuantityAsNumberMissing) onSave() }
    ) {
        Column {
            TextField(
                value = ingredientNameTemp ?: "",
                label = {
                    Text(stringResource(R.string.ingredientName))
                },
                onValueChange = { changedString ->
                    ingredientNameTemp = changedString
                }
            )
            LabelledCheckBox(
                checked = ingredientIsGroupHeaderTemp,
                label = stringResource(R.string.isGroupHeader),
                onCheckedChange = {
                    changedBoolean ->
                    ingredientIsGroupHeaderTemp = changedBoolean
                }
            )

            if (! ingredientIsGroupHeaderTemp) {
                TextField(
                    value = tmpQuantity,
//                    value = Utils.quantityToString(ingredientQuantity),
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
                        onIngredientQuantityTextFieldChange(changedString)
                    }
                )
                TextField(
                    value = ingredientQuantityVerbalTemp ?: "",
                    label = {
                        Text(stringResource(R.string.quantity_verbal))
                    },
                    onValueChange = { changedString ->
                        ingredientQuantityVerbalTemp = changedString
                    }
                )
                TextField(
                    value = ingredientUnityTemp ?: "",
                    label = {
                        Text(stringResource(R.string.unity))
                    },
                    onValueChange = { changedString ->
                        ingredientUnityTemp = changedString
                    }
                )
            }
        }
    }
}
