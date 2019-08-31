package com.pingwinek.jens.cookandbake.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.pingwinek.jens.cookandbake.viewModels.IngredientViewModel
import com.pingwinek.jens.cookandbake.R

class IngredientActivity : BaseActivity() {

    private lateinit var ingredientModel: IngredientViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_ingredient)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val nameView = findViewById<TextView>(R.id.ingredientName)
        val unityView = findViewById<TextView>(R.id.ingredientUnity)
        val quantityView = findViewById<TextView>(R.id.ingredientQuantity)

        ingredientModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(IngredientViewModel::class.java)

        ingredientModel.ingredientData.observe(this, Observer { ingredient ->
            nameView.text = ingredient?.name
            quantityView.text = ingredient?.quantity.toString()
            unityView.text = ingredient?.unity
        })

        ingredientModel.recipeData.observe(this, Observer { recipe ->
            supportActionBar?.title = recipe?.title
        })

        if (intent.hasExtra(EXTRA_RECIPE_ID)) {
            intent.extras?.getInt(EXTRA_RECIPE_ID)?.let { id ->
                ingredientModel.loadRecipe(id)
            }

            if (intent.hasExtra(EXTRA_INGREDIENT_ID)) {
                intent.extras?.getInt(EXTRA_INGREDIENT_ID)?.let { id ->
                    ingredientModel.loadData(id)
                }
            } else {
                intent.extras?.getInt(EXTRA_RECIPE_ID)?.let { id ->
                    ingredientModel.newIngredient(id)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.instruction_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done -> {
                save()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    fun save() {
        ingredientModel.save(
            findViewById<TextView>(R.id.ingredientName).text.toString(),
            findViewById<TextView>(R.id.ingredientQuantity).text.toString().toDoubleOrNull(),
            findViewById<TextView>(R.id.ingredientUnity).text.toString())
        finish()
    }
}