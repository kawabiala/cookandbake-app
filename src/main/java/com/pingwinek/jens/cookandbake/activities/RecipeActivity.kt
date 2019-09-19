package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.arch.lifecycle.Lifecycle
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
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.viewModels.RecipeViewModel

class RecipeActivity : BaseActivity(),
    IngredientListingFragment.OnListFragmentInteractionListener,
    ConfirmDialogFragment.ConfirmDialogListener {

    private lateinit var recipeModel: RecipeViewModel
    //private lateinit var recipeData: LiveData<Recipe?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_recipe)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recipeModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
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

        val onClickListener  = { _: View ->
            startActivityForResult(Intent(this, RecipeEditActivity::class.java).also {
                it.putExtra(EXTRA_RECIPE_TITLE, recipeModel.recipeData.value?.title)
                it.putExtra(EXTRA_RECIPE_DESCRIPTION, recipeModel.recipeData.value?.description)
            }, REQUEST_CODE_TITLE)
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
        recipeModel.loadData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_TITLE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.extras?.let {
                        it.getString(EXTRA_RECIPE_TITLE)?.let { title ->
                            recipeModel.save(title, it.getString(EXTRA_RECIPE_DESCRIPTION, ""), recipeModel.recipeData.value?.instruction ?: "")
                        }
                    }
                }
            }
            REQUEST_CODE_INSTRUCTION -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.extras?.let {
                        it.getString(EXTRA_RECIPE_INSTRUCTION)?.let { instruction ->
                            recipeModel.recipeData.value?.title?.let { title ->
                                recipeModel.save(title, recipeModel.recipeData.value?.description ?: "", instruction)
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
                        if (name != null) { recipeModel.saveIngredient(id, name, quantity, unity) }
                    }
                }
            }
        }
    }

    override fun onLogin(intent: Intent) {
        if (this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            recipeModel.loadData()
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
        args.putString("id", button.tag.toString())
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