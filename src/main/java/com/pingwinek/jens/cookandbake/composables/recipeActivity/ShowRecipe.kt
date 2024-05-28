package com.pingwinek.jens.cookandbake.composables.recipeActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.activities.RecipeActivity
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.models.Ingredient

@Composable
fun ShowRecipe(
    paddingValues: PaddingValues,
    tabMode: RecipeActivity.TabMode,
    recipeTitle: String,
    recipeDescription: String,
    ingredients: List<Ingredient>,
    instruction: String,
    onEditRecipe: () -> Unit,
    onDeleteRecipe: () -> Unit,
    onAttachDocument: () -> Unit,
    onEditIngredient: (ingredientId: String) -> Unit,
    onDeleteIngredient: (ingredientId: String) -> Unit,
    onChangeSortIngredient: (Map<Ingredient, Int>) -> Unit,
    onEditInstruction: () -> Unit,
    onTabModeChange: (RecipeActivity.TabMode) -> Unit
) {
    var showButtons by remember(recipeTitle) { mutableStateOf(recipeTitle.isEmpty()) }

    Column {
        PingwinekCooksComposables.SpacerSmall()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(70f)
                    .clickable { showButtons = !showButtons },
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = recipeTitle
                )
                Text(
                    text = recipeDescription
                )
            }

            if (showButtons) {
                IconButton(onClick = onEditRecipe) {
                    Icon(Icons.Filled.Edit, stringResource(R.string.edit_recipe))
                }
                IconButton(onClick = onDeleteRecipe) {
                    Icon(Icons.Filled.Delete, stringResource(R.string.delete_recipe))
                }
                IconButton(onClick = onAttachDocument) {
                    Icon(Icons.Filled.Attachment, stringResource(R.string.attach_document))
                }
            }
        }

        PingwinekCooksComposables.SpacerMedium()

        PingwinekCooksComposables.PingwinekCooksTabElement(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.tertiaryContainer),
            selectedItem = tabMode.ordinal,
            onSelectedItemChange = { item -> onTabModeChange(RecipeActivity.TabMode.entries[item]) },
            tabItems = mutableListOf<PingwinekCooksComposables.PingwinekCooksTabItem>().apply {
                add(
                    PingwinekCooksComposables.PingwinekCooksTabItem(
                    tabNameId = R.string.ingredients,
                    tabIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                    content = {
                        ShowIngredients(
                            paddingValues = paddingValues,
                            ingredients = ingredients,
                            onEditIngredient = onEditIngredient,
                            onDeleteIngredient = onDeleteIngredient,
                            onChangeSort = onChangeSortIngredient
                        )
                    }
                ))
                add(
                    PingwinekCooksComposables.PingwinekCooksTabItem(
                    tabNameId = R.string.instruction,
                    tabIcon = Icons.Filled.Receipt,
                    content = {
                        ShowInstruction(
                            paddingValues = paddingValues,
                            instruction = instruction,
                            onEditInstruction = onEditInstruction
                        )
                    }
                ))
                add(
                    PingwinekCooksComposables.PingwinekCooksTabItem(
                    tabNameId = R.string.pdf,
                    tabIcon = Icons.Filled.FilePresent,
                    content = {
                    }
                ))
            }
        )
    }
}
