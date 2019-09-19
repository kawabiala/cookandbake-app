package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_DESCRIPTION
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_TITLE
import com.pingwinek.jens.cookandbake.R

class RecipeEditActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_recipe_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()

        intent.extras?.getString(EXTRA_RECIPE_TITLE)?.let { title ->
            findViewById<TextView>(R.id.titleText).text = title
        }

        intent.extras?.getString(EXTRA_RECIPE_DESCRIPTION)?.let { description ->
            findViewById<TextView>(R.id.descriptionText).text = description
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
            it.putExtra(EXTRA_RECIPE_TITLE, findViewById<TextView>(R.id.titleText).text.toString())
            it.putExtra(EXTRA_RECIPE_DESCRIPTION, findViewById<TextView>(R.id.descriptionText).text.toString())
        })
        finish()
    }

}