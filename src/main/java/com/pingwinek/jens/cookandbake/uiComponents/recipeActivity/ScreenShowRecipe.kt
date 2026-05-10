package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.ImageInfo
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksScaffold

@Composable
fun ScreenShowRecipe(
    tabMode: TabMode,
    recipeTitle: String,
    recipeDescription: String,
    recipeInstruction: String,
    recipeHasAttachment: Boolean,
    attachedTags: List<Tag>,
    ingredients: List<Ingredient>,
    imageGalleryInfos: List<ImageInfo>,
    isLoadingAttachment: Boolean,
    deleteRecipe: () -> Unit,
    deleteIngredient: (Ingredient) -> Unit,
    attachDocument: (Uri) -> Unit,
    deleteAttachment: () -> Unit,
    loadAttachment: () -> Unit,
    bulkUpdateIngredients: (Map<Ingredient, Int>) -> Unit,
    onEditRecipe: () -> Unit,
    onEditInstruction: () -> Unit,
    onEditTags: () -> Unit,
    onEditIngredient: (String?) -> Unit,
    onImageSelected: (String) -> Unit,
    onAddImage: (Boolean) -> Unit,
    onTabeModeChange: (TabMode) -> Unit,
    onShareRecipe: () -> Unit,
    onHasShownSnackBar: () -> Unit,
    onFinish: () -> Unit,
    exceptionMessage: String?,
) {
    var ingredientsEditMode by remember { mutableStateOf(false) }
    val fabMode by remember(tabMode, ingredientsEditMode) {
        derivedStateOf {
            if (tabMode == TabMode.INGREDIENTS
                && !ingredientsEditMode) {
                FabMode.ADD_INGREDIENT
            } else if (tabMode == TabMode.IMAGE) {
                FabMode.ADD_IMAGE
            } else {
                FabMode.NONE
            }
        }
    }

    val fabIcon = Icons.Filled.Add
    val fabIconLabel = when (fabMode) {
        FabMode.ADD_INGREDIENT -> stringResource(R.string.plus_new_ingredient)
        FabMode.ADD_IMAGE -> "addImage"
        FabMode.NONE -> ""
    }

    val onFabClicked: () -> Unit = {
        when (fabMode) {
            FabMode.ADD_INGREDIENT -> {
                onEditIngredient(null)
            }
            FabMode.ADD_IMAGE -> {}
            FabMode.NONE -> {}
        }
    }

    val onIngredientFunctionsMode: (Boolean) -> Unit = {
        ingredientsEditMode = it
    }

    val optionBack = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.back,
        icon = Icons.AutoMirrored.Outlined.ArrowBack,
        onClick = onFinish
    )

    val optionShare = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.share,
        icon = Icons.Filled.Share,
        onClick = onShareRecipe
    )

    val fabMenuItems by remember(fabMode) {
        derivedStateOf {
            if (fabMode == FabMode.ADD_IMAGE) {
                listOf(
                    PingwinekCooksComposableHelpers.OptionItem(
                        labelResourceId = R.string.take_picture,
                        icon = Icons.Filled.Camera,
                        onClick = { onAddImage(true) }
                    ),
                    PingwinekCooksComposableHelpers.OptionItem(
                        labelResourceId = R.string.choose_from_gallery,
                        icon = Icons.Filled.Image,
                        onClick = { onAddImage(false) }
                    )
                )
            } else {
                listOf()
            }
        }
    }

    PingwinekCooksScaffold(
        title = "",
        optionItemLeft = optionBack,
        optionItemMid = optionShare,
        navigationBarVisible = false,
        showFab = (fabMode != FabMode.NONE),
        fabIcon = fabIcon,
        fabIconLabel = fabIconLabel,
        fabContainerColor = MaterialTheme.colorScheme.primary,
        fabIconColor = MaterialTheme.colorScheme.onPrimary,
        fabContainerSecondaryColor = MaterialTheme.colorScheme.primaryContainer,
        fabIconSecondaryColor = MaterialTheme.colorScheme.onPrimaryContainer,
        fabMenuItems = fabMenuItems,
        onFabClicked = onFabClicked,
        snackbarMessage = exceptionMessage,
        onHasShownSnackbar = onHasShownSnackBar
    ) { paddingValues ->
        ScaffoldShowRecipe(
            paddingValues = paddingValues,
            tabMode = tabMode,
            recipeTitle = recipeTitle,
            recipeDescription = recipeDescription,
            recipeInstruction = recipeInstruction,
            recipeHasAttachment = recipeHasAttachment,
            attachedTags = attachedTags,
            ingredients = ingredients,
            imageGalleryInfos = imageGalleryInfos,
            isLoadingAttachment = isLoadingAttachment,
            deleteRecipe = deleteRecipe,
            deleteIngredient = deleteIngredient,
            attachDocument = attachDocument,
            onDeleteDocument = deleteAttachment,
            onAttachmentClicked = loadAttachment,
            onChangeSortIngredient = bulkUpdateIngredients,
            onEditRecipe = onEditRecipe,
            onEditInstruction = onEditInstruction,
            onEditTags = onEditTags,
            onEditIngredient = onEditIngredient,
            onIngredientFunctionsMode = onIngredientFunctionsMode,
            onImageSelected = onImageSelected,
            onTabModeChange = onTabeModeChange,
        )
    }

}