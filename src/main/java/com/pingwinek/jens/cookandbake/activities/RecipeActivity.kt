package com.pingwinek.jens.cookandbake.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.EditableText
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.models.Recipe
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

    private lateinit var recipeModel: RecipeViewModel

//    private lateinit var saveRecipeLauncher: ActivityResultLauncher<Intent>
    private lateinit var saveIngredientLauncher: ActivityResultLauncher<Intent>
    private lateinit var savePdfLauncher: ActivityResultLauncher<Intent>
    lateinit var saveInstructionLauncher: ActivityResultLauncher<Intent>

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

                val recipeData = recipeModel.recipeData.observeAsState()

                var recipeTitle by remember(recipeData.value?.title) {
                    mutableStateOf(recipeData.value?.title ?: "")
                }
                var recipeDescription by remember(recipeData.value?.description) {
                    mutableStateOf(recipeData.value?.description ?: "")
                }

                var editRecipe by remember { mutableStateOf(recipeModel.recipeId == null) }

                val optionEdit = PingwinekCooksComposables.OptionItem(
                    label = getString(R.string.edit_recipe),
                    icon = Icons.Filled.Edit
                ) {
                    editRecipe = true
                }

                val optionDelete = PingwinekCooksComposables.OptionItem(
                    label = getString(R.string.delete_recipe),
                    icon = Icons.Filled.Delete
                ) {}

                val optionSave = PingwinekCooksComposables.OptionItem(
                    label = getString(R.string.ok),
                    icon = Icons.Filled.Check
                ) {
                    editRecipe = false
                    recipeModel.saveRecipe(recipeTitle, recipeDescription)
                }

                val optionBack = PingwinekCooksComposables.OptionItem(
                    label = "back",
                    icon = Icons.AutoMirrored.Outlined.ArrowBack
                ) { finish() }


                PingwinekCooksScaffold(
                    title = "",
                    showDropDown = !editRecipe,
                    dropDownOptions = listOf(
                        optionEdit,
                        optionDelete
                    ),
                    optionItemLeft = if (editRecipe) null else optionBack,
                    optionItemRight = optionSave
                ) { paddingValues ->
                    ScaffoldContent(
                        paddingValues = paddingValues,
                        recipeTitle = recipeTitle,
                        recipeDescription = recipeDescription,
                        editRecipe = editRecipe,
                        onRecipeTitleChange = { title -> recipeTitle = title },
                        onRecipeDescriptionChange = { description -> recipeDescription = description }
                        )
                }
            }
        }

//        saveRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveRecipe))
        saveIngredientLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveIngredient))
        savePdfLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::savePdf))
        saveInstructionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback(::saveInstruction))

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        recipeModel.recipeData.observe(this) { recipe: Recipe? ->
//            titleView.text = recipe?.title
//            descriptionView.text = recipe?.description
            /*
            if (!recipeModel.hasRecipeImage()) {
                fab.setImageResource(R.drawable.ic_action_add_white)
            } else {
                fab.setImageResource(R.drawable.ic_action_create_white)
            }

             */
        }

        val onEditRecipeClickListener = { _: View ->
            /*
            val editIntent = Intent(this, RecipeEditActivity::class.java)
            with(editIntent) {
                putExtra(EXTRA_RECIPE_TITLE, recipeModel.recipeData.value?.title)
                putExtra(EXTRA_RECIPE_DESCRIPTION, recipeModel.recipeData.value?.description)
            }
            saveRecipeLauncher.launch(editIntent)
            */
        }
/*
//        titleView.setOnClickListener(onEditRecipeClickListener)
//        descriptionView.setOnClickListener(onEditRecipeClickListener)

        val pagerAdapter = RecipePagerAdapter(
            supportFragmentManager,
            resources.getString(R.string.ingredients),
            resources.getString(R.string.instruction),
            resources.getString(R.string.pdf))

        val tabLayout = findViewById<TabLayout>(R.id.recipe_tablayout)
        findViewById<ViewPager>(R.id.recipeTabs).apply {
           /*
           adapter = pagerAdapter
            tabLayout.setupWithViewPager(this)
            addOnPageChangeListener(object: ViewPager.OnPageChangeListener{

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    //do nothing
                }

                override fun onPageSelected(position: Int) {
                    /*
                    when (position) {
                        0 -> fab.hide()
                        1 -> fab.hide()
                        2 -> fab.show()
                    }
                    */
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // do nothing
                }
            })
            */
        }

        optionMenu.apply {
            addMenuEntry(R.id.OPTION_MENU_DELETE, resources.getString(R.string.delete)) {
                delete()
                true
            }

            addMenuEntry(R.id.OPTION_MENU_DELETE_PDF, resources.getString(R.string.delete_pdf)) {
                deletePdf()
                true
            }

            addMenuEntry(
                R.id.OPTION_MENU_SHARE,
                resources.getString(R.string.share),
                R.drawable.ic_action_share_black,
                true) {
                startActivity(getShareRecipeIntent())
                true
            }
        }

 */
    }

    override fun onResume() {
        super.onResume()
        recipeModel.loadData()
    }

    @Composable
    private fun ScaffoldContent(
        paddingValues: PaddingValues,
        recipeTitle: String = "",
        recipeDescription: String = "",
        editRecipe: Boolean,
        onRecipeTitleChange: (String) -> Unit,
        onRecipeDescriptionChange: (String) -> Unit,
    ) {

        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {

            PingwinekCooksComposables.SpacerSmall()

            EditableText(
                text = recipeTitle,
                editable = editRecipe,
                onValueChange = onRecipeTitleChange
            )
            EditableText(
                text = recipeDescription,
                editable = editRecipe,
                onValueChange = onRecipeDescriptionChange
            )
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