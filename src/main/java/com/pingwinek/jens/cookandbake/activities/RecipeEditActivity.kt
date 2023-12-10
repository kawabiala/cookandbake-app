package com.pingwinek.jens.cookandbake.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_DESCRIPTION
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_TITLE
import com.pingwinek.jens.cookandbake.R

class RecipeEditActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addContentView(R.layout.activity_recipe_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val titleView = findViewById<TextView>(R.id.titleText)
        val descriptionView = findViewById<TextView>(R.id.descriptionText)

        intent.extras?.getString(EXTRA_RECIPE_TITLE)?.let { title ->
            titleView.text = title
        }

        intent.extras?.getString(EXTRA_RECIPE_DESCRIPTION)?.let { description ->
            descriptionView.text = description
        }

        // TODO remove this smoke test
        val auth = Firebase.auth
        val userID = auth.uid ?: ""

        val db = Firebase.firestore

        val recipe = db.collection("user")
            .document(userID)
            .collection("recipe")
            .document("recipeID")

        recipe.get().addOnSuccessListener { document ->
            if (document != null) {
                titleView.text = document.getString("title")
            } else {
                titleView.text = "something went wrong"
            }
        }.addOnFailureListener { exception ->
            titleView.text = exception.toString()
        }

        titleView.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(titleView, InputMethodManager.SHOW_IMPLICIT)

        optionMenu.apply {
            addMenuEntry(
                R.id.OPTION_MENU_DONE,
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
        setResult(Activity.RESULT_OK, Intent().also {
            it.putExtra(EXTRA_RECIPE_TITLE, findViewById<TextView>(R.id.titleText).text.toString())
            it.putExtra(EXTRA_RECIPE_DESCRIPTION, findViewById<TextView>(R.id.descriptionText).text.toString())
        })
        finish()
    }

}