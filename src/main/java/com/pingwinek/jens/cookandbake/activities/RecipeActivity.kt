package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Receipt
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_INSTRUCTION
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.lib.spacing
import com.pingwinek.jens.cookandbake.models.Ingredient
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

    private lateinit var recipeModel: RecipeViewModel

//    private lateinit var saveRecipeLauncher: ActivityResultLauncher<Intent>
//    private lateinit var saveIngredientLauncher: ActivityResultLauncher<Intent>
    private lateinit var savePdfLauncher: ActivityResultLauncher<Intent>
    lateinit var saveInstructionLauncher: ActivityResultLauncher<Intent>

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

    private enum class FabMode { NONE, ADD_INGREDIENT }
    private enum class TabMode { INGREDIENTS, INSTRUCTION, PDF }

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

                val optionBack = PingwinekCooksComposables.OptionItem(
                    labelResourceId = R.string.back,
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    onClick = { finish() }
                )

                val optionAddIngredient = PingwinekCooksComposables.OptionItem(
                    labelResourceId = R.string.plus_new_ingredient,
                    icon = Icons.Filled.Add,
                    onClick = {
                        mode = Mode.EDIT_INGREDIENT
                    }
                )
                
                val onTabModeChange: (TabMode) -> Unit = { newTabMode ->
                    tabMode = newTabMode
                }

                PingwinekCooksScaffold(
                    title = "",
                    optionItemLeft = if (mode == Mode.SHOW_RECIPE) optionBack else null,
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
                        ingredients = ingredientData.value ?: listOf(),
                        instruction = recipeData.value?.instruction ?: "",
                        onModeChange = { changedMode ->
                            mode = changedMode
                        },
                        onTabModeChange = { changedTabMode ->
                            tabMode = changedTabMode
                        }
                    )
                }
            }
        }

