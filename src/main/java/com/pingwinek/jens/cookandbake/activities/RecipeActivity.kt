package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.lib.spacing
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.utils.Utils
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel

class RecipeActivity: AppCompatActivity() {

    private class ActivityResultCallback(val onActivityResultHandler: (Intent) -> Unit) : androidx.activity.result.ActivityResultCallback<ActivityResult> {
        override fun onActivityResult(result: ActivityResult) {
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    onActivityResultHandler(it)
                }
            }
        }
    }

    private enum class Mode {
        SHOW_RECIPE,
        EDIT_RECIPE,
        EDIT_INGREDIENT,
        EDIT_INSTRUCTION
    }

    private enum class FabMode { NONE, ADD_INGREDIENT }
    private enum class TabMode { INGREDIENTS, INSTRUCTION, PDF }

    private enum class Delete { NONE, RECIPE, INGREDIENT }

    private lateinit var recipeModel: RecipeViewModel

//    private lateinit var saveRecipeLauncher: ActivityResultLauncher<Intent>
//    private lateinit var saveIngredientLauncher: ActivityResultLauncher<Intent>
    private lateinit var savePdfLauncher: ActivityResultLauncher<Intent>
//    lateinit var saveInstructionLauncher: ActivityResultLauncher<Intent>

    private val optionIngredients = PingwinekCooksComposables.OptionItem(
        R.string.ingredients,
        Icons.AutoMirrored.Filled.ReceiptLong,
        {}
    )

    private val optionInstruction = PingwinekCooksComposables.OptionItem(
        R.string.instruction,
        Icons.Filled.Receipt,
        {}
    )

    private val optionPdf = PingwinekCooksComposables.OptionItem(
        R.string.pdf,
        Icons.Filled.FilePresent,
        {}
    )

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

//        saveRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveRecipe))
//        saveIngredientLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveIngredient))
        savePdfLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::savePdf))
//        saveInstructionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveInstruction))

