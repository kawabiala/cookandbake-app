package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.composables.recipeActivity.EditIngredient
import com.pingwinek.jens.cookandbake.composables.recipeActivity.EditInstruction
import com.pingwinek.jens.cookandbake.composables.recipeActivity.EditRecipe
import com.pingwinek.jens.cookandbake.composables.recipeActivity.ShowRecipe
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel


class RecipeActivity: AppCompatActivity() {

    private enum class Mode {
        SHOW_RECIPE,
        EDIT_RECIPE,
        EDIT_INGREDIENT,
        EDIT_INSTRUCTION
    }
    private enum class FabMode { NONE, ADD_INGREDIENT }
    enum class TabMode { INGREDIENTS, INSTRUCTION, PDF }
    private enum class Delete { NONE, RECIPE, INGREDIENT }

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

        setContent {
            PingwinekCooksAppTheme {

                var mode by remember { mutableStateOf(Mode.SHOW_RECIPE)}
                var tabMode by remember { mutableStateOf(TabMode.INGREDIENTS) }
                val fabMode by remember(mode, tabMode) { derivedStateOf {
                        if (mode == Mode.SHOW_RECIPE && tabMode == TabMode.INGREDIENTS) {
                            FabMode.ADD_INGREDIENT
                        } else {
                            FabMode.NONE
                        }
                    } 
                }

                val recipeData = recipeModel.recipeData.observeAsState()
                val ingredientData = recipeModel.ingredientListData.observeAsState()
                val ingredients = ingredientData.value ?: listOf()

                val optionBack = PingwinekCooksComposables.OptionItem(
                    labelResourceId = R.string.back,
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    onClick = { finish() }
                )

                val optionShare = PingwinekCooksComposables.OptionItem(
                    labelResourceId = R.string.share,
                    icon = Icons.Filled.Share,
                    onClick = { }
                )

                PingwinekCooksScaffold(
                    title = "",
                    optionItemLeft = if (mode == Mode.SHOW_RECIPE) optionBack else null,
                    optionItemMid = if (mode == Mode.SHOW_RECIPE) optionShare else null,
                    showFab = (fabMode != FabMode.NONE),
                    fabIconLabel = if (fabMode == FabMode.ADD_INGREDIENT) getString(R.string.plus_new_ingredient) else "",
                    fabIcon = Icons.Filled.Add,
                    onFabClicked = {
                        if (fabMode == FabMode.ADD_INGREDIENT) {
                            mode = Mode.EDIT_INGREDIENT
                        }
                    }
                ) { paddingValues ->
                    ScaffoldContent(
                        paddingValues = paddingValues,
                        mode = mode,
                        tabMode = tabMode,
                        recipeTitle = recipeData.value?.title ?: "",
                        recipeDescription = recipeData.value?.description ?: "",
                        ingredients = ingredients,
                        instruction = recipeData.value?.instruction ?: "",
                        onModeChange = { changedMode ->
                            mode = changedMode
                        },
                        onTabModeChange = { changedTabMode ->
                            tabMode = changedTabMode
                        },
                    )
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        recipeModel.loadData()
    }

    @Composable
    private fun ScaffoldContent(
        paddingValues: PaddingValues,
        mode: Mode,
        tabMode: TabMode,
        recipeTitle: String,
        recipeDescription: String,
        ingredients: List<Ingredient>,
        instruction: String,
        onModeChange: (Mode) -> Unit,
        onTabModeChange: (TabMode) -> Unit,
    ) {
        var recipeTitleTemp by remember(recipeTitle) {
            mutableStateOf(recipeTitle)
        }
        var recipeDescriptionTemp by remember(recipeDescription) {
            mutableStateOf(recipeDescription)
        }
        var instructionTemp by remember(instruction) {
            mutableStateOf(instruction)
        }

        var ingredientIdTemp: String? by remember {
            mutableStateOf(null)
        }
        var ingredientNameTemp by remember(ingredientIdTemp) {
            mutableStateOf(findIngredient(ingredients, ingredientIdTemp)?.name)
        }
        var ingredientQuantityTemp by remember(ingredientIdTemp) {
            mutableStateOf(findIngredient(ingredients, ingredientIdTemp)?.quantity)
        }
        var ingredientQuantityVerbalTemp by remember(ingredientIdTemp) {
            mutableStateOf(findIngredient(ingredients, ingredientIdTemp)?.quantityVerbal)
        }
        var ingredientUnityTemp by remember(ingredientIdTemp) {
            mutableStateOf(findIngredient(ingredients, ingredientIdTemp)?.unity)
        }

        var ingredientSortTemp by remember(ingredientIdTemp) {
            mutableIntStateOf(findIngredient(ingredients, ingredientIdTemp)?.sort ?: -1)
        }

        val attachmentPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            // see below savePdf
        }

        var deleteDialogMode by remember {
            mutableStateOf(Delete.NONE)
        }

        val resetIngredient: () -> Unit = {
            ingredientIdTemp = null
            // when adding new ingredients, id is always null and other temp-values are not reset automatically
            ingredientNameTemp = null
            ingredientQuantityTemp = null
            ingredientQuantityVerbalTemp = null
            ingredientUnityTemp = null
            ingredientSortTemp = -1
        }

        val resetDelete: () -> Unit = {
            deleteDialogMode = Delete.NONE
        }

        val saveRecipe: () -> Unit = {
            recipeModel.saveRecipe(recipeTitleTemp, recipeDescriptionTemp, instructionTemp)
        }

        val saveIngredient: () -> Unit = {
            if (!ingredientNameTemp.isNullOrEmpty()) {
                recipeModel.saveIngredient(
                    ingredientIdTemp,
                    ingredientNameTemp!!,
                    ingredientQuantityTemp,
                    ingredientQuantityVerbalTemp,
                    ingredientUnityTemp,
                    ingredientSortTemp
                )
            }
        }

        val deleteRecipe: () -> Unit = {
            recipeModel.deleteRecipe()
        }

        val deleteIngredient: () -> Unit = {
            ingredients.find { ingredient -> ingredient.id == ingredientIdTemp }
                ?.let { recipeModel.deleteIngredient(it) }
        }

        val onAttachDocument: () -> Unit = {
            attachmentPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
        }

        val onChangeSortIngredient: (Map<Ingredient, Int>) -> Unit = { map ->
            recipeModel.bulkUpdateIngredients(map)
        }

        val onCloseDeleteDialog: () -> Unit = {
            resetIngredient()
            resetDelete()
        }

        val onCloseEdit: () -> Unit = {
            resetIngredient()
            onModeChange(Mode.SHOW_RECIPE)
        }

        val onDelete: (delete: Delete) -> Unit = { selectedDelete ->
            when (selectedDelete) {
                Delete.NONE -> {}
                Delete.RECIPE -> {
                    deleteRecipe()
                    finish()
                }
                Delete.INGREDIENT -> {
                    deleteIngredient()
                    resetIngredient()
                    resetDelete()
                }
            }
        }

        val onDeleteIngredient: (ingredientId: String) -> Unit = { id ->
            ingredientIdTemp = id
            deleteDialogMode = Delete.INGREDIENT
        }

        val onDeleteRecipe: () -> Unit = {
            deleteDialogMode = Delete.RECIPE
        }

        val onEditIngredient: (ingredientId: String) -> Unit = { id ->
            ingredientIdTemp = id
            onModeChange(Mode.EDIT_INGREDIENT)
        }

        val onEditInstruction: () -> Unit = {
            onModeChange(Mode.EDIT_INSTRUCTION)
        }

        val onEditRecipe: () -> Unit = {
            onModeChange(Mode.EDIT_RECIPE)
        }

        val onSaveIngredient: () -> Unit = {
            saveIngredient()
            resetIngredient()
            onModeChange(Mode.SHOW_RECIPE)
        }

        val onSaveRecipe: () -> Unit = {
            saveRecipe()
            onModeChange(Mode.SHOW_RECIPE)
        }

        if (deleteDialogMode != Delete.NONE) {
            DeleteDialog(
                dialogMode = deleteDialogMode,
                onClose = onCloseDeleteDialog,
                onDelete = onDelete
            )
        }

        when (mode) {
            Mode.SHOW_RECIPE -> {
                ShowRecipe(
                    paddingValues = paddingValues,
                    tabMode = tabMode,
                    recipeTitle = recipeTitleTemp,
                    recipeDescription = recipeDescriptionTemp,
                    ingredients = ingredients,
                    instruction = instruction,
                    onEditRecipe = onEditRecipe,
                    onDeleteRecipe = onDeleteRecipe,
                    onAttachDocument = onAttachDocument,
                    onEditIngredient = onEditIngredient,
                    onDeleteIngredient = onDeleteIngredient,
                    onChangeSortIngredient = onChangeSortIngredient,
                    onEditInstruction = onEditInstruction,
                    onTabModeChange = onTabModeChange
                )
            }
            Mode.EDIT_RECIPE -> {
                EditRecipe(
                    paddingValues = paddingValues,
                    recipeTitle = recipeTitleTemp,
                    recipeDescription = recipeDescriptionTemp,
                    onRecipeTitleChange = { title -> recipeTitleTemp = title},
                    onRecipeDescriptionChange = { description -> recipeDescriptionTemp = description},
                    onCancel = onCloseEdit,
                    onSave = onSaveRecipe
                )
            }
            Mode.EDIT_INGREDIENT -> {
                EditIngredient(
                    paddingValues = paddingValues,
                    ingredientName = ingredientNameTemp,
                    ingredientQuantity = ingredientQuantityTemp,
                    ingredientQuantityVerbal = ingredientQuantityVerbalTemp,
                    ingredientUnity = ingredientUnityTemp,
                    onIngredientNameChange = { name -> ingredientNameTemp = name},
                    onIngredientQuantityChange = { quantity -> ingredientQuantityTemp = quantity},
                    onIngredientQuantityVerbalChange = { quantityVerbal -> ingredientQuantityVerbalTemp = quantityVerbal},
                    onIngredientUnityChange = { unity -> ingredientUnityTemp = unity},
                    onCancel = onCloseEdit,
                    onSave = onSaveIngredient
                )
            }
            Mode.EDIT_INSTRUCTION -> {
                EditInstruction(
                    paddingValues = paddingValues,
                    instruction = instructionTemp,
                    onInstructionChange = { changedInstruction -> instructionTemp = changedInstruction },
                    onCancel = onCloseEdit,
                    onSave = onSaveRecipe
                )
            }
        }
    }

    @Composable
    private fun DeleteDialog(
        dialogMode: Delete,
        onClose: () -> Unit,
        onDelete: (delete: Delete) -> Unit
    ) {
        AlertDialog(
            text = {
                val msg = when (dialogMode) {
                    Delete.RECIPE -> getString(R.string.delete_recipe)
                    Delete.INGREDIENT -> getString(R.string.delete_ingredient)
                    Delete.NONE -> ""
                }
                Text(msg)
            },
            onDismissRequest = onClose,
            dismissButton = {
                Text(
                    modifier = Modifier
                        .clickable { onClose() },
                    text = getString(R.string.close)
                )
            },
            confirmButton = {
                Text(
                    modifier = Modifier
                        .clickable { onDelete(dialogMode) },
                    text = getString(R.string.delete)
                )
            }
        )
    }

    private fun findIngredient(ingredients: List<Ingredient>, ingredientId: String?): Ingredient? {
        return ingredients.find { ingredient ->
            ingredient.id == ingredientId
        }
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
/*
    private fun savePdf(data: Intent) {
        data.data?.let { pdfUri ->
            recipeModel.recipeData.value?.title?.let { title ->
                contentResolver.takePersistableUriPermission(
                    pdfUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                recipeModel.saveRecipe(
                    title,
                    recipeModel.recipeData.value?.description ?: "",
                    recipeModel.recipeData.value?.instruction ?: ""
                )
                //recipeModel.savePdf(pdfUri)
            }
        }
    }
*/
}