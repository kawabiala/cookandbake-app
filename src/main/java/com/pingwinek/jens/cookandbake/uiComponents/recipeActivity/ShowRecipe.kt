package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksDropDown
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksTabElement
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerMedium
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers

@Composable
fun ShowRecipe(
    paddingValues: PaddingValues,
    tabMode: TabMode,
    onIngredientsFunctionsMode: (Boolean) -> Unit,
    recipeTitle: String,
    recipeDescription: String,
    ingredients: List<Ingredient>,
    instruction: String,
    hasAttachment: Boolean,
    isAttachmentLoading: Boolean,
    onEditRecipe: () -> Unit,
    onDeleteRecipe: () -> Unit,
    onAttachDocument: () -> Unit,
    onDeleteDocument: () -> Unit,
    onAttachmentClicked: () -> Unit,
    onEditIngredient: (ingredientId: String) -> Unit,
    onDeleteIngredient: (ingredientId: String) -> Unit,
    onChangeSortIngredient: (Map<Ingredient, Int>) -> Unit,
    onEditInstruction: () -> Unit,
    onTabModeChange: (TabMode) -> Unit
) {
    var showButtons by remember(recipeTitle) { mutableStateOf(recipeTitle.isEmpty()) }
    var expanded by remember { mutableStateOf(false) }

    val optionUpdate = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.update_attachment,
        icon = Icons.Filled.Attachment,
        onClick = {
            onAttachDocument()
            expanded = false
        }
    )

    val optionDelete = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.delete_attachment,
        icon = Icons.Filled.Delete,
        onClick = {
            onDeleteDocument()
            expanded = false
        }
    )

    Column {
        SpacerSmall()

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
                    //fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = recipeTitle
                )

                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = recipeDescription
                )

                if (isAttachmentLoading) {
                    CircularProgressIndicator()
                } else if (hasAttachment) {
                    IconButton(
                        onClick = onAttachmentClicked
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Attachment,
                            contentDescription = stringResource(R.string.show_attachment)
                        )
                    }
                }
            }

            if (showButtons) {
                IconButton(onClick = onEditRecipe) {
                    Icon(Icons.Filled.Edit, stringResource(R.string.edit_recipe))
                }

                IconButton(onClick = onDeleteRecipe) {
                    Icon(Icons.Filled.Delete, stringResource(R.string.delete_recipe))
                }

                if (hasAttachment) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Filled.MoreVert, stringResource(R.string.more))
                        PingwinekCooksDropDown(
                            expanded = expanded,
                            options = listOf(optionUpdate, optionDelete)
                        ) {

                        }
                    }
                } else {
                    IconButton(onClick = onAttachDocument) {
                        Icon(Icons.Filled.Attachment, stringResource(R.string.attach_document))
                    }
                }
            }
        }

        SpacerMedium()

        PingwinekCooksTabElement(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.tertiaryContainer),
            selectedItem = tabMode.ordinal,
            onSelectedItemChange = { item -> onTabModeChange(TabMode.entries[item]) },
            tabItems = mutableListOf<PingwinekCooksComposableHelpers.PingwinekCooksTabItem>().apply {
                add(
                    PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
                    tabNameId = R.string.ingredients,
                    tabIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                    content = {
                        ShowIngredients(
                            paddingValues = paddingValues,
                            ingredients = ingredients,
                            onIngredientsFunctionsMode = onIngredientsFunctionsMode,
                            onEditIngredient = onEditIngredient,
                            onDeleteIngredient = onDeleteIngredient,
                            onChangeSort = onChangeSortIngredient
                        )
                    }
                ))
                add(
                    PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
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
                    PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
                    tabNameId = R.string.gallery,
                    tabIcon = Icons.Filled.Image,
                    content = {
                    }
                ))
            }
        )
    }
}
