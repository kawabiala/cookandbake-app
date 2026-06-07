package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.EXTRA_RECIPE_ID
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.AuthService
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity.CategoriesDrawerSheet
import com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity.ScaffoldContent
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel
import com.pingwinek.jens.cookandbake.viewModels.RecipeListingViewModel
import kotlinx.coroutines.launch
import java.util.LinkedList

class RecipeListingActivity : AppCompatActivity() {

    private lateinit var recipeListingModel: RecipeListingViewModel
    private lateinit var authenticationViewModel: AuthenticationViewModel

    private lateinit var recipeListData: LiveData<LinkedList<Recipe>>
    private lateinit var recipesByLabelListData: LiveData<LinkedList<Pair<String, LinkedList<Recipe>>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        // needed since Android 12
        installSplashScreen()

        super.onCreate(savedInstanceState)

        recipeListingModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(RecipeListingViewModel::class.java)

        authenticationViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(AuthenticationViewModel::class.java)

        recipeListData = recipeListingModel.recipeListData
        recipesByLabelListData = recipeListingModel.recipesByLabelListData

        authenticationViewModel.checkAuthStatus()

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

        val startLabelManagementActivity: () -> Unit = {
            startActivity(Intent(this@RecipeListingActivity, LabelManagementActivity::class.java))
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

        val optionItemLabelManagement = PingwinekCooksComposableHelpers.OptionItem(
            R.string.manage_labels,
            Icons.AutoMirrored.Outlined.Label
        ) {
            startLabelManagementActivity()
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

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                val scope = rememberCoroutineScope()
                val close = fun () { scope.launch { drawerState.close() }}

                val recipes by recipeListData.observeAsState()
                val recipesByLabel by recipesByLabelListData.observeAsState()

                val authStatus by authenticationViewModel.authStatus.observeAsState()
                val loggedIn = authStatus == AuthService.AuthStatus.SIGNED_IN
                val verified = authStatus == AuthService.AuthStatus.VERIFIED

                var labelFilter: String? by remember {
                    mutableStateOf(null)
                }

                val filteredRecipes: List<Recipe> by remember(recipes, recipesByLabel, labelFilter) {
                    mutableStateOf(
                        (if (labelFilter == null) recipes else recipesByLabel?.toMap()[labelFilter]) ?: listOf()
                    )
                }

                val categories = (recipesByLabel?.map { pair ->
                    Pair(
                        first = pair.first,
                        second = {
                            labelFilter = pair.first
                            close()
                        }
                    )
                } ?: listOf())
                    .toMutableList()
                    .also { map ->
                        map.add(0, Pair(
                            first = stringResource(R.string.all),
                            second = {
                                labelFilter = null
                                close()
                            }
                        ))
                    }

                val optionItemMenu by remember(loggedIn, verified) {
                    mutableStateOf(
                        if (loggedIn && verified) {
                            PingwinekCooksComposableHelpers.OptionItem(
                                labelResourceId = R.string.labels,
                                icon = Icons.Filled.Menu
                            ) {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        } else {
                            null
                        }
                    )
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        CategoriesDrawerSheet(categories)
                    }
                ) {

                    PingwinekCooksScaffold(
                        title = "PingwinekCooks",
                        showDropDown = true,
                        dropDownOptions = listOf(
                            optionItemPrivacy,
                            optionItemImpressum
                        ),
                        optionItemLeft = optionItemMenu,
                        optionItemMid = optionItemLabelManagement,
                        selectedNavigationBarItem = PingwinekCooksComposableHelpers.Navigation.RECIPE.ordinal,
                        navigationBarEnabled = true,
                        navigationBarItems = listOf(
                            optionItemRecipe,
                            if (loggedIn) optionItemProfileLoggedIn else optionItemProfileLoggedOut
                        ),
                        showFab = verified,
                        fabIcon = Icons.Filled.Add,
                        fabIconLabel = getString(R.string.add_recipe),
                        fabContainerColor = MaterialTheme.colorScheme.primary,
                        fabIconColor = MaterialTheme.colorScheme.onPrimary,
                        onFabClicked = { openRecipeItem(null) }
                    ) { paddingValues ->
                        ScaffoldContent(
                            paddingValues = paddingValues,
                            recipes = LinkedList(filteredRecipes),
                            label = labelFilter,
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
    }

    override fun onResume() {
        super.onResume()

        recipeListingModel.loadData()
        authenticationViewModel.checkAuthStatus()
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