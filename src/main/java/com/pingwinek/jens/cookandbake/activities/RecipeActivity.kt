package com.pingwinek.jens.cookandbake.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.Recipe
import com.pingwinek.jens.cookandbake.RecipeViewModel

class RecipeActivity : BaseActivity() {

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

        val titleView = findViewById<TextView>(R.id.recipeTitle)
        val descriptionView = findViewById<TextView>(R.id.recipeDescription)
        val editTitleButtonView = findViewById<Button>(R.id.editTitleButton)

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

    }

    fun editTitleButton(button: View) {
        if (recipeModel.isEditableTitle.value == true) {
            recipeModel.isEditableTitle.postValue(false)
            recipeModel.save(
                findViewById<TextView>(R.id.recipeTitle).text.toString(),
                findViewById<TextView>(R.id.recipeDescription).text.toString())
        } else {
            recipeModel.isEditableTitle.postValue(true)
        }
    }
}