//        saveRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveRecipe))
//        saveIngredientLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveIngredient))
        savePdfLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::savePdf))
        saveInstructionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveInstruction))

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
        onTabModeChange: (TabMode) -> Unit
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
        var ingredientId: String? by remember {
            mutableStateOf(null)
        }
        var ingredientName by remember(ingredientId) {
            mutableStateOf(ingredients.find { ingredient ->
                ingredient.id == ingredientId
            }?.name)
        }
        var ingredientQuantity by remember(ingredientId) {
            mutableStateOf(ingredients.find { ingredient ->
                ingredient.id == ingredientId
            }?.quantity)
        }
        var ingredientQuantityVerbal by remember(ingredientId) {
            mutableStateOf(ingredients.find { ingredient ->
                ingredient.id == ingredientId
            }?.quantityVerbal)
        }
        var ingredientUnity by remember(ingredientId) {
            mutableStateOf(ingredients.find { ingredient ->
                ingredient.id == ingredientId
            }?.unity)
        }

        when (mode) {
                Mode.SHOW_RECIPE -> {
                    ShowRecipe(
                        paddingValues = paddingValues,
                        recipeTitle = recipeTitleTemp,
                        recipeDescription = recipeDescriptionTemp,
                        ingredients = ingredients,
                        instruction = instruction,
                        onEditRecipe = { onModeChange(Mode.EDIT_RECIPE) },
                        onEditIngredient = { changedIngredientId ->
                            ingredientId = changedIngredientId
                            onModeChange(Mode.EDIT_INGREDIENT)
                       },
                        onEditInstruction = { onModeChange(Mode.EDIT_INSTRUCTION) },
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
                        onCancel = {
                            onModeChange(Mode.SHOW_RECIPE)
                                   },
                        onSave = {
                            recipeModel.saveRecipe(recipeTitleTemp, recipeDescriptionTemp)
                            onModeChange(Mode.SHOW_RECIPE)
                        }
                    )
                }
                Mode.EDIT_INGREDIENT -> {
                    EditIngredient(
                        paddingValues = paddingValues,
                        ingredientName = ingredientName,
                        ingredientQuantity = ingredientQuantity,
                        ingredientQuantityVerbal = ingredientQuantityVerbal,
                        ingredientUnity = ingredientUnity,
                        onIngredientNameChange = { name -> ingredientName = name},
                        onIngredientQuantityChange = { quantity -> ingredientQuantity = quantity},
                        onIngredientQuantityVerbalChange = { quantityVerbal -> ingredientQuantityVerbal = quantityVerbal},
                        onIngredientUnityChange = { unity -> ingredientUnity = unity},
                        onCancel = {
                            ingredientId = null
                            onModeChange(Mode.SHOW_RECIPE)
                                   },
                        onSave = {
                            if (!ingredientName.isNullOrEmpty()) {
                                recipeModel.saveIngredient(
                                    ingredientId,
                                    ingredientName!!,
                                    ingredientQuantity,
                                    ingredientQuantityVerbal,
                                    ingredientUnity
                                )
                            }
                            onModeChange(Mode.SHOW_RECIPE)
                        }
                    )
                }
                Mode.EDIT_INSTRUCTION -> {}
            }
    }

    @Composable
    private fun ShowRecipe(
        paddingValues: PaddingValues,
        recipeTitle: String,
        recipeDescription: String,
        ingredients: List<Ingredient>,
        instruction: String,
        onEditRecipe: () -> Unit,
        onEditIngredient: (ingredientId: String) -> Unit,
        onEditInstruction: () -> Unit,
        onTabModeChange: (TabMode) -> Unit
    ) {
        var showButtons by remember { mutableStateOf(false) }

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
                        .weight(80f)
                        .clickable { showButtons = !showButtons },
                ) {
                    Text(text = recipeTitle)
                    Text(text = recipeDescription)
                }

                if (showButtons) {
                    IconButton(onClick = onEditRecipe) {
                        Icon(Icons.Filled.Edit, getString(R.string.edit_recipe))
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Delete, getString(R.string.delete_recipe))
                    }
                }
            }

            PingwinekCooksComposables.SpacerMedium()

            RecipeTabRow(
                paddingValues = paddingValues,
                ingredients = ingredients,
                instruction = instruction,
                onEditIngredient = onEditIngredient,
                onEditInstruction = onEditInstruction,
                onTabModeChange = onTabModeChange
            )
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
        EditPane (
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

        EditPane(
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
                    value = ingredientQuantity?.toString() ?: "",
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
                        if (changedString.isNotEmpty()) {
                            try {
                                val quantity = changedString.toDouble()
                                onIngredientQuantityChange(quantity)
                            } catch (exception: NumberFormatException) {
                                Log.e(this::class.java.name, exception.toString())
                            }
                        } else {
                            onIngredientQuantityChange(null)
                        }
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
    private fun RecipeTabRow(
        paddingValues: PaddingValues,
        ingredients: List<Ingredient>,
        instruction: String,
        onEditIngredient: (ingredientId: String) -> Unit,
        onEditInstruction: () -> Unit,
        onTabModeChange: (TabMode) -> Unit
    ) {

        var selectedTab by remember {
            mutableStateOf(TabMode.INGREDIENTS)
        }

        val onSelectedTabChange: (tab: TabMode) -> Unit = { newSelectedTab ->
            selectedTab = newSelectedTab
            onTabModeChange(newSelectedTab)
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Surface(
                color = Color.Transparent,
                /*modifier = Modifier
                    .padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                    )*/
            ) {
                PingwinekCooksComposables.PingwinekCooksTabRow(
                    selectedItem = selectedTab.ordinal,
                    containerColor = Color.Transparent,
                    menuItems = listOf(
                        optionIngredients.apply {
                            onClick = { onSelectedTabChange(TabMode.INGREDIENTS) }
                        },
                        optionInstruction.apply {
                            onClick = { onSelectedTabChange(TabMode.INSTRUCTION) }
                        },
                        optionPdf.apply {
                            onClick = { onSelectedTabChange(TabMode.PDF) }
                        }
                    )
                )
            }

            when (selectedTab) {
                TabMode.INGREDIENTS -> {
                    var showButtons by remember {
                        mutableIntStateOf(-1)
                    }

                    PingwinekCooksComposables.SpacerSmall()

                    ingredients.forEachIndexed { index, ingredient ->
                        key(index) {
                            Surface(
                                color =
                                if (showButtons == index) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                shape = ShapeDefaults.Small,
                                modifier = Modifier
                                    .padding(bottom = MaterialTheme.spacing.extraSmallPadding)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(
                                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                        )
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .weight(80f)
                                            .clickable {
                                                showButtons =
                                                    if (showButtons == index) -1 else index
                                            },
                                        text = ingredient.name
                                    )

                                    if (showButtons == index) {
                                        IconButton(onClick = {
                                            onEditIngredient(ingredient.id)
                                        }) {
                                            Icon(Icons.Filled.Edit, getString(R.string.edit_ingredient))
                                        }
                                        IconButton(onClick = {}) {
                                            Icon(Icons.Filled.Delete, getString(R.string.delete_ingredient))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                TabMode.INSTRUCTION -> {
                    Text(text = instruction)
                }
                TabMode.PDF -> {}
            }
        }
    }

    @Composable
    private fun EditPane(
        paddingValues: PaddingValues,
        onCancel: () -> Unit,
        onSave: () -> Unit,
        content: @Composable () -> Unit
    ) {
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            PingwinekCooksComposables.SpacerSmall()

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .clickable { onCancel() },
                    text = getString(R.string.close)
                )
                Text(
                    modifier = Modifier
                        .clickable { onSave() },
                    text = getString(R.string.save)
                )
            }

            PingwinekCooksComposables.SpacerMedium()

            content()
        }
    }

    private fun delete() {
        AlertDialog.Builder(this).apply {
            setMessage(R.string.recipe_delete_confirm)
            setPositiveButton(R.string.yes) { _, _ ->
                recipeModel.deleteRecipe()
                finish()
            }
            setNegativeButton(R.string.no) { _, _ ->
                // Do nothing
            }
        }.show()

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

    private fun saveInstruction(data: Intent) {
        data.extras?.let {
            it.getString(EXTRA_RECIPE_INSTRUCTION)?.let { instruction ->
                recipeModel.recipeData.value?.title?.let { title ->
                    recipeModel.saveRecipe(
                        title,
                        recipeModel.recipeData.value?.description ?: "",
                        instruction
                    )
                }
            }
        }
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