/*
        // the fab needs to sit in the Activity, does not work in the Fragment
        val fab = findViewById<FloatingActionButton>(R.id.recipeFab)
        fab.hide()
        fab.setOnClickListener {
            // Check if Android 10 or higher
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                Log.i(this::class.java.name, "not yet implemented for Android 11+")


                val query = contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                    ),
                    null,
                    null,
                    null
                )
                Log.i(this::class.java.name, query.toString())

                query?.use { cursor ->
                    Log.i(this::class.java.name, cursor.count.toString())
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    while (cursor.moveToNext()) {
                        Log.i(this::class.java.name, cursor.getInt(idColumn).toString())
                        Log.i(this::class.java.name, cursor.getString(nameColumn))
                    }
                }

            } else {
                val pdfIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    //flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/pdf"
                }
                savePdfLauncher.launch(pdfIntent)
            }
        }


        val titleView = findViewById<TextView>(R.id.recipeName)
        val descriptionView = findViewById<TextView>(R.id.recipeDescription)
*/
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

        val attachementPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri ->

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
            attachementPickerLauncher.launch(arrayOf(""))
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
    private fun ShowRecipe(
        paddingValues: PaddingValues,
        tabMode: TabMode,
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
        onTabModeChange: (TabMode) -> Unit
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
                        Icon(Icons.Filled.Edit, getString(R.string.edit_recipe))
                    }
                    IconButton(onClick = onDeleteRecipe) {
                        Icon(Icons.Filled.Delete, getString(R.string.delete_recipe))
                    }
                    IconButton(onClick = onAttachDocument) {
                        Icon(Icons.Filled.Attachment, getString(R.string.attach_document))
                    }
                }
            }

            PingwinekCooksComposables.SpacerMedium()

            PingwinekCooksComposables.PingwinekCooksTabElement(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(color = MaterialTheme.colorScheme.tertiaryContainer),
                selectedItem = tabMode.ordinal,
                onSelectedItemChange = { item -> onTabModeChange(TabMode.entries[item]) },
                tabItems = mutableListOf<PingwinekCooksComposables.PingwinekCooksTabItem>().apply {
                    add(PingwinekCooksComposables.PingwinekCooksTabItem(
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
                    add(PingwinekCooksComposables.PingwinekCooksTabItem(
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
                    add(PingwinekCooksComposables.PingwinekCooksTabItem(
                        tabNameId = R.string.pdf,
                        tabIcon = Icons.Filled.FilePresent,
                        content = {
                        }
                    ))
                }
            )
        }
    }

    @Composable
    private fun ShowIngredients(
        paddingValues: PaddingValues,
        ingredients: List<Ingredient>,
        onEditIngredient: (String) -> Unit,
        onDeleteIngredient: (String) -> Unit,
        onChangeSort: (Map<Ingredient, Int>) -> Unit
    ) {
        val height = MaterialTheme.spacing.standardIcon
        val paddingBelow = MaterialTheme.spacing.extraSmallPadding
        val switchPositionOffset = (height + paddingBelow).value

        var activePane by remember(ingredients) {
            mutableIntStateOf(-1)
        }

        var ingredientsSorted by remember(ingredients) {
            mutableStateOf(ingredients.sortedBy { ingredient -> ingredient.sort })
        }

        var offset by remember(activePane) { mutableFloatStateOf(0f) }
        var posDelta by remember(activePane) { mutableIntStateOf(0) }

        val onDrag: (Float) -> Unit = { newOffset ->
            offset = newOffset
            posDelta = (newOffset / switchPositionOffset).toInt()
        }

        val onDragStopped: () -> Unit = {
            val ingredientsLocallyResorted = ingredientsSorted.toMutableList()
            val ingredientsResorted = mutableMapOf<Ingredient, Int>()

            ingredientsSorted.forEachIndexed { index, ingredient ->
                var sort = if (index == activePane) {
                    index + posDelta
                } else if(index < activePane && index >= activePane + posDelta) {
                    index + 1
                } else if (index > activePane && index <= activePane + posDelta) {
                    index - 1
                } else {
                    index
                }

                sort = when {
                    sort < 0 -> 0
                    sort > ingredientsSorted.size -1 -> ingredientsSorted.size -1
                    else -> sort
                }

                ingredientsLocallyResorted[sort] = ingredient
                if (sort != ingredient.sort) ingredientsResorted[ingredient] = sort
            }

            activePane = -1
            ingredientsSorted = ingredientsLocallyResorted
            onChangeSort(ingredientsResorted)
        }

        Column {
            PingwinekCooksComposables.SpacerSmall()

            ingredientsSorted.forEachIndexed { index, ingredient ->
                val conditionalOffset = if (index == activePane) {
                    offset
                } else if(index < activePane && index >= activePane + posDelta) {
                    switchPositionOffset
                } else if (index > activePane && index <= activePane + posDelta) {
                    switchPositionOffset * -1
                } else {
                    0f
                }
                key(index) {
                    IngredientPane(
                        paddingValues = paddingValues,
                        height = height,
                        paddingBelow = paddingBelow,
                        zIndex = if (index == activePane) 1f else 0f,
                        elevation = if (index == activePane) 10f else 0f,
                        offset = conditionalOffset,
                        paneColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        showButtons = (activePane == index),
                        onChangeActivePane = {
                              activePane = if (activePane == index) -1 else index
                                              },
                        onEditIngredient = onEditIngredient,
                        onDeleteIngredient = onDeleteIngredient,
                        onDrag = onDrag,
                        onDragStopped = onDragStopped,
                        ingredient = ingredient
                    )
                }
            }
        }
    }

    @Composable
    private fun ShowInstruction(
        paddingValues: PaddingValues,
        instruction: String,
        onEditInstruction: () -> Unit
    ) {
        var showButtons by remember(instruction) { mutableStateOf(instruction.isBlank()) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    top = MaterialTheme.spacing.spacerSmall
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .clickable { showButtons = !showButtons },
                text = instruction
            )

            if (showButtons) {
                IconButton(onClick = onEditInstruction) {
                    Icon(Icons.Filled.Edit, getString(R.string.write_instruction))
                }
            }
        }
    }

    @Composable
    private fun EditRecipe(
        paddingValues: PaddingValues,
        recipeTitle: String,
        recipeDescription: String,
        onRecipeTitleChange: (String) -> Unit,
        onRecipeDescriptionChange: (String) -> Unit,
        onCancel: () -> Unit,
        onSave: () -> Unit
    ) {
        PingwinekCooksComposables.EditPane (
            paddingValues = paddingValues,
            onCancel = onCancel,
            onSave = onSave
        ) {
            Column {
                TextField(
                    value = recipeTitle,
                    label = {
                        Text(getString(R.string.recipe_title))
                    },
                    onValueChange = { changedString ->
                        onRecipeTitleChange(changedString)
                    }
                )
                TextField(
                    value = recipeDescription,
                    label = {
                        Text(getString(R.string.recipe_description))
                    },
                    onValueChange = { changedString ->
                        onRecipeDescriptionChange(changedString)
                    }
                )
            }
        }
    }

    @Composable
    private fun EditIngredient(
        paddingValues: PaddingValues,
        ingredientName: String?,
        ingredientQuantity: Double?,
        ingredientQuantityVerbal: String?,
        ingredientUnity: String?,
        onIngredientNameChange: (String) -> Unit,
        onIngredientQuantityChange: (Double?) -> Unit,
        onIngredientQuantityVerbalChange: (String) -> Unit,
        onIngredientUnityChange: (String) -> Unit,
        onCancel: () -> Unit,
        onSave: () -> Unit
    ) {
        val isQuantityAsNumberMissing = (ingredientQuantity == null && !ingredientQuantityVerbal.isNullOrEmpty())

        PingwinekCooksComposables.EditPane(
            paddingValues = paddingValues,
            onCancel = onCancel,
            onSave = { if (!isQuantityAsNumberMissing) onSave() }
        ) {
            Column {
                TextField(
                    value = ingredientName ?: "",
                    label = {
                        Text(getString(R.string.ingredientName))
                    },
                    onValueChange = { changedString ->
                        onIngredientNameChange(changedString)
                    }
                )
                TextField(
                    value = Utils.quantityToString(ingredientQuantity),
                    label = {
                        Text(getString(R.string.quantity_number))
                    },
                    isError = isQuantityAsNumberMissing,
                    supportingText = {
                        if (isQuantityAsNumberMissing) {
                            Text(getString(R.string.ingredient_quantity_unequal_verbal))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { changedString ->
                        onIngredientQuantityChange(Utils.quantityToDouble(changedString))
                    }
                )
                TextField(
                    value = ingredientQuantityVerbal ?: "",
                    label = {
                        Text(getString(R.string.quantity_verbal))
                    },
                    onValueChange = { changedString ->
                        onIngredientQuantityVerbalChange(changedString)
                    }
                )
                TextField(
                    value = ingredientUnity ?: "",
                    label = {
                        Text(getString(R.string.unity))
                    },
                    onValueChange = { changedString ->
                        onIngredientUnityChange(changedString)
                    }
                )
            }
        }
    }

    @Composable
    private fun EditInstruction(
        paddingValues: PaddingValues,
        instruction: String,
        onInstructionChange: (String) -> Unit,
        onCancel: () -> Unit,
        onSave: () -> Unit
    ) {
        PingwinekCooksComposables.EditPane(
            paddingValues = paddingValues,
            onCancel = onCancel,
            onSave = onSave
        ) {
            TextField(
                value = instruction,
                onValueChange = { changedString ->
                    onInstructionChange(changedString)
                }
            )
        }
    }

    @Composable
    private fun IngredientPane(
        paddingValues: PaddingValues,
        height: Dp,
        paddingBelow: Dp,
        zIndex: Float,
        elevation: Float,
        offset: Float,
        paneColor: Color,
        contentColor: Color,
        showButtons: Boolean,
        onChangeActivePane: () -> Unit,
        onEditIngredient: (String) -> Unit,
        onDeleteIngredient: (String) -> Unit,
        onDrag: (Float) -> Unit,
        onDragStopped: () -> Unit,
        ingredient: Ingredient
    ){
        Surface(
            color = paneColor,
            contentColor = contentColor,
            shape = ShapeDefaults.Small,
            modifier = Modifier
                .padding(bottom = paddingBelow)
                .height(height)
                .zIndex(zIndex)
                .offset(Dp(0f), Dp(offset))
                .shadow(
                    elevation = Dp(elevation)
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        start = paddingValues.calculateStartPadding(
                            LayoutDirection.Ltr
                        ),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    )
            ) {
                Column(
                    modifier = Modifier
                        .weight(80f)
                        .fillMaxWidth()
                        .clickable { onChangeActivePane() },
                ) {
                    val quantity =
                        if (!ingredient.quantityVerbal.isNullOrEmpty()) {
                            ingredient.quantityVerbal!!
                        } else {
                            val quantityAsString =
                                Utils.quantityToString(ingredient.quantity)
                            if (quantityAsString.isEmpty()) {
                                ""
                            } else if (ingredient.unity.isNullOrEmpty()) {
                                quantityAsString
                            } else {
                                "$quantityAsString ${ingredient.unity}"
                            }
                        }
                    Text(
                        modifier = Modifier
                            .height(height/2),
                        fontWeight = FontWeight.Bold,
                        text = ingredient.name
                    )
                    Text(
                        modifier = Modifier
                            .height(height / 2)
                            .padding(
                                start = MaterialTheme.spacing.spacerSmall
                            ),
                        text = quantity
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(40f)
                ) {
                    if (showButtons) {
                        val density = LocalDensity.current
                        val draggableState = rememberDraggableState { delta ->
                            val deltaDPValue = density.run { delta.toDp().value }
                            onDrag(offset + deltaDPValue)
                        }
                        IconButton(onClick = {
                            onEditIngredient(ingredient.id)
                        }) {
                            Icon(
                                Icons.Filled.Edit,
                                getString(R.string.edit_ingredient)
                            )
                        }
                        IconButton(onClick = {
                            onDeleteIngredient(ingredient.id)
                        }) {
                            Icon(
                                Icons.Filled.Delete,
                                getString(R.string.delete_ingredient)
                            )
                        }
                        IconButton(
                            modifier = Modifier
                                .draggable(
                                    state = draggableState,
                                    orientation = Orientation.Vertical,
                                    onDragStopped = { onDragStopped() }
                                ),
                            onClick = {}
                        ) {
                            Icon(
                                Icons.Filled.Menu,
                                "DragAndDrop"
                            )
                        }
                    }
                }
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

}