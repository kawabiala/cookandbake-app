package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
    onEditTags: () -> Unit,
    onEditIngredient: (String?) -> Unit,
    onImageSelected: (String) -> Unit,
    onAddImage: (Boolean) -> Unit,
    onTabModeChange: (TabMode) -> Unit,
    onShareRecipe: () -> Unit,
    onHasShownSnackBar: () -> Unit,
    onFinish: () -> Unit,
    exceptionMessage: String?,
) {
    var deleteTarget by remember {
        mutableStateOf<DeleteTarget>(DeleteTarget.NONE)
    }

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

    val attachmentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            attachDocument(uri)
        }
    }

    val fabIcon = Icons.Filled.Add
    val fabIconLabel = when (fabMode) {
        FabMode.ADD_INGREDIENT -> stringResource(R.string.plus_new_ingredient)
        FabMode.ADD_IMAGE -> "addImage"
        FabMode.NONE -> ""
    }

    val onIngredientFunctionsMode: (Boolean) -> Unit = {
        ingredientsEditMode = it
    }

    val onAttachDocument: () -> Unit = {
        attachmentPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
    }

    val onCloseDeleteDialog: () -> Unit = {
        deleteTarget = DeleteTarget.NONE
    }

    val onDelete: (deleteTarget: DeleteTarget) -> Unit = { target ->
        when (target) {
            is DeleteTarget.NONE -> {}
            is DeleteTarget.RECIPE -> {
                deleteRecipe()
            }
            is DeleteTarget.ATTACHMENT -> {
                deleteAttachment()
            }
            is DeleteTarget.INGREDIENT -> {
                deleteIngredient(target.ingredient)
                onIngredientFunctionsMode(false)
            }
            is DeleteTarget.IMAGE -> {}
        }
        deleteTarget = DeleteTarget.NONE
    }

    val onDeleteIngredient: (ingredientId: String) -> Unit = { id ->
        val deleteIngredient = ingredients.find { ingredient ->
            ingredient.id == id
        }
        if (deleteIngredient != null) {
            deleteTarget = DeleteTarget.INGREDIENT(deleteIngredient)
        } else  {
            Log.e("ScaffoldShowRecipe", "ingredient not found")
            deleteTarget = DeleteTarget.NONE
        }
    }

    val onDeleteRecipe: () -> Unit = {
        deleteTarget = DeleteTarget.RECIPE
    }

    val onDeleteAttachment: () -> Unit = {
        deleteTarget = DeleteTarget.ATTACHMENT
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

    val optionBack = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.back,
        icon = Icons.AutoMirrored.Outlined.ArrowBack,
        onClick = onFinish
    )

    val optionEditRecipe = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.edit_recipe,
        icon = Icons.Filled.Edit,
        onClick = onEditRecipe
    )

    val optionShare = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.share,
        icon = Icons.Filled.Share,
        onClick = onShareRecipe
    )

    val optionDeleteRecipe = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.delete_recipe,
        icon = Icons.Filled.Delete,
        onClick = onDeleteRecipe
    )

    val optionAttachDocument = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.attach_document,
        icon = Icons.Filled.Add,
        onClick = onAttachDocument
    )

    val optionUpdateDocument = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.update_attachment,
        icon = Icons.Filled.Attachment,
        onClick = onAttachDocument
    )

    val optionDeleteDocument = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.delete_attachment,
        icon = Icons.Filled.Delete,
        onClick = onDeleteAttachment
    )

    val optionEditTags = PingwinekCooksComposableHelpers.OptionItem(
        labelResourceId = R.string.manage_labels,
        icon = Icons.AutoMirrored.Outlined.Label,
        onClick = onEditTags
    )

    val dropDownOptions = mutableListOf(
        optionShare,
        optionDeleteRecipe,
        optionEditTags,
    ).apply {
        if (recipeHasAttachment) {
            add(optionUpdateDocument)
            add(optionDeleteDocument)
        } else {
            add(optionAttachDocument)
        }
    }.toList()

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

    if (deleteTarget != DeleteTarget.NONE) {
        DeleteDialog(
            deleteTarget = deleteTarget,
            onClose = onCloseDeleteDialog,
            onDelete = onDelete
        )
    }

    PingwinekCooksScaffold(
        title = "",
        optionItemLeft = optionBack,
        optionItemMid = optionEditRecipe,
        showDropDown = true,
        dropDownOptions = dropDownOptions,
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
            onAttachmentClicked = loadAttachment,
            onChangeSortIngredient = bulkUpdateIngredients,
            onEditIngredient = onEditIngredient,
            onDeleteIngredient = onDeleteIngredient,
            onIngredientFunctionsMode = onIngredientFunctionsMode,
            onImageSelected = onImageSelected,
            onTabModeChange = onTabModeChange,
        )
    }
}

sealed class DeleteTarget(val messageId: Int? = null) {
    object NONE: DeleteTarget()
    object RECIPE: DeleteTarget(R.string.delete_recipe)
    object ATTACHMENT: DeleteTarget(R.string.delete_attachment)
    data class INGREDIENT(val ingredient: Ingredient): DeleteTarget(R.string.delete_ingredient)
    object IMAGE: DeleteTarget()
}