package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Tag4Recipe
import com.pingwinek.jens.cookandbake.models.Tag4RecipeFB
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksTag
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun EditRecipe(
    paddingValues: PaddingValues,
    recipeTitle: String,
    recipeDescription: String,
    tags: List<Tag4Recipe>,
    onRecipeTitleChange: (String) -> Unit,
    onRecipeDescriptionChange: (String) -> Unit,
    onAddLabel: () -> Unit,
    onDeleteLabel: (Tag4Recipe) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val fabArrangement = if(tags.isNotEmpty()) {
        Arrangement.End
    } else {
        Arrangement.Start
    }

    EditPane (
        paddingValues = paddingValues,
        cancelButtonColors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.inversePrimary,
            contentColor = MaterialTheme.colorScheme.primary
        ),
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

            SpacerSmall()

            HorizontalDivider()

            SpacerSmall()

            Text(
                text = stringResource(R.string.labels),
                style = MaterialTheme.typography.headlineMedium
            )

            SpacerSmall()

            tags.forEachIndexed { index, tag4Recipe ->

                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 5.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    )

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PingwinekCooksTag(tag4Recipe.label)

                    IconButton(
                        onClick = { onDeleteLabel(tag4Recipe) }
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            stringResource(R.string.delete_label)
                        )
                    }
                }
            }

            SpacerSmall()

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = fabArrangement
            ) {
                FloatingActionButton(
                    onClick = onAddLabel
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        stringResource(R.string.add_label)
                    )
                }
            }
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
        parameters.labels,
        parameters.onRecipeTitleChange,
        parameters.onRecipeDescriptionChange,
        parameters.onAddLabel,
        parameters.onDeleteLabel,
        parameters.onCancel,
        parameters.onSave
    )
}

class PPEditRecipe: PreviewParameterProvider<Parameters4PreviewEditRecipe> {
    override val values = sequenceOf(
        Parameters4PreviewEditRecipe(
            recipeTitle = "Title",
            recipeDescription = "Description",
            labels = listOf()
        ),
        Parameters4PreviewEditRecipe(
            recipeTitle = "Title",
            recipeDescription = "Description",
            labels = listOf(
                Tag4RecipeFB("Label 1", "0"),
                Tag4RecipeFB("Label 2", "1")
            )
        )
    )
}

data class Parameters4PreviewEditRecipe (
    val paddingValues: PaddingValues = PaddingValues.Absolute(),
    val recipeTitle: String,
    val recipeDescription: String,
    val labels: List<Tag4Recipe>,
    val onRecipeTitleChange: (String) -> Unit = {},
    val onRecipeDescriptionChange: (String) -> Unit = {},
    val onAddLabel: () -> Unit = {},
    val onDeleteLabel: (Tag4Recipe) -> Unit = {},
    val onCancel: () -> Unit = {},
    val onSave: () -> Unit = {}
)