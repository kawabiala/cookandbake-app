package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.ImageInfo
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksTabElement
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun ScaffoldShowRecipe(
    paddingValues: PaddingValues,
    tabMode: TabMode,
    recipeTitle: String,
    recipeDescription: String,
    recipeInstruction: String,
    recipeHasAttachment: Boolean,
    attachedTags: List<Tag>,
    ingredients: List<Ingredient>,
    imageGalleryInfos: List<ImageInfo>,
    isLoadingAttachment: Boolean,
    onIngredientFunctionsMode: (Boolean) -> Unit,
    onDeleteIngredient: (String) -> Unit,
    onAttachmentClicked: () -> Unit,
    onChangeSortIngredient: (Map<Ingredient, Int>) -> Unit,
    onEditIngredient: (String) -> Unit,
    onImageSelected: (String) -> Unit,
    onTabModeChange: (TabMode) -> Unit,
) {

    val sortedTags by remember(attachedTags) {
        derivedStateOf { attachedTags.sortedBy { it.sort }.map { it.label } }
    }

    val tabItems = remember(ingredients, recipeInstruction, imageGalleryInfos) {
        listOf(
            PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
                tabNameId = R.string.ingredients,
                tabIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                content = {
                    ShowIngredients(
                        ingredients = ingredients,
                        onIngredientsFunctionsMode = onIngredientFunctionsMode,
                        onEditIngredient = onEditIngredient,
                        onDeleteIngredient = onDeleteIngredient,
                        onChangeSort = onChangeSortIngredient
                    )
                }
            ),
            PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
                tabNameId = R.string.instruction,
                tabIcon = Icons.Filled.Receipt,
                content = {
                    ShowInstruction(
                        paddingValues = paddingValues,
                        instruction = recipeInstruction,
                    )
                }
            ),
            PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
                tabNameId = R.string.gallery,
                tabIcon = Icons.Filled.Image,
                content = {
                    ShowImageGallery(
                        images = imageGalleryInfos,
                        onSelect = onImageSelected
                    )
                }
            )
        )
    }

    val paddingLeft by remember(paddingValues, tabMode) {
        derivedStateOf {
            if (tabMode == TabMode.IMAGE) {
                0.dp
            } else {
                paddingValues.calculateStartPadding(LayoutDirection.Ltr)
            }
        }
    }

    val paddingRight by remember(paddingValues, tabMode) {
        derivedStateOf {
            if (tabMode == TabMode.IMAGE) {
                0.dp
            } else {
                paddingValues.calculateEndPadding(LayoutDirection.Ltr)
            }
        }
    }

    Column {
        SpacerSmall()

        ShowRecipe(
            paddingValues = paddingValues,
            recipeTitle = recipeTitle,
            recipeDescription = recipeDescription,
            labels = sortedTags,
            hasAttachment = recipeHasAttachment,
            isAttachmentLoading = isLoadingAttachment,
            onAttachmentClicked = onAttachmentClicked,
        )

        SpacerSmall()

        PingwinekCooksTabElement(
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            paddingLeft = paddingLeft,
            paddingRight = paddingRight,
            selectedItem = tabMode.ordinal,
            onSelectedItemChange = { item -> onTabModeChange(TabMode.entries[item]) },
            tabItems = tabItems
        )
    }
}