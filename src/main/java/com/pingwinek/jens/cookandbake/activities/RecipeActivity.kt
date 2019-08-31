package com.pingwinek.jens.cookandbake.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.Recipe
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel

const val EXTRA_EDIT_RECIPE = "editRecipe"
const val EXTRA_INGREDIENT_ID = "ingredientID"

class RecipeActivity : BaseActivity(),
    IngredientListingFragment.OnListFragmentInteractionListener,
    ConfirmDialogFragment.ConfirmDialogListener {

    private lateinit var recipeModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_recipe)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recipeModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(RecipeViewModel::class.java)

        val recipeData = recipeModel.recipeData

        val titleView = findViewById<TextView>(R.id.recipeName)
        val descriptionView = findViewById<TextView>(R.id.recipeDescription)

        recipeData.observe(this, Observer { recipe: Recipe? ->
            titleView.text = recipe?.title
            descriptionView.text = recipe?.description
        })

        val onClickListener  = { _: View ->
            val intent = Intent(this, RecipeEditActivity::class.java)
            intent.putExtra(EXTRA_RECIPE_ID, recipeData.value?.id)
            startActivity(intent)
        }

        titleView.setOnClickListener(onClickListener)
        descriptionView.setOnClickListener(onClickListener)

        val pagerAdapter = RecipePagerAdapter(supportFragmentManager)
        val tabLayout = findViewById<TabLayout>(R.id.recipe_tablayout)
        findViewById<ViewPager>(R.id.recipeTabs).apply {
            adapter = pagerAdapter
            tabLayout.setupWithViewPager(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra(EXTRA_RECIPE_ID)) {
            intent.extras?.getInt(EXTRA_RECIPE_ID)?.let { id ->
                recipeModel.loadData(id)
            }
        }
    }

    override fun onListFragmentInteraction(id: Int?) {
        val intent = Intent(this, IngredientActivity::class.java)
        recipeModel.recipeData.value?.let { recipe ->
            intent.putExtra(EXTRA_RECIPE_ID, recipe.id)
            intent.putExtra(EXTRA_EDIT_RECIPE, recipeModel.isEditableTitle.value)
            id?.let {
                intent.putExtra(EXTRA_INGREDIENT_ID, id)
            }
            startActivity(intent)
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
        args.putString("id", button.tag.toString())
        confirmDialog.arguments = args
        confirmDialog.show(supportFragmentManager, "DeleteIngredient${button.tag}")

        Toast.makeText(this, "Ingredient ${button.tag}", Toast.LENGTH_LONG).show()
    }

    override fun onPositiveButton(confirmItemId: String?) {
        confirmItemId?.let { idAsString ->
            try {
                val idAsInt = idAsString.toInt()
                recipeModel.deleteIngredient(idAsInt)
                Toast.makeText(this, "Delete $idAsInt", Toast.LENGTH_LONG).show()
            } catch (exception: NumberFormatException) {
                Log.e(this::class.java.name, "Cannot parse $confirmItemId into Integer")
            }
        }
    }

    override fun onNegativeButton(confirmItemId: String?) {
        Toast.makeText(this, "Cancel", Toast.LENGTH_LONG).show()
        //Do nothing
    }
}

class RecipePagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
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