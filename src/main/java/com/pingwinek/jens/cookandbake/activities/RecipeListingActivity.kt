package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity.ScaffoldContent
import com.pingwinek.jens.cookandbake.viewModels.RecipeListingViewModel
import java.util.LinkedList

class RecipeListingActivity : AppCompatActivity() {

    private lateinit var recipeListingModel: RecipeListingViewModel
    private lateinit var auth: FirebaseAuth

    private lateinit var recipeListData: LiveData<LinkedList<Recipe>>

    override fun onCreate(savedInstanceState: Bundle?) {
        // needed since Android 12
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        recipeListingModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(RecipeListingViewModel::class.java)

        recipeListData = recipeListingModel.recipeListData

        val startImpressumActivity: () -> Unit = {
            startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                .putExtra("title", getString(R.string.impressum))
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)))
        }

        val startDataProtectionActivity: () -> Unit = {
            startActivity(Intent(this@RecipeListingActivity, ImpressumActivity::class.java)
                .putExtra("title", getString(R.string.dataprotection))
                .putExtra("url", (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)))
        }

        val startSignInActivity: () -> Unit = {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        val optionItemImpressum = PingwinekCooksComposableHelpers.OptionItem(
            R.string.impressum,
            Icons.Filled.Info
        ) {
            startImpressumActivity()
        }

        val optionItemPrivacy = PingwinekCooksComposableHelpers.OptionItem(
            R.string.dataprotection,
            Icons.Filled.Lock
        ) {
            startDataProtectionActivity()
        }

        val optionItemRecipe = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.RECIPE.label,
            icon = PingwinekCooksComposableHelpers.Navigation.RECIPE.icon
        ) {}

        val optionItemProfileLoggedOut = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.LOGIN.label,
            icon = PingwinekCooksComposableHelpers.Navigation.LOGIN.icon
        ) {
            startSignInActivity()
        }

        val optionItemProfileLoggedIn = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.LOGIN.label,
            icon = Icons.Filled.Person
        ) {
            startSignInActivity()
        }

        val onOpenRecipe: (String) -> Unit = {
            openRecipeItem(it)
        }

        setContent {

            PingwinekCooksAppTheme {

                val recipes by recipeListData.observeAsState()

                val loggedIn by remember(recipes, auth.currentUser) {
                    mutableStateOf(auth.currentUser != null)
                }

                val verified by remember(recipes, auth.currentUser) {
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
                        loggedIn = loggedIn,
                        verified = verified,
                        onOpenRecipe = onOpenRecipe,
                        onShowSignIn = startSignInActivity,
                        onCheckDataProtection = startDataProtectionActivity
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