package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall
import com.pingwinek.jens.cookandbake.uiComponents.spacing
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel

@Composable
fun EditRecipe(
    paddingValues: PaddingValues,
    recipeTitle: String,
    recipeDescription: String,
    tags: List<RecipeViewModel.TagHelper>,
    onRecipeTitleChange: (String) -> Unit,
    onRecipeDescriptionChange: (String) -> Unit,
    onAddLabel: () -> Unit,
    onDeleteLabel: (RecipeViewModel.TagHelper) -> Unit,
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

            SpacerSmall()

            HorizontalDivider()

            SpacerSmall()

            Text(
                text = stringResource(R.string.labels),
                style = MaterialTheme.typography.headlineMedium
            )

            SpacerSmall()

            LazyColumn(
                modifier = Modifier
                    .padding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom).asPaddingValues()),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spacerSmall)
            ) {
                items(
                    items = tags.filter { tag -> ! tag.isDeleted },
                    key = { tag -> tag.tagID }
                ) { tagHelper ->

                    InputChip(
                        selected = true,
                        onClick = { onDeleteLabel(tagHelper) },
                        label = {
                            Text(
                                text = tagHelper.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier
                            .width(180.dp),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            Icon(
                                Icons.Outlined.Close,
                                stringResource(R.string.delete)
                            )
                        }
                    )

                }

                item {
                    SpacerSmall()

                    FilledIconButton(
                        onClick = onAddLabel
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            stringResource(R.string.add_label)
                        )
                    }
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
                RecipeViewModel.TagHelper("", "Label 1", "", 0),
                RecipeViewModel.TagHelper("", "Label 2", "", 1)
            )
        )
    )
}

data class Parameters4PreviewEditRecipe (
    val paddingValues: PaddingValues = PaddingValues.Absolute(),
    val recipeTitle: String,
    val recipeDescription: String,
    val labels: List<RecipeViewModel.TagHelper>,
    val onRecipeTitleChange: (String) -> Unit = {},
    val onRecipeDescriptionChange: (String) -> Unit = {},
    val onAddLabel: () -> Unit = {},
    val onDeleteLabel: (RecipeViewModel.TagHelper) -> Unit = {},
    val onCancel: () -> Unit = {},
    val onSave: () -> Unit = {}
)