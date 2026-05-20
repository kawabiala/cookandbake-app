package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.BuildConfig
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.FileInfo
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.DeleteDialog
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.uiComponents.recipeActivity.EditIngredient
import com.pingwinek.jens.cookandbake.uiComponents.recipeActivity.EditRecipe
import com.pingwinek.jens.cookandbake.uiComponents.recipeActivity.EditTags
import com.pingwinek.jens.cookandbake.uiComponents.recipeActivity.Mode
import com.pingwinek.jens.cookandbake.uiComponents.recipeActivity.ScreenShowRecipe
import com.pingwinek.jens.cookandbake.uiComponents.recipeActivity.ShowImage
import com.pingwinek.jens.cookandbake.uiComponents.recipeActivity.TabMode
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel
import java.io.File


class RecipeActivity: AppCompatActivity() {

    private lateinit var recipeModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[RecipeViewModel::class.java]

        if (intent.hasExtra(EXTRA_RECIPE_ID)) {
            intent.extras?.getString(EXTRA_RECIPE_ID)?.let { id ->
                recipeModel.recipeId = id
            }
        }

        recipeModel.recipeAttachment.observe(this) {
            recipeModel.recipeAttachment.value?.let {
                showAttachment(it)
            }
        }

        setContent {
            PingwinekCooksAppTheme {

                val exceptionMessage by recipeModel.message.observeAsState()

                val recipe by recipeModel.recipeData.observeAsState()
                val isLoadingAttachment by recipeModel.isUpOrDownLoading.observeAsState()
                val availableTagData = recipeModel.availableTagListData.observeAsState()
                val availableTags = availableTagData.value ?: listOf()
                val attachedTagData = recipeModel.attachedTagListData.observeAsState()
                val attachedTags = attachedTagData.value ?: listOf()
                val ingredientData = recipeModel.ingredientListData.observeAsState()
                val ingredients = ingredientData.value ?: listOf()
                val imageGalleryInfosData = recipeModel.imageGalleryInfos.observeAsState()
                val imageGalleryInfos = imageGalleryInfosData.value ?: listOf()

                var mode by remember { mutableStateOf(if (recipeModel.recipeId != null) Mode.SHOW_RECIPE else Mode.EDIT_RECIPE)}
                var tabMode by remember { mutableStateOf(TabMode.INGREDIENTS) }

                var ingredientIdTemp: String? by remember {
                    mutableStateOf(null)
                }

                var imageIdTemp: String? by remember {
                    mutableStateOf(null)
                }

                when (mode) {
                    Mode.SHOW_RECIPE -> {

                        var uriTemp: Uri? by remember { mutableStateOf(null) }

                        val photoPickerLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.PickVisualMedia()
                        ) { uri ->
                            Log.i("Composable ScreenShowRecipe", "Gallery: uri $uri")
                            uri?.let {
                                recipeModel.addImage(uri)
                            }
                        }

                        val cameraLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.TakePicture()
                        ) { success ->
                            Log.i("Composable ScreenShowRecipe", "Camera: success $success")
                            if (success) {
                                uriTemp?.let {
                                    recipeModel.addImage(it)
                                }
                            }
                        }

                        val deleteRecipe: () -> Unit = {
                            recipeModel.deleteRecipe()
                            finish()
                        }

                        val onEditIngredient: (String?) -> Unit = { ingredientId ->
                            if (ingredientId != null) {
                                ingredientIdTemp = ingredientId
                            }

                            mode = Mode.EDIT_INGREDIENT
                        }

                        val onImageSelected: (String) -> Unit = { imageId ->
                            imageIdTemp = imageId
                            mode = Mode.SHOW_IMAGE
                        }

                        val onAddImage: (Boolean) -> Unit = { isTakePhoto ->
                            if (isTakePhoto) {
                                uriTemp = getTempUri()
                                uriTemp?.let {
                                    cameraLauncher.launch(it)
                                }
                            } else {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        }

                        ScreenShowRecipe(
                            tabMode = tabMode,
                            recipeTitle = recipe?.title ?: "",
                            recipeDescription = recipe?.description ?: "",
                            recipeInstruction = recipe?.instruction ?: "",
                            recipeHasAttachment = recipe?.hasAttachment ?: false,
                            attachedTags = attachedTags,
                            ingredients = ingredients,
                            imageGalleryInfos = imageGalleryInfos,
                            isLoadingAttachment = isLoadingAttachment ?: false,
                            deleteRecipe = deleteRecipe,
                            deleteIngredient = { ingredient -> recipeModel.deleteIngredient(ingredient) },
                            attachDocument = { uri -> recipeModel.attachDocument(uri) },
                            deleteAttachment = { recipeModel.deleteAttachment() },
                            loadAttachment = { recipeModel.loadAttachment() },
                            bulkUpdateIngredients = { map -> recipeModel.bulkUpdateIngredients(map) },
                            onEditRecipe = { mode = Mode.EDIT_RECIPE },
                            onEditTags = { mode = Mode.EDIT_TAGS },
                            onEditIngredient = onEditIngredient,
                            onImageSelected = onImageSelected,
                            onAddImage = onAddImage,
                            onTabModeChange = { changedTabMode -> tabMode = changedTabMode },
                            onShareRecipe = { startActivity(getShareRecipeIntent()) },
                            onHasShownSnackBar = { recipeModel.resetMessage() },
                            onFinish = { finish() },
                            exceptionMessage = exceptionMessage,
                        )
                    }

                    Mode.SHOW_IMAGE -> {

                        val imageInfo by remember(imageIdTemp) {
                            derivedStateOf { imageGalleryInfos.find { imageInfo -> imageInfo.imageId == imageIdTemp } }
                        }

                        var deleteImageId: String? by remember(imageIdTemp) {
                            mutableStateOf(null)
                        }

                        val deleteImage: () -> Unit = {
                            deleteImageId?.let {
                                recipeModel.deleteImage(it)
                            }
                            mode = Mode.SHOW_RECIPE
                        }

                        val updateImageName: (String) -> Unit = { imageName ->
                            recipeModel.updateImageName(imageName)
                        }

                        val optionBack = PingwinekCooksComposableHelpers.OptionItem(
                            labelResourceId = R.string.back,
                            icon = Icons.AutoMirrored.Rounded.ArrowBack,
                            onClick = { mode = Mode.SHOW_RECIPE }
                        )

                        val optionDelete = PingwinekCooksComposableHelpers.OptionItem(
                            labelResourceId = R.string.delete,
                            icon = Icons.Filled.Delete,
                            onClick = { deleteImageId = imageIdTemp }
                        )

                        if (deleteImageId != null) {
                            DeleteDialog(
                                message = stringResource(R.string.delete_image) + "?",
                                onClose = { deleteImageId = null },
                                onDelete = deleteImage
                            )
                        }

                        PingwinekCooksScaffold(
                            title = "",
                            optionItemLeft = optionBack,
                            optionItemRight = optionDelete,
                            navigationBarVisible = false,
                            snackbarMessage = exceptionMessage,
                            onHasShownSnackbar = { recipeModel.resetMessage() }
                        ) { paddingValues ->
                            ShowImage(
                                paddingValues = paddingValues,
                                imageInfo = imageInfo,
                                updateImageName = updateImageName
                            )
                        }
                    }

                    Mode.EDIT_RECIPE -> {

                        val onSave: (String, String, String) -> Unit = { recipeTitle, recipeDescription, instruction ->
                            recipeModel.saveRecipe(recipeTitle, recipeDescription, instruction)
                            mode = Mode.SHOW_RECIPE
                        }

                        val onCancel: () -> Unit = {
                            if (recipeModel.recipeId != null) {
                                mode = Mode.SHOW_RECIPE
                            } else {
                                finish()
                            }
                        }

                        PingwinekCooksScaffold(
                            title = "",
                            navigationBarVisible = false,
                            snackbarMessage = exceptionMessage,
                            onHasShownSnackbar = { recipeModel.resetMessage() }
                        ) { paddingValues ->
                            EditRecipe(
                                paddingValues = paddingValues,
                                recipeTitle = recipe?.title ?: "",
                                recipeDescription = recipe?.description ?: "",
                                instruction = recipe?.instruction ?: "",
                                onCancel = onCancel,
                                onSave = onSave
                            )
                        }
                    }

                    Mode.EDIT_INGREDIENT -> {

                        val ingredient by remember(ingredientIdTemp) {
                            derivedStateOf { findIngredient(ingredients, ingredientIdTemp) }
                        }

                        val ingredientSortMax by remember(ingredients) {
                            derivedStateOf {
                                ingredients.maxOfOrNull { ingredient ->
                                    ingredient.sort
                                }
                            }
                        }

                        val onCancel: () -> Unit = {
                            ingredientIdTemp = null
                            mode = Mode.SHOW_RECIPE
                        }

                        val onSave: (String, Double?, String?, String?, Boolean) -> Unit = { name, quantity, quantityVerbal, unity, isGroupHeader ->
                                recipeModel.saveIngredient(
                                    id = ingredientIdTemp,
                                    name = name,
                                    quantity = quantity,
                                    quantityVerbal = quantityVerbal,
                                    unity = unity,
                                    sort = ingredient?.sort ?: ingredientSortMax ?: -1,
                                    isGroupHeader = isGroupHeader
                                )
                                ingredientIdTemp = null
                                mode = Mode.SHOW_RECIPE
                        }

                        PingwinekCooksScaffold(
                            title = "",
                            navigationBarVisible = false,
                            snackbarMessage = exceptionMessage,
                            onHasShownSnackbar = { recipeModel.resetMessage() }
                        ) { paddingValues ->
                            EditIngredient(
                                paddingValues = paddingValues,
                                ingredientName = ingredient?.name,
                                ingredientQuantity = ingredient?.quantity,
                                ingredientQuantityVerbal = ingredient?.quantityVerbal,
                                ingredientUnity = ingredient?.unity,
                                ingredientIsGroupHeader = ingredient?.isGroupHeader ?: false,
                                onCancel = onCancel,
                                onSave = onSave,
                            )
                        }
                    }

                    Mode.EDIT_TAGS -> {

                        val onSave: (Map<Tag, Boolean>) -> Unit = { map ->
                            val tagsToChange = map.filterValues { selected -> selected }.keys.toList()

                            recipeModel.saveTags(tagsToChange)
                            mode = Mode.SHOW_RECIPE
                        }

                        PingwinekCooksScaffold(
                            title = "",
                            navigationBarVisible = false,
                            snackbarMessage = exceptionMessage,
                            onHasShownSnackbar = { recipeModel.resetMessage() }
                        ) { paddingValues ->
                            EditTags(
                                paddingValues = paddingValues,
                                availableTags = availableTags,
                                attachedTags = attachedTags,
                                onSave = onSave,
                                onClose = { mode = Mode.SHOW_RECIPE }
                            )
                        }
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        recipeModel.loadData()
    }

    private fun getShareRecipeIntent(): Intent {
        return Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_HTML_TEXT,
                    recipeModel.getShareableRecipe()?.getHtml(
                        resources.getString(R.string.pingwinekcooks)
                    ))
                putExtra(
                    Intent.EXTRA_TEXT,
                    recipeModel.getShareableRecipe()?.getPlainText(
                        resources.getString(R.string.pingwinekcooks)
                    ))
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    recipeModel.getShareableRecipe()?.subject)
                type ="text/plain"
            },
            null
        )
    }

    private fun findIngredient(ingredients: List<Ingredient>, ingredientId: String?): Ingredient? {
        return ingredients.find { ingredient ->
            ingredient.id == ingredientId
        }
    }

    private fun getTempUri() : Uri {
        val file = File.createTempFile("tmp_image_file", ".png", applicationContext.cacheDir)
        return getUri(file)
    }

    private fun getUri(file: File): Uri {
        return FileProvider.getUriForFile(
            applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
    }

    private fun showAttachment(fileInfo: FileInfo) {
        val uri = getUri(fileInfo.file)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.setDataAndType(uri, fileInfo.contentType)

        startActivity(intent)
    }
}