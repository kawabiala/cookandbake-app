package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
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
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksDropDown
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksTabElement
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun ShowRecipe(
    paddingValues: PaddingValues,
    tabMode: TabMode,
    onIngredientsFunctionsMode: (Boolean) -> Unit,
    recipeTitle: String,
    recipeDescription: String,
    labels: List<String>,
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
    onEditTags: () -> Unit,
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

        Column(
            modifier = Modifier
                .clickable { showButtons = !showButtons }
        ) {

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
                ) {
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = recipeTitle
                    )

                    if (recipeDescription.isNotEmpty()) {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = recipeDescription
                        )
                    }

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
                                expanded = false
                            }
                        }
                    } else {
                        IconButton(onClick = onAttachDocument) {
                            Icon(Icons.Filled.Attachment, stringResource(R.string.attach_document))
                        }
                    }
                }
            }

            SpacerSmall()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (labels.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        labels.forEach { label ->
                            InputChip(
                                selected = true,
                                onClick = { showButtons = !showButtons },
                                label = {
                                    Text(
                                        text = label,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier
                                    .requiredWidthIn(60.dp, 90.dp),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }

                if (showButtons) {
                    FilledTonalIconButton(
                        onClick = onEditTags
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.Label,
                            stringResource(R.string.manage_labels)
                        )
                    }
                }
            }
        }

        SpacerSmall()

        PingwinekCooksTabElement(
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            paddingLeft = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
            paddingRight = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
            selectedItem = tabMode.ordinal,
            onSelectedItemChange = { item -> onTabModeChange(TabMode.entries[item]) },
            tabItems = mutableListOf<PingwinekCooksComposableHelpers.PingwinekCooksTabItem>().apply {
                add(
                    PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
                    tabNameId = R.string.ingredients,
                    tabIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                    content = {
                        ShowIngredients(
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
                /*
                add(
                    PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
                    tabNameId = R.string.gallery,
                    tabIcon = Icons.Filled.Image,
                    content = {
                    }
                ))*/
            }
        )
    }
}
