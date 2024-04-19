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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksScaffold
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

        val optionItemImpressum = PingwinekCooksComposables.OptionItem(
            getString(R.string.impressum),
            Icons.Filled.Info
        ) {
            startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                .putExtra("title", getString(R.string.impressum))
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)))
        }

        val optionItemPrivacy = PingwinekCooksComposables.OptionItem(
            getString(R.string.dataprotection),
            Icons.Filled.Lock
        ) {
            startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                .putExtra("title", getString(R.string.dataprotection))
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)))
        }

        val optionItemRecipe = PingwinekCooksComposables.OptionItem(
            label = getString(PingwinekCooksComposables.Navigation.RECIPE.label),
            icon = PingwinekCooksComposables.Navigation.RECIPE.icon
        ) {}

        val optionItemProfileLoggedOut = PingwinekCooksComposables.OptionItem(
            label = getString(PingwinekCooksComposables.Navigation.LOGIN.label),
            icon = PingwinekCooksComposables.Navigation.LOGIN.icon
        ) {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        val optionItemProfileLoggedIn = PingwinekCooksComposables.OptionItem(
            label = getString(PingwinekCooksComposables.Navigation.LOGIN.label),
            icon = Icons.Filled.Person
        ) {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        setContent {
            PingwinekCooksAppTheme {

                var selectedNavigationBarItem by remember {
                    mutableIntStateOf(PingwinekCooksComposables.Navigation.RECIPE.ordinal)
                }

                var loggedIn by remember {
                    mutableStateOf(false)
                }

                LifecycleResumeEffect(LocalLifecycleOwner.current) {
                    loggedIn = auth.currentUser != null && auth.currentUser!!.isEmailVerified
                    selectedNavigationBarItem = PingwinekCooksComposables.Navigation.RECIPE.ordinal
                    recipeListingModel.loadData()

                    onPauseOrDispose {
                        loggedIn = false
                    }
                }

                PingwinekCooksScaffold(
                    title = "PingwinekCooks",
                    showDropDown = true,
                    dropDownOptions = listOf(
                        optionItemPrivacy,
                        optionItemImpressum
                    ),
                    selectedNavigationBarItem = selectedNavigationBarItem,
                    navigationBarEnabled = true,
                    navigationBarItems = listOf(
                        optionItemRecipe,
                        if (loggedIn) optionItemProfileLoggedIn else optionItemProfileLoggedOut
                    ),
                    onSelectedNavigationItemChange = { itemId -> selectedNavigationBarItem = itemId },
                    showFab = loggedIn,
                    fabIcon = Icons.Filled.Add,
                    fabIconLabel = getString(R.string.add_recipe),
                    onFabClicked = { openRecipeItem(null) }
                ) { paddingValues ->
                    ScaffoldContent(
                        paddingValues = paddingValues,
                        loggedIn = loggedIn
                    )
                }
            }
        }
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
        loggedIn: Boolean
    ) {
        val recipes = recipeListData.observeAsState()
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            PingwinekCooksComposables.SpacerSmall()

            if (loggedIn) {
                recipes.value?.forEachIndexed { index, recipe ->
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