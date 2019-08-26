package com.pingwinek.jens.cookandbake.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.pingwinek.jens.cookandbake.IngredientViewModel
import com.pingwinek.jens.cookandbake.R

class IngredientActivity : BaseActivity() {

    private val inputType = InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_FLAG_AUTO_CORRECT

    private lateinit var ingredientModel: IngredientViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_ingredient)

        val nameView = findViewById<TextView>(R.id.ingredientName)
        val unityView = findViewById<TextView>(R.id.ingredientUnity)
        val quantityView = findViewById<TextView>(R.id.ingredientQuantity)
        val editIngredientButtonView = findViewById<Button>(R.id.editIngredientButton)

        ingredientModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(IngredientViewModel::class.java)

        ingredientModel.ingredientData.observe(this, Observer { ingredient ->
            nameView.text = ingredient?.name
            quantityView.text = ingredient?.quantity.toString()
            unityView.text = ingredient?.unity
        })

        ingredientModel.isEditable.observe(this, Observer { isEditable: Boolean? ->
            if (isEditable == null || isEditable == false) {
                editIngredientButtonView.setBackgroundResource(R.drawable.icons8_bleistift_24)
                nameView.inputType = InputType.TYPE_NULL
                nameView.isFocusable = false
                nameView.isFocusableInTouchMode = false
                unityView.inputType = InputType.TYPE_NULL
                unityView.isFocusable = false
                unityView.isFocusableInTouchMode = false
                quantityView.inputType = InputType.TYPE_NULL
                quantityView.isFocusable = false
                quantityView.isFocusableInTouchMode = false
            } else {
                editIngredientButtonView.setBackgroundResource(R.drawable.icons8_haekchen_24)
                nameView.inputType = inputType
                nameView.isFocusable = true
                nameView.isFocusableInTouchMode = true
                unityView.inputType = inputType
                unityView.isFocusable = true
                unityView.isFocusableInTouchMode = true
                quantityView.inputType = inputType
                quantityView.isFocusable = true
                quantityView.isFocusableInTouchMode = true
            }
        })

        if (intent.hasExtra(EXTRA_INGREDIENT_ID)) {
            ingredientModel.loadData(intent.extras[EXTRA_INGREDIENT_ID] as Int)
        } else if (intent.hasExtra(EXTRA_RECIPE_ID)) {
            ingredientModel.newIngredient(intent.extras[EXTRA_RECIPE_ID] as Int)
        }

        if (intent.hasExtra(EXTRA_EDIT_RECIPE) && intent.extras[EXTRA_EDIT_RECIPE] as Boolean) {
            ingredientModel.isEditable.postValue(true)
        }
    }

    fun editIngredientButton(button: View) {
        if (ingredientModel.isEditable.value == true) {
            ingredientModel.isEditable.postValue(false)
            ingredientModel.save(
                findViewById<TextView>(R.id.ingredientName).text.toString(),
                findViewById<TextView>(R.id.ingredientQuantity).text.toString().toDoubleOrNull(),
                findViewById<TextView>(R.id.ingredientUnity).text.toString())
        } else {
            ingredientModel.isEditable.postValue(true)
        }
    }


}