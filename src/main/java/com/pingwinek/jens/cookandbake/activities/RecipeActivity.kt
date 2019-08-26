package com.pingwinek.jens.cookandbake.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.Recipe
import com.pingwinek.jens.cookandbake.RecipeViewModel
import kotlinx.android.synthetic.main.recyclerview_ingredient_list_item.view.*
import java.lang.NumberFormatException

const val EXTRA_EDIT_RECIPE = "editRecipe"
const val EXTRA_INGREDIENT_ID = "ingredientID"

class RecipeActivity : BaseActivity(),
    IngredientListingFragment.OnListFragmentInteractionListener,
    ConfirmDialogFragment.ConfirmDialogListener {

    private val inputType = InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_FLAG_AUTO_CORRECT

    private lateinit var recipeModel: RecipeViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_recipe)

        recipeModel = ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(RecipeViewModel::class.java)

        if (intent.hasExtra(EXTRA_RECIPE_ID)) {
            recipeModel.loadData(intent.extras[EXTRA_RECIPE_ID] as Int)
        }

        val recipeData = recipeModel.recipeData

        val titleView = findViewById<TextView>(R.id.ingredientName)
        val descriptionView = findViewById<TextView>(R.id.ingredientUnity)
        val editTitleButtonView = findViewById<Button>(R.id.editIngredientButton)

        titleView.setOnEditorActionListener { textView, actionId, keyEvent ->
            Log.i("ActionListener", when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> "Next"
                EditorInfo.IME_ACTION_DONE -> "Done"
                else -> "Was anderes"
            })
            return@setOnEditorActionListener true
        }

        descriptionView.setOnEditorActionListener { textView, actionId, keyEvent ->
            recipeModel.isEditableTitle.postValue(false)
            return@setOnEditorActionListener true
        }

        recipeData.observe(this, Observer { recipe: Recipe? ->
            titleView.text = recipe?.title
            descriptionView.text = recipe?.description
        })

        recipeModel.isEditableTitle.observe(this, Observer { isEditable: Boolean? ->
            if (isEditable == null || isEditable == false) {
                editTitleButtonView.setBackgroundResource(R.drawable.icons8_bleistift_24)
                titleView.inputType = InputType.TYPE_NULL
                titleView.isFocusable = false
                titleView.isFocusableInTouchMode = false
                descriptionView.inputType = InputType.TYPE_NULL
                descriptionView.isFocusable = false
                descriptionView.isFocusableInTouchMode = false
            } else {
                editTitleButtonView.setBackgroundResource(R.drawable.icons8_haekchen_24)
                titleView.inputType = inputType
                titleView.isFocusable = true
                titleView.isFocusableInTouchMode = true
                descriptionView.inputType = inputType
                descriptionView.isFocusable = true
                descriptionView.isFocusableInTouchMode = true
            }
        })

        val pagerAdapter = RecipePagerAdapter(supportFragmentManager)
        val tabLayout = findViewById<TabLayout>(R.id.recipe_tablayout)
        val recipePager  = findViewById<ViewPager>(R.id.recipeTabs).apply {
            adapter = pagerAdapter
            tabLayout.setupWithViewPager(this)
        }
    }

    fun editTitleButton(button: View) {
        if (recipeModel.isEditableTitle.value == true) {
            recipeModel.isEditableTitle.postValue(false)
            recipeModel.save(
                findViewById<TextView>(R.id.ingredientName).text.toString(),
                findViewById<TextView>(R.id.ingredientUnity).text.toString())
        } else {
            recipeModel.isEditableTitle.postValue(true)
        }
    }

    override fun onListFragmentInteraction(ingredientId: Int?) {
        val intent = Intent(this, IngredientActivity::class.java)
        recipeModel.recipeData.value?.let { recipe ->
            intent.putExtra(EXTRA_RECIPE_ID, recipe.id)
            intent.putExtra(EXTRA_EDIT_RECIPE, recipeModel.isEditableTitle.value)
            ingredientId?.let {
                intent.putExtra(EXTRA_INGREDIENT_ID, ingredientId)
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
        confirmDialog.setArguments(args)
        confirmDialog.show(supportFragmentManager, "DeleteIngredient${button.tag}")

        Toast.makeText(this, "Ingredient ${button.tag}", Toast.LENGTH_LONG).show()
    }

    override fun onPositiveButton(ingredientId: String?) {
        ingredientId?.let { idAsString ->
            try {
                val idAsInt = idAsString.toInt()
                recipeModel.deleteIngredient(idAsInt)
                Toast.makeText(this, "Delete $idAsInt", Toast.LENGTH_LONG).show()
            } catch (exception: NumberFormatException) {
                Log.e(this::class.java.name, "Cannot parse $ingredientId into Integer")
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
            IngredientInstructionFragment()
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