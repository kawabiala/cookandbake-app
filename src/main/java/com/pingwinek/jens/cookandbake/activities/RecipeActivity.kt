package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

        recipeModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )
            .get(RecipeViewModel::class.java)

        if (intent.hasExtra(EXTRA_RECIPE_ID)) {
            intent.extras?.getInt(EXTRA_RECIPE_ID)?.let { id ->
                recipeModel.recipeId = id
            }
        }

        val titleView = findViewById<TextView>(R.id.recipeName)
        val descriptionView = findViewById<TextView>(R.id.recipeDescription)

        recipeModel.recipeData.observe(this, Observer { recipe: Recipe? ->
            titleView.text = recipe?.title
            descriptionView.text = recipe?.description
        })

        val onClickListener = { _: View ->
            startActivityForResult(Intent(this, RecipeEditActivity::class.java).also {
                it.putExtra(EXTRA_RECIPE_TITLE, recipeModel.recipeData.value?.title)
                it.putExtra(EXTRA_RECIPE_DESCRIPTION, recipeModel.recipeData.value?.description)
            }, REQUEST_CODE_TITLE)
        }

        titleView.setOnClickListener(onClickListener)
        descriptionView.setOnClickListener(onClickListener)

        val pagerAdapter = RecipePagerAdapter(supportFragmentManager)
        val tabLayout = findViewById<TabLayout>(R.id.recipe_tablayout)
        findViewById<androidx.viewpager.widget.ViewPager>(R.id.recipeTabs).apply {
            adapter = pagerAdapter
            tabLayout.setupWithViewPager(this)
        }
    }

    override fun getOptionsMenu(): OptionMenu {
        return OptionMenu().apply {
            addMenuEntry(OPTION_MENU_DELETE, resources.getString(R.string.delete)) {
                delete()
                true
            }/*.apply {
                iconId = R.drawable.ic_action_delete_black
                ifRoom = true
            }*/
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
                        val unity = it.getString(EXTRA_INGREDIENT_UNITY)
                        if (name != null) {
                            recipeModel.saveIngredient(id, name, quantity, unity)
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
            setPositiveButton(R.string.yes) { dialog, which ->
                finish()
                recipeModel.delete()
            }
            setNegativeButton(R.string.no) { _, _ ->
                // Do nothing
            }
        }.show()

    }
}

class RecipePagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager) :
    androidx.fragment.app.FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return if (position == 0) {
            IngredientListingFragment()
        } else {
            InstructionFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            "Zutaten"
        } else {
            "Anleitung"
        }
    }
}