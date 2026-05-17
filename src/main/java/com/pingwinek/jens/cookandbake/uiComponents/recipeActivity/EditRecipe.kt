package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun EditRecipe(
    paddingValues: PaddingValues,
    recipeTitle: String,
    recipeDescription: String,
    instruction: String,
    onCancel: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var recipeTitleTmp by remember {
        mutableStateOf(recipeTitle)
    }

    var recipeDescriptionTmp by remember {
        mutableStateOf(recipeDescription)
    }

    var instructionTemp by remember {
        mutableStateOf(instruction)
    }

    val isSaveEnabled by remember {
        derivedStateOf {
            recipeTitleTmp.isNotEmpty()
        }
    }

    val onSave: () -> Unit = {
        if (recipeTitleTmp.isNotEmpty()) {
            onSave(recipeTitleTmp, recipeDescriptionTmp, instructionTemp)
        }
    }

    EditPane (
        paddingValues = paddingValues,
        isSaveEnabled = isSaveEnabled,
        onCancel = onCancel,
        onSave = onSave
    ) {
        Column {
            TextField(
                value = recipeTitleTmp,
                label = {
                    Text(stringResource(R.string.recipe_title))
                },
                onValueChange = { changedString ->
                    recipeTitleTmp = changedString
                }
            )

            TextField(
                value = recipeDescriptionTmp,
                label = {
                    Text(stringResource(R.string.recipe_description))
                },
                onValueChange = { changedString ->
                    recipeDescriptionTmp = changedString
                }
            )

            SpacerSmall()

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                value = instructionTemp,
                label = {
                    Text(stringResource(R.string.instruction))
                },
                minLines = 2,
                onValueChange = { changedString ->
                    instructionTemp = changedString
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
        parameters.instruction,
        parameters.onCancel,
        parameters.onSave
    )
}

class PPEditRecipe: PreviewParameterProvider<Parameters4PreviewEditRecipe> {
    override val values = sequenceOf(
        Parameters4PreviewEditRecipe(
            recipeTitle = "Title",
            recipeDescription = "Description",
            instruction = "Instruction"
        ),
        Parameters4PreviewEditRecipe(
            recipeTitle = "Title",
            recipeDescription = "Description",
            instruction = "Instruction"
        )
    )
}

data class Parameters4PreviewEditRecipe (
    val paddingValues: PaddingValues = PaddingValues.Absolute(),
    val recipeTitle: String,
    val recipeDescription: String,
    val instruction: String,
    val onCancel: () -> Unit = {},
    val onSave: (String, String, String) -> Unit = { recipeTitle, recipeDescription, instruction -> }
)