package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.composables.PingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.composables.PingwinekCooks.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.composables.PingwinekCooks.SpacerSmall
import com.pingwinek.jens.cookandbake.composables.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.viewModels.RecipeListingViewModel
import java.util.LinkedList

class RecipeListingActivity : AppCompatActivity() {

    private lateinit var recipeListingModel: RecipeListingViewModel
    private lateinit var auth: FirebaseAuth

    private lateinit var recipeListData: LiveData<LinkedList<Recipe>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        recipeListingModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(RecipeListingViewModel::class.java)

        recipeListData = recipeListingModel.recipeListData

        val optionItemImpressum = PingwinekCooksComposableHelpers.OptionItem(
            R.string.impressum,
            Icons.Filled.Info
        ) {
            startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                .putExtra("title", getString(R.string.impressum))
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)))
        }

        val optionItemPrivacy = PingwinekCooksComposableHelpers.OptionItem(
            R.string.dataprotection,
            Icons.Filled.Lock
        ) {
            startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                .putExtra("title", getString(R.string.dataprotection))
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)))
        }

        val optionItemRecipe = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.RECIPE.label,
            icon = PingwinekCooksComposableHelpers.Navigation.RECIPE.icon
        ) {}

        val optionItemProfileLoggedOut = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.LOGIN.label,
            icon = PingwinekCooksComposableHelpers.Navigation.LOGIN.icon
        ) {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        val optionItemProfileLoggedIn = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.LOGIN.label,
            icon = Icons.Filled.Person
        ) {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        setContent {
            PingwinekCooksAppTheme {

                val recipes by recipeListData.observeAsState()

                val loggedIn by remember(recipes, auth.currentUser) {
                    mutableStateOf(auth.currentUser?.isEmailVerified == true)
                }

                PingwinekCooksScaffold(
                    title = "PingwinekCooks",
                    showDropDown = true,
                    dropDownOptions = listOf(
                        optionItemPrivacy,
                        optionItemImpressum
                    ),
                    selectedNavigationBarItem = PingwinekCooksComposableHelpers.Navigation.RECIPE.ordinal,
                    navigationBarEnabled = true,
                    navigationBarItems = listOf(
                        optionItemRecipe,
                        if (loggedIn) optionItemProfileLoggedIn else optionItemProfileLoggedOut
                    ),
                    showFab = loggedIn,
                    fabIcon = Icons.Filled.Add,
                    fabIconLabel = getString(R.string.add_recipe),
                    onFabClicked = { openRecipeItem(null) }
                ) { paddingValues ->
                    ScaffoldContent(
                        paddingValues = paddingValues,
                        recipes = recipes,
                        loggedIn = loggedIn
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        recipeListingModel.loadData()
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
//// Data Migration - used one time for migrating data Jan 2024
////////////////////////////////////////////////////////////////////////////////////////////////////
/*
    override fun onResume() {
        super.onResume()

        AlertDialog.Builder(this).apply {
            setMessage("Migrate?")
            setPositiveButton("yes") { _, _ ->
                recipeListingModel.migrateData()
            }
            setNegativeButton("no") { _, _ ->

            }
        }.show()
    }
*/
////////////////////////////////////////////////////////////////////////////////////////////////////

    @Composable
    fun ScaffoldContent(
        paddingValues: PaddingValues,
        recipes: LinkedList<Recipe>?,
        loggedIn: Boolean
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            SpacerSmall()

            if (loggedIn) {
                recipes?.forEachIndexed { index, recipe ->
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(top = 5.dp, bottom = 5.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    }
                    key(recipe.id) {
                        Recipe(recipe = recipe) { recipeId ->
                            openRecipeItem(recipeId)
                        }
                    }
                }
            } else {
                Text(getString(R.string.no_account))
            }
        }
    }

    @Composable
    private fun Recipe(
        recipe: Recipe,
        onClick: (String) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(recipe.id) }
        ) {
            Text(recipe.title)
            Text(recipe.description ?: "")
        }
    }

    // TODO remove function together with view
    fun onRecipeItemClick(recipeItem: View) {
        openRecipeItem(recipeItem.tag as String)
    }

    private fun openRecipeItem(itemId: String?) {
        val intent = Intent(this, RecipeActivity::class.java)
        itemId?.let {
            intent.apply {
                putExtra(EXTRA_RECIPE_ID, it)
            }
        }
        startActivity(intent)
    }
}