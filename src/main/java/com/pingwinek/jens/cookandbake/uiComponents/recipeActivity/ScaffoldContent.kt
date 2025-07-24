package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel

@Composable
fun ScaffoldContent(
    paddingValues: PaddingValues,
    mode: Mode,
    tabMode: TabMode,
    recipeModel: RecipeViewModel,
    onIngredientFunctionsMode: (Boolean) -> Unit,
    onModeChange: (Mode) -> Unit,
    onTabModeChange: (TabMode) -> Unit,
    onFinish: () -> Unit
) {
    val recipeData by recipeModel.recipeData.observeAsState()
    val isLoadingAttachment by recipeModel.isUpOrDownLoading.observeAsState()
    val ingredientData = recipeModel.ingredientListData.observeAsState()
    val ingredients = ingredientData.value ?: listOf()
    val availableTagData = recipeModel.availableTagListData.observeAsState()
    val availableTags = availableTagData.value ?: listOf()
    val attachedTagData = recipeModel.attachedTagListData.observeAsState()
    val attachedTags = attachedTagData.value ?: listOf()

    var recipeTitleTemp by remember(recipeData?.title) {
        mutableStateOf(recipeData?.title ?: "")
    }
    var recipeDescriptionTemp by remember(recipeData?.description) {
        mutableStateOf(recipeData?.description ?: "")
    }
    var instructionTemp by remember(recipeData?.instruction) {
        mutableStateOf(recipeData?.instruction ?: "")
    }

    val tagsTemp: MutableList<RecipeViewModel.TagHelper> = remember(attachedTagData.value) {
        mutableStateListOf<RecipeViewModel.TagHelper>().apply { this.addAll(attachedTags) }
    }
    var tagTemp: RecipeViewModel.TagHelper? by remember {
        mutableStateOf(null)
    }

    val unattachedAvailableTags = availableTags.filter { availableTag ->
        tagsTemp.find { attachedTag ->
            attachedTag.tagID == availableTag.id && !attachedTag.isDeleted
        } == null
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
        uri?.let {
            recipeModel.attachDocument(uri)
        }
    }

    var addLabelDialogMode by remember {
        mutableStateOf(false)
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

    val resetTag: () -> Unit = {
        tagTemp = null
    }

    val resetTags: () -> Unit = {
        tagsTemp.removeIf {
            tt -> tt.isNew
        }
        tagsTemp.forEach { tt ->
            tt.isDeleted = false
        }
    }

    val resetDelete: () -> Unit = {
        deleteDialogMode = Delete.NONE
    }

    val saveRecipe: () -> Unit = {
        recipeModel.saveRecipe(recipeTitleTemp, recipeDescriptionTemp, instructionTemp)
        recipeModel.saveTags(tagsTemp)
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

    val addTag: (List<Tag>) -> Unit = { tags ->
        tags.forEach { tag ->
            tagsTemp.add(
                RecipeViewModel.TagHelper(
                    tagID = tag.id,
                    label = tag.label,
                    color = tag.color,
                    sort = tagsTemp.size,
                    isNew = true
                )
            )
        }
    }

    val deleteTag: () -> Unit = {
        tagTemp?.let { tt ->
            tagsTemp.remove(tt)
            tt.isDeleted = true
            tagsTemp.add(tt)
        }
    }

    val deleteRecipe: () -> Unit = {
        recipeModel.deleteRecipe()
    }

    val deleteIngredient: () -> Unit = {
        ingredients.find { ingredient -> ingredient.id == ingredientIdTemp }
            ?.let { recipeModel.deleteIngredient(it) }
    }

    val onAddTag: () -> Unit = {
        deleteDialogMode = Delete.NONE
        addLabelDialogMode = true
    }

    val onAttachDocument: () -> Unit = {
        attachmentPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
    }

    val onAttachmentClicked: () -> Unit = {
        recipeModel.loadAttachment()
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
        resetTags()
        onModeChange(Mode.SHOW_RECIPE)
        onIngredientFunctionsMode(false)
    }

    val onDelete: (delete: Delete) -> Unit = { selectedDelete ->
        when (selectedDelete) {
            Delete.NONE -> {}
            Delete.RECIPE -> {
                deleteRecipe()
                onFinish()
            }
            Delete.INGREDIENT -> {
                deleteIngredient()
                resetIngredient()
                resetDelete()
                onIngredientFunctionsMode(false)
            }
            Delete.Label -> {
                deleteTag()
                resetTag()
                resetDelete()
            }
        }
    }

    val onDeleteDocument: () -> Unit = {
        recipeModel.deleteAttachment()
    }

    val onDeleteIngredient: (ingredientId: String) -> Unit = { id ->
        ingredientIdTemp = id
        addLabelDialogMode = false
        deleteDialogMode = Delete.INGREDIENT
    }

    val onDeleteTag: (tag: RecipeViewModel.TagHelper) -> Unit = { tag ->
        tagTemp = tag
        onDelete(Delete.Label)
    }

    val onDeleteRecipe: () -> Unit = {
        addLabelDialogMode = false
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
        onIngredientFunctionsMode(false)
    }

    val onSaveRecipe: () -> Unit = {
        saveRecipe()
        onModeChange(Mode.SHOW_RECIPE)
    }

    if (addLabelDialogMode) {
        AddTagDialog(
            tags = unattachedAvailableTags,
            onClose = { addLabelDialogMode = false },
            onSave = { tags ->
                addTag(tags)
                addLabelDialogMode = false
            }
        )
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
                onIngredientsFunctionsMode = onIngredientFunctionsMode,
                recipeTitle = recipeTitleTemp,
                recipeDescription = recipeDescriptionTemp,
                ingredients = ingredients,
                labels = attachedTags.mapNotNull { tag -> if (! tag.isDeleted) tag.label else null },
                instruction = recipeData?.instruction ?: "",
                hasAttachment = recipeData?.hasAttachment ?: false,
                isAttachmentLoading = isLoadingAttachment ?: false,
                onEditRecipe = onEditRecipe,
                onDeleteRecipe = onDeleteRecipe,
                onAttachDocument = onAttachDocument,
                onDeleteDocument = onDeleteDocument,
                onAttachmentClicked = onAttachmentClicked,
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
                tags = tagsTemp,
                onRecipeTitleChange = { title -> recipeTitleTemp = title},
                onRecipeDescriptionChange = { description -> recipeDescriptionTemp = description},
                onAddLabel = onAddTag,
                onDeleteLabel = onDeleteTag,
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

private fun findIngredient(ingredients: List<Ingredient>, ingredientId: String?): Ingredient? {
    return ingredients.find { ingredient ->
        ingredient.id == ingredientId
    }
}
