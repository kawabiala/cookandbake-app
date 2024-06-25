package com.pingwinek.jens.cookandbake.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModelProvider
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksScaffold
import com.pingwinek.jens.cookandbake.uiComponents.signInActivity.ScaffoldContent
import com.pingwinek.jens.cookandbake.viewModels.AuthenticationViewModel
import com.pingwinek.jens.cookandbake.viewModels.UserInfoViewModel

class SignInActivity : AppCompatActivity() {

    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var userInfoViewModel: UserInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authenticationViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(AuthenticationViewModel::class.java)

        userInfoViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(UserInfoViewModel::class.java)

        authenticationViewModel.authStatus.observe(this) {
            authenticationViewModel.retrieveEmail()
            userInfoViewModel.loadData()
        }

        val onClose: () -> Unit = {
            startActivity(Intent(this, RecipeListingActivity::class.java))
            finish()
        }

        val optionItems = mutableListOf(
            PingwinekCooksComposableHelpers.OptionItem(
                R.string.dataprotection,
                Icons.Filled.Lock
            ) {
                startActivity(
                    Intent(this@SignInActivity, ImpressumActivity::class.java)
                        .putExtra("title", getString(R.string.dataprotection))
                        .putExtra(
                            "url",
                            (application as PingwinekCooksApplication).getURL(R.string.URL_DATAPROTECTION)
                        )
                )
            },
            PingwinekCooksComposableHelpers.OptionItem(
                R.string.impressum,
                Icons.Filled.Info
            ) {
                startActivity(
                    Intent(this@SignInActivity, ImpressumActivity::class.java)
                        .putExtra("title", getString(R.string.impressum))
                        .putExtra(
                            "url",
                            (application as PingwinekCooksApplication).getURL(R.string.URL_IMPRESSUM)
                        )
                )
            }
        )

        val optionItemRecipe = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.RECIPE.label,
            icon = PingwinekCooksComposableHelpers.Navigation.RECIPE.icon
        ) {
            onClose()
        }

        val optionItemProfileLoggedOut = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.LOGIN.label,
            icon = PingwinekCooksComposableHelpers.Navigation.LOGIN.icon
        ) {
        }

        val optionItemProfileLoggedIn = PingwinekCooksComposableHelpers.OptionItem(
            labelResourceId = PingwinekCooksComposableHelpers.Navigation.LOGIN.label,
            icon = Icons.Filled.Person
        ) { startActivity(Intent(this, SignInActivity::class.java))
        }

        setContent {
            PingwinekCooksAppTheme {
                PingwinekCooksScaffold(
                    title = getString(R.string.profile),
                    showDropDown = true,
                    dropDownOptions = optionItems,
                    selectedNavigationBarItem = PingwinekCooksComposableHelpers.Navigation.LOGIN.ordinal,
                    navigationBarEnabled = true,
                    navigationBarItems = listOf(
                        optionItemRecipe,
                        optionItemProfileLoggedIn
                    )
                ) { paddingValues ->
                   ScaffoldContent(
                       paddingValues = paddingValues,
                       authenticationViewModel = authenticationViewModel,
                       userInfoViewModel = userInfoViewModel,
                       onClose = onClose
                   )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        authenticationViewModel.checkAuthStatus()
        authenticationViewModel.retrieveEmail()
        authenticationViewModel.checkActionCodeForIntent(intent)

        userInfoViewModel.loadData()

    }

    /**
     * Called before onResume:
     * if there is a new intent, update the intent for this activity - otherwise the original
     * intent would be used; compare documentation in [android.app.Activity]
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            this.intent = it
        }
    }
}