package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.Utils.quantityToDouble
import com.pingwinek.jens.cookandbake.Utils.quantityToString

class IngredientActivity : BaseActivity() {

    var ingredientId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_ingredient)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent?.extras?.let {
            supportActionBar?.title = it.getString(EXTRA_RECIPE_TITLE)
            it.get(EXTRA_INGREDIENT_ID)?.run {
                ingredientId = it.getInt(EXTRA_INGREDIENT_ID)
            }
            findViewById<TextView>(R.id.ingredientName).text = it.getString(EXTRA_INGREDIENT_NAME)
            findViewById<TextView>(R.id.ingredientQuantity).text =
                quantityToString(it.getDouble(EXTRA_INGREDIENT_QUANTITY))
            findViewById<TextView>(R.id.ingredientUnity).text = it.getString(EXTRA_INGREDIENT_UNITY)
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

    private fun save() {
        setResult(Activity.RESULT_OK, Intent().also {
            it.putExtra(EXTRA_INGREDIENT_ID, ingredientId)
            it.putExtra(EXTRA_INGREDIENT_NAME, findViewById<TextView>(R.id.ingredientName).text.toString())
            it.putExtra(EXTRA_INGREDIENT_QUANTITY, quantityToDouble(findViewById<TextView>(R.id.ingredientQuantity).text.toString()))
            it.putExtra(EXTRA_INGREDIENT_UNITY, findViewById<TextView>(R.id.ingredientUnity).text.toString())
        })
        finish()
    }

}