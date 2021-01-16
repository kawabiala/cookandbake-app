package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel

class RecipeActivity : BaseActivity(),
    IngredientListingFragment.OnListFragmentInteractionListener,
    ConfirmDialogFragment.ConfirmDialogListener {

    private lateinit var recipeModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_recipe)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // the fab needs to sit in the Activity, does not work in the Fragment
        val fab = findViewById<FloatingActionButton>(R.id.recipeFab)
        fab.hide()
        fab.setOnClickListener {
            // Check if Android 10 or higher
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                Log.i(this::class.java.name, "not yet implemented for Android 11+")

/*
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

 */
            } else {
                startActivityForResult(
                    Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        //flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/pdf"
                    },
                    REQUEST_CODE_PDF
                )
            }
        }

        recipeModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(RecipeViewModel::class.java)

        if (intent.hasExtra(EXTRA_RECIPE_ID)) {
            intent.extras?.getInt(EXTRA_RECIPE_ID)?.let { id ->
                recipeModel.recipeId = id
            }
        }

        val titleView = findViewById<TextView>(R.id.recipeName)
        val descriptionView = findViewById<TextView>(R.id.recipeDescription)

        recipeModel.recipeData.observe(this, { recipe: Recipe? ->
            titleView.text = recipe?.title
            descriptionView.text = recipe?.description
            if (recipe?.uri == null) {
                fab.setImageResource(R.drawable.ic_action_add_white)
            } else {
                fab.setImageResource(R.drawable.ic_action_create_white)
            }
        })

        val onClickListener = { _: View ->
            startActivityForResult(Intent(this, RecipeEditActivity::class.java).also {
                it.putExtra(EXTRA_RECIPE_TITLE, recipeModel.recipeData.value?.title)
                it.putExtra(EXTRA_RECIPE_DESCRIPTION, recipeModel.recipeData.value?.description)
            }, REQUEST_CODE_TITLE)
        }

        titleView.setOnClickListener(onClickListener)
        descriptionView.setOnClickListener(onClickListener)

        val pagerAdapter = RecipePagerAdapter(
            supportFragmentManager,
            resources.getString(R.string.ingredients),
            resources.getString(R.string.instruction),
            resources.getString(R.string.pdf))

        val tabLayout = findViewById<TabLayout>(R.id.recipe_tablayout)
        findViewById<ViewPager>(R.id.recipeTabs).apply {
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
                    when (position) {
                        0 -> fab.hide()
                        1 -> fab.hide()
                        2 -> fab.show()
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // do nothing
                }
            })
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
    }

    override fun onResume() {
        super.onResume()
        recipeModel.loadData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_TITLE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.extras?.let {
                        it.getString(EXTRA_RECIPE_TITLE)?.let { title ->
                            recipeModel.saveRecipe(
                                title,
                                it.getString(EXTRA_RECIPE_DESCRIPTION, ""),
                                recipeModel.recipeData.value?.instruction ?: ""
                            )
                        }
                    }
                }
            }
            REQUEST_CODE_INSTRUCTION -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.extras?.let {
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
            }
            REQUEST_CODE_INGREDIENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.extras?.let {
                        val id = it.get(EXTRA_INGREDIENT_ID)?.run {
                            it.getInt(EXTRA_INGREDIENT_ID)
                        }
                        val name = it.getString(EXTRA_INGREDIENT_NAME)
                        val quantity = it.get(EXTRA_INGREDIENT_QUANTITY)?.run {
                            it.getDouble(EXTRA_INGREDIENT_QUANTITY)
                        }
                        val quantityVerbal = it.getString(EXTRA_INGREDIENT_QUANTITY_VERBAL)
                        val unity = it.getString(EXTRA_INGREDIENT_UNITY)
                        if (name != null) {
                            recipeModel.saveIngredient(id, name, quantity, quantityVerbal, unity)
                        }
                    }
                }
            }
            REQUEST_CODE_PDF -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { pdfUri ->
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
                            val inputStream = contentResolver.openInputStream(pdfUri) ?: return
                            val type = contentResolver.getType(pdfUri) ?: return
                            recipeModel.savePdf(inputStream, type)
                        }
                    }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onListFragmentInteraction(ingredient: Ingredient?) {
        val intent = Intent(this, IngredientActivity::class.java)
        recipeModel.recipeData.value?.let { recipe ->
            intent.putExtra(EXTRA_RECIPE_TITLE, recipe.title)
            ingredient?.let {
                intent.putExtra(EXTRA_INGREDIENT_ID, it.id)
                intent.putExtra(EXTRA_INGREDIENT_NAME, it.name)
                intent.putExtra(EXTRA_INGREDIENT_QUANTITY, it.quantity)
                intent.putExtra(EXTRA_INGREDIENT_QUANTITY_VERBAL, it.quantityVerbal)
                intent.putExtra(EXTRA_INGREDIENT_UNITY, it.unity)
            }
            startActivityForResult(intent, REQUEST_CODE_INGREDIENT)
        }
    }

    fun deleteIngredientButton(button: View) {

        val ingredient = recipeModel.ingredientListData.value?.find { item ->
            try {
                item.id == button.tag.toString().toInt()
            } catch (exception: NumberFormatException) {
                false
            }
        }

        val confirmDialog = ConfirmDialogFragment()
        val args = Bundle()
        args.putString("message", "Zutat ${ingredient?.name} wirklich löschen?")
        args.putString("remoteId", button.tag.toString())
        confirmDialog.arguments = args
        confirmDialog.show(supportFragmentManager, "DeleteIngredient${button.tag}")
    }

    override fun onPositiveButton(confirmItemId: String?) {
        confirmItemId?.let { idAsString ->
            try {
                val idAsInt = idAsString.toInt()
                recipeModel.deleteIngredient(idAsInt)
            } catch (exception: NumberFormatException) {
                Log.e(this::class.java.name, "Cannot parse $confirmItemId into Integer")
            }
        }
    }

    override fun onNegativeButton(confirmItemId: String?) {
        //Do nothing
    }

    fun delete() {
        AlertDialog.Builder(this).apply {
            setMessage(R.string.recipe_delete_confirm)
            setPositiveButton(R.string.yes) { _, _ ->
                recipeModel.delete()
                finish()
            }
            setNegativeButton(R.string.no) { _, _ ->
                // Do nothing
            }
        }.show()

    }

    private fun deletePdf() {
        AlertDialog.Builder(this).apply {
            setMessage(R.string.pdf_delete_confirm)
            setPositiveButton(R.string.yes) { _, _ ->
                recipeModel.recipeData.value?.title?.let { title ->
                    recipeModel.saveRecipe(
                        title,
                        recipeModel.recipeData.value?.description ?: "",
                        recipeModel.recipeData.value?.instruction ?: ""
                    )
                }
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
}

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
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> ingredientTitle
            1 -> instructionTitle
            else -> pdfTitle
        }
    }
}