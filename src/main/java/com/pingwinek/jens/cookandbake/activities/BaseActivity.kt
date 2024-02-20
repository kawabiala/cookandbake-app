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
import androidx.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksNavigationBar
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksTopAppBar
import com.pingwinek.jens.cookandbake.theming.Margins

/**
 */
abstract class BaseActivity : AppCompatActivity() {

    protected object TopBar {
        var title = ""
    }

    private var title: String = ""
    private var showDrowDown = false
    private var optionItemLeft: PingwinekCooksComposables.OptionItem? = null
    private var optionItemMid: PingwinekCooksComposables.OptionItem? = null
    private var optionItemRight: PingwinekCooksComposables.OptionItem? = null

    private val dropDownOptions = mutableListOf<PingwinekCooksComposables.OptionItem>()
    private val navigationBarItems = mutableListOf<PingwinekCooksComposables.NavigationBarItem>()

    private val recipeIcon = Icons.Outlined.RestaurantMenu
    private var loginIcon = Icons.Outlined.Person
    private lateinit var recipeLabel: String
    private lateinit var loginLabel: String
    protected val recipeIsSelected = MutableLiveData(false)
    protected val loginIsSelected = MutableLiveData(false)
    protected val recipeOnClick = MutableLiveData({})
    protected val loginOnClick = MutableLiveData({})
    protected val navigationBarItemsEnabled = MutableLiveData(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeLabel = getString(R.string.recipes)
        loginLabel = getString(R.string.profile)

        setContent {
            PingwinekCooksAppTheme {
                BaseScaffold()
            }
        }

        recipeIsSelected.observe(this) { fillNavigationBarItemsList() }
        recipeOnClick.observe(this) { fillNavigationBarItemsList() }
        loginIsSelected.observe(this) { fillNavigationBarItemsList() }
        loginOnClick.observe(this) { fillNavigationBarItemsList() }
        navigationBarItemsEnabled.observe(this) { fillNavigationBarItemsList() }
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
                PingwinekCooksTopAppBar(TopBar.title, showDrowDown, dropDownOptions, optionItemLeft,
                    this.optionItemMid,
                    this.optionItemRight
                )
            },
            bottomBar = { PingwinekCooksNavigationBar(navigationBarItems) }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                ScaffoldContent()
            }
        }
    }

    @Composable
    abstract fun ScaffoldContent()

    fun configureTopBar(
        title: String,
        showDropDown: Boolean = false,
        optionItemLeft: PingwinekCooksComposables.OptionItem? = null,
        optionItemMid: PingwinekCooksComposables.OptionItem? = null,
        optionItemRight: PingwinekCooksComposables.OptionItem? = null
    ) {
        this.title = title
        this.showDrowDown = showDropDown
        this.optionItemLeft = optionItemLeft
        this.optionItemRight = optionItemMid
        this.optionItemRight = optionItemRight
    }

    fun addDropDownOptionItem(optionItem: PingwinekCooksComposables.OptionItem) {
        dropDownOptions.add(optionItem)
    }

    private fun createRecipeNavigationBarItem(): PingwinekCooksComposables.NavigationBarItem {
        return PingwinekCooksComposables.NavigationBarItem(
            recipeIcon,
            recipeLabel,
            recipeIsSelected.value ?: false && navigationBarItemsEnabled.value ?: false,
            navigationBarItemsEnabled.value ?: true,
            recipeOnClick.value ?: {})
    }

    private fun createLoginNavigationBarItem(): PingwinekCooksComposables.NavigationBarItem {
        return PingwinekCooksComposables.NavigationBarItem(
            loginIcon,
            loginLabel,
            loginIsSelected.value ?: false && navigationBarItemsEnabled.value ?: false,
            navigationBarItemsEnabled.value ?: true,
            loginOnClick.value ?: {})
    }

    private fun fillNavigationBarItemsList() {
        navigationBarItems.apply {
            clear()
            add(createRecipeNavigationBarItem())
            add(createLoginNavigationBarItem())
        }
    }
}