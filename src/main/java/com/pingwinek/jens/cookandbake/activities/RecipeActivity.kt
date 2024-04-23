package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.EXTRA_INGREDIENT_ID
import com.pingwinek.jens.cookandbake.EXTRA_INGREDIENT_NAME
import com.pingwinek.jens.cookandbake.EXTRA_INGREDIENT_QUANTITY
import com.pingwinek.jens.cookandbake.EXTRA_INGREDIENT_QUANTITY_VERBAL
import com.pingwinek.jens.cookandbake.EXTRA_INGREDIENT_UNITY
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_DESCRIPTION
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_INSTRUCTION
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_TITLE
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksScaffold
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
    private lateinit var saveIngredientLauncher: ActivityResultLauncher<Intent>
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

                val recipeData = recipeModel.recipeData.observeAsState()
                val ingredientData = recipeModel.ingredientListData.observeAsState()

                val optionBack = PingwinekCooksComposables.OptionItem(
                    labelResourceId = R.string.back,
                    icon = Icons.AutoMirrored.Outlined.ArrowBack
                ) { finish() }

                PingwinekCooksScaffold(
                    title = "",
                    optionItemLeft = if (mode == Mode.SHOW_RECIPE) optionBack else null,
                ) { paddingValues ->
                    ScaffoldContent(
                        paddingValues = paddingValues,
                        mode = mode,
                        recipeTitle = recipeData.value?.title ?: "",
                        recipeDescription = recipeData.value?.description ?: "",
                        ingredients = ingredientData.value ?: listOf(),
                        instruction = recipeData.value?.instruction ?: "",
                        onModeChange = { changedMode ->
                            mode = changedMode
                        }
                    )
                }
            }
        }

//        saveRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveRecipe))
        saveIngredientLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveIngredient))
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
        recipeTitle: String,
        recipeDescription: String,
        ingredients: List<Ingredient>,
        instruction: String,
        onModeChange: (Mode) -> Unit
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

        when (mode) {
                Mode.SHOW_RECIPE -> {
                    ShowRecipe(
                        paddingValues = paddingValues,
                        recipeTitle = recipeTitleTemp,
                        recipeDescription = recipeDescriptionTemp,
                        ingredients = ingredients,
                        instruction = instruction,
                        onEditRecipe = { onModeChange(Mode.EDIT_RECIPE) }
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
                Mode.EDIT_INGREDIENT -> {}
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
        onEditRecipe: () -> Unit
    ) {
        var showButtons by remember { mutableStateOf(false) }

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
                ingredients = ingredients,
                instruction = instruction
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
    private fun RecipeTabRow(
        ingredients: List<Ingredient>,
        instruction: String
    ) {

        var selectedItem by remember {
            mutableIntStateOf(0)
        }

        PingwinekCooksComposables.PingwinekCooksTabRow(
            selectedItem = selectedItem,
            menuItems = listOf(
                optionIngredients.apply {
                    onClick = { selectedItem = 0 }
                },
                optionInstruction.apply {
                    onClick = { selectedItem = 1 }
                },
                optionPdf.apply {
                    onClick = { selectedItem = 2 }
                }
            )
        )

        when (selectedItem) {
            0 -> {
                PingwinekCooksComposables.SpacerSmall()

                ingredients.forEach { ingredient ->
                    Row() {
                        var showButtons by remember {
                            mutableStateOf(false)
                        }

                        Text(
                            modifier = Modifier
                                .weight(80f)
                                .clickable { showButtons = !showButtons },
                            text = ingredient.name
                        )

                        if (showButtons) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Filled.Edit, getString(R.string.edit_recipe))
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.Filled.Delete, getString(R.string.delete_recipe))
                            }
                        }
                    }
                }
            }
            1 -> {
                Text(text = instruction)
            }
            2 -> {}
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

/*
    override fun onListFragmentDeleteIngredient(ingredient: Ingredient) {
        AlertDialog.Builder(this).apply {
            setMessage("Zutat ${ingredient.name} wirklich löschen?")
            setPositiveButton(getString(R.string.delete)) { _, _ ->
                recipeModel.ingredientListData.value?.find { toDelete ->
                    toDelete.id == ingredient.id
                }?.let { ingredient ->
                    recipeModel.deleteIngredient(ingredient)
                }
            }
            setNegativeButton(getString(R.string.close)) { _, _ -> /* do nothing */ }
        }.show()
    }

    override fun onListFragmentSaveIngredient(ingredient: Ingredient?) {
        val intent = Intent(this, IngredientEditActivity::class.java)
        recipeModel.recipeData.value?.let { recipe ->
            intent.putExtra(EXTRA_RECIPE_TITLE, recipe.title)
            ingredient?.let {
                intent.putExtra(EXTRA_INGREDIENT_ID, it.id)
                intent.putExtra(EXTRA_INGREDIENT_NAME, it.name)
                intent.putExtra(EXTRA_INGREDIENT_QUANTITY, it.quantity)
                intent.putExtra(EXTRA_INGREDIENT_QUANTITY_VERBAL, it.quantityVerbal)
                intent.putExtra(EXTRA_INGREDIENT_UNITY, it.unity)
            }
            saveIngredientLauncher.launch(intent)
        }
    }
*/
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

    /*
    private fun deletePdf() {
        AlertDialog.Builder(this).apply {
            setMessage(R.string.pdf_delete_confirm)
            setPositiveButton(R.string.yes) { _, _ ->
                //recipeModel.deletePdf()
            }
            setNegativeButton(R.string.no) { _, _ ->
                // Do nothing
            }
        }.show()

    }

     */

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

    private fun saveIngredient(data: Intent) {
        data.extras?.let {
            it.getString(EXTRA_INGREDIENT_NAME)?.let { name ->
                recipeModel.saveIngredient(
                    it.getString(EXTRA_INGREDIENT_ID),
                    name,
                    it.getDouble(EXTRA_INGREDIENT_QUANTITY),
                    it.getString(EXTRA_INGREDIENT_QUANTITY_VERBAL),
                    it.getString(EXTRA_INGREDIENT_UNITY)
                )
            }
        }
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

    private fun saveRecipe(data: Intent) {
        data.extras?.let {
            it.getString(EXTRA_RECIPE_TITLE)?.let { title ->
                recipeModel.saveRecipe(
                    title,
                    it.getString(EXTRA_RECIPE_DESCRIPTION, "")
                )
            }
        }
    }

}
/*
class RecipePagerAdapter(
    fragmentManager: androidx.fragment.app.FragmentManager,
    private val ingredientTitle: String,
    private val instructionTitle: String,
    private val pdfTitle: String
) : androidx.fragment.app.FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            0 -> IngredientListingFragment()
            1 -> InstructionFragment()
            else -> PdfFragment()
        }
    }

    override fun getCount(): Int {
        //return 3
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> ingredientTitle
            1 -> instructionTitle
            else -> pdfTitle
        }
    }
}
*/