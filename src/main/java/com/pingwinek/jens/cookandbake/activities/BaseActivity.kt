package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksNavigationBar
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksTopAppBar
import com.pingwinek.jens.cookandbake.theming.Margins
import java.util.LinkedList

/**
 */
abstract class BaseActivity : AppCompatActivity() {

    enum class Navigation(val label: Int, val icon: ImageVector) {
        RECIPE(R.string.recipes, Icons.Outlined.RestaurantMenu),
        LOGIN(R.string.login, Icons.Outlined.Person)
    }

    private var title = ""
    private var showDrowDown = false
    private var optionItemLeft: PingwinekCooksComposables.OptionItem? = null
    private var optionItemMid: PingwinekCooksComposables.OptionItem? = null
    private var optionItemRight: PingwinekCooksComposables.OptionItem? = null

    private var dropDownOptions = mutableListOf<PingwinekCooksComposables.OptionItem>()

    private var selectedNavigationBarItem: Int = 0
    private var navigationBarEnabled = true
    /*
        private val recipeIcon = Icons.Outlined.RestaurantMenu
        private var loginIcon = Icons.Outlined.Person
        private lateinit var recipeLabel: String
        private lateinit var loginLabel: String
    */    private var navigationBarItems = listOf<PingwinekCooksComposables.OptionItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PingwinekCooksAppTheme {
                BaseScaffold()
            }
        }
    }

    @Composable
    fun BasePreview() {
        PingwinekCooksAppTheme {
            BaseScaffold()
        }
    }

    @Composable
    fun BaseScaffold() {
        Scaffold(
            contentWindowInsets = WindowInsets(Margins.MARGIN_LEFT_RIGHT, Margins.MARGIN_TOP_BOTTOM, Margins.MARGIN_LEFT_RIGHT, Margins.MARGIN_TOP_BOTTOM),
            topBar = {
                PingwinekCooksTopAppBar(
                    title,
                    showDrowDown,
                    dropDownOptions,
                    optionItemLeft,
                    optionItemMid,
                    optionItemRight
                )
            },
            bottomBar = {
                PingwinekCooksNavigationBar(
                    selectedNavigationBarItem,
                    navigationBarEnabled,
                    navigationBarItems
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                ScaffoldContent()
            }
        }
    }

    @Composable
    protected abstract fun ScaffoldContent()

    protected fun configureTopBar(
        title: String,
        optionItemLeft: PingwinekCooksComposables.OptionItem? = null,
        optionItemMid: PingwinekCooksComposables.OptionItem? = null,
        optionItemRight: PingwinekCooksComposables.OptionItem? = null
    ) {
        this.title = title
        this.optionItemLeft = optionItemLeft
        this.optionItemRight = optionItemMid
        this.optionItemRight = optionItemRight
    }

    protected fun configureNavigationBar(
        selectedItem: Navigation = Navigation.RECIPE,
        enabled: Boolean = true,
        onRecipeClickAction: () -> Unit = {},
        onLoginClickAction: () -> Unit = {}
    ) {
        selectedNavigationBarItem = selectedItem.ordinal
        navigationBarEnabled = enabled
        val navBarItems = LinkedList<PingwinekCooksComposables.OptionItem>()
        Navigation.entries.forEach { navigationEntry ->
            val onClickAction = if (navigationEntry == Navigation.RECIPE) {
                onRecipeClickAction
            } else {
                onLoginClickAction
            }
            navBarItems.add(PingwinekCooksComposables.OptionItem(
                getString(navigationEntry.label),
                navigationEntry.icon,
                onClickAction)
            )
        }
        navigationBarItems = navBarItems
    }

    protected fun configureDropDown(vararg optionItem: PingwinekCooksComposables.OptionItem) {
        showDrowDown = true
        val optionItems = mutableListOf<PingwinekCooksComposables.OptionItem>()
        optionItem.iterator().forEach { item ->
            optionItems.add(item)
        }
        dropDownOptions = optionItems
    }

}