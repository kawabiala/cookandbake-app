package com.pingwinek.jens.cookandbake.composables.recipeActivity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables

@Composable
fun EditRecipe(
    paddingValues: PaddingValues,
    recipeTitle: String,
    recipeDescription: String,
    onRecipeTitleChange: (String) -> Unit,
    onRecipeDescriptionChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    PingwinekCooksComposables.EditPane (
        paddingValues = paddingValues,
        onCancel = onCancel,
        onSave = onSave
    ) {
        Column {
            TextField(
                value = recipeTitle,
                label = {
                    Text(stringResource(R.string.recipe_title))
                },
                onValueChange = { changedString ->
                    onRecipeTitleChange(changedString)
                }
            )
            TextField(
                value = recipeDescription,
                label = {
                    Text(stringResource(R.string.recipe_description))
                },
                onValueChange = { changedString ->
                    onRecipeDescriptionChange(changedString)
                }
            )
        }
    }
}
