package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane

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
    EditPane (
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

@Preview(
    device = "id:pixel_9",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewEditRecipe(@PreviewParameter(PPEditRecipe::class) parameters: Parameters4PreviewEditRecipe) {

    EditRecipe(
        parameters.paddingValues,
        parameters.recipeTitle,
        parameters.recipeDescription,
        parameters.onRecipeTitleChange,
        parameters.onRecipeDescriptionChange,
        parameters.onCancel,
        parameters.onSave
    )
}

class PPEditRecipe: PreviewParameterProvider<Parameters4PreviewEditRecipe> {
    override val values = sequenceOf(
        Parameters4PreviewEditRecipe(
            recipeTitle = "Title",
            recipeDescription = "Description"
        ),
        Parameters4PreviewEditRecipe(
            recipeTitle = "Title",
            recipeDescription = "Description"
        )
    )
}

data class Parameters4PreviewEditRecipe (
    val paddingValues: PaddingValues = PaddingValues.Absolute(),
    val recipeTitle: String,
    val recipeDescription: String,
    val onRecipeTitleChange: (String) -> Unit = {},
    val onRecipeDescriptionChange: (String) -> Unit = {},
    val onCancel: () -> Unit = {},
    val onSave: () -> Unit = {}
)