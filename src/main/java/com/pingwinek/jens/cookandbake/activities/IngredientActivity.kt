package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.utils.Utils.quantityToDouble
import com.pingwinek.jens.cookandbake.utils.Utils.quantityToString

class IngredientActivity : BaseActivity() {

    private var ingredientId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_ingredient)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val ingredientView = findViewById<TextView>(R.id.ingredientName)

        intent?.extras?.let {
            supportActionBar?.title = it.getString(EXTRA_RECIPE_TITLE)
            it.get(EXTRA_INGREDIENT_ID)?.run {
                ingredientId = it.getInt(EXTRA_INGREDIENT_ID)
            }
            ingredientView.text = it.getString(EXTRA_INGREDIENT_NAME)
            findViewById<TextView>(R.id.ingredientQuantity).text =
                quantityToString(it.getDouble(EXTRA_INGREDIENT_QUANTITY))
            findViewById<TextView>(R.id.ingredientQuantityVerbal).text =
                it.getString(EXTRA_INGREDIENT_QUANTITY_VERBAL)
            findViewById<TextView>(R.id.ingredientUnity).text = it.getString(EXTRA_INGREDIENT_UNITY)
        }

        ingredientView.requestFocus()

        optionMenu.apply {
            addMenuEntry(
                OPTION_MENU_DONE,
                resources.getString(R.string.save),
                R.drawable.ic_action_done_black,
                true
            ) {
                save()
                true
            }
        }
    }

    private fun save() {
        val ingredientName = findViewById<TextView>(R.id.ingredientName).text.toString()
        val ingredientQuantity = quantityToDouble(findViewById<TextView>(R.id.ingredientQuantity).text.toString())
        val ingredientQuantityVerbal = findViewById<TextView>(R.id.ingredientQuantityVerbal).text.toString()
        val ingredientUnity = findViewById<TextView>(R.id.ingredientUnity).text.toString()

        if (ingredientQuantity == 0.0 && ingredientQuantityVerbal.isNotEmpty()) {
            AlertDialog.Builder(this).apply {
                setMessage(resources.getString(R.string.ingredient_quantity_unequal_verbal))
                setPositiveButton("Ok") { _, _ ->
                    // do nothing
                }
            }.create().show()
        } else {
            setResult(Activity.RESULT_OK, Intent().also {
                it.putExtra(EXTRA_INGREDIENT_ID, ingredientId)
                it.putExtra(EXTRA_INGREDIENT_NAME, ingredientName)
                it.putExtra(EXTRA_INGREDIENT_QUANTITY, ingredientQuantity)
                it.putExtra(EXTRA_INGREDIENT_QUANTITY_VERBAL, ingredientQuantityVerbal)
                it.putExtra(EXTRA_INGREDIENT_UNITY, ingredientUnity)
            })
            finish()
        }
    }

}