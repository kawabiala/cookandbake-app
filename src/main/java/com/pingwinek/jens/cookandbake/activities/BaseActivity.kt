package com.pingwinek.jens.cookandbake.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.composables.PingwinekCooks.PingwinekCooksNavigationBar
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables.Companion.PingwinekCooksTopAppBar
import com.pingwinek.jens.cookandbake.lib.spacing
import java.util.LinkedList

/**
 */
abstract class BaseActivity : AppCompatActivity() {

    enum class Navigation(val label: Int, val icon: ImageVector) {
        RECIPE(R.string.recipes, Icons.Outlined.RestaurantMenu),
        LOGIN(R.string.profile, Icons.Outlined.Person)
    }

    private var title = ""
    private var showDrowDown = false
    private var optionItemLeft: PingwinekCooksComposables.OptionItem? = null
    private var optionItemMid: PingwinekCooksComposables.OptionItem? = null
    private var optionItemRight: PingwinekCooksComposables.OptionItem? = null

    private var dropDownOptions = mutableListOf<PingwinekCooksComposables.OptionItem>()

    private var selectedNavigationBarItem = 0
    private var selectedNavigationBarItemAsLiveData = MutableLiveData(selectedNavigationBarItem)
    private var navigationBarEnabled = true
    private var navigationBarItems = listOf<PingwinekCooksComposables.OptionItem>()

    private var FloatingActionButtonAsLiveData: MutableLiveData<@Composable () -> Unit> = MutableLiveData{}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PingwinekCooksAppTheme {
                BaseScaffold()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        selectedNavigationBarItemAsLiveData.value = selectedNavigationBarItem
    }

    @Composable
    fun BasePreview() {
        PingwinekCooksAppTheme {
            BaseScaffold()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BaseScaffold() {

        val selectedNaviItem by selectedNavigationBarItemAsLiveData.observeAsState()

        val onSelectedNavigationItemChange : (Int) -> Unit = { item ->
            Log.i(this::class.java.name, "selectedItem change: $item")
            selectedNavigationBarItemAsLiveData.value = item
        }

        val FloatingActionButton by FloatingActionButtonAsLiveData.observeAsState()

        val topAppBarColors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
        val topBarIconButtonColors = IconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )

        val dropDownMenuColor = MaterialTheme.colorScheme.surfaceContainerLow
        val dropDownItemColors = MenuItemColors(
            textColor = MaterialTheme.colorScheme.onSurface,
            leadingIconColor = MaterialTheme.colorScheme.onSurface,
            trailingIconColor = MaterialTheme.colorScheme.onSurface,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
        )

        val navigationBarColor = MaterialTheme.colorScheme.surfaceContainer
        val navigationBarItemColors = NavigationBarItemColors(
            selectedIconColor = MaterialTheme.colorScheme.onSurface,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Scaffold(
            contentWindowInsets = WindowInsets(
                MaterialTheme.spacing.mainWindowPadding.value.toInt(),
                MaterialTheme.spacing.mainWindowPadding.value.toInt(),
                MaterialTheme.spacing.mainWindowPadding.value.toInt(),
                MaterialTheme.spacing.mainWindowPadding.value.toInt(),
            ),
            topBar = {
                PingwinekCooksTopAppBar(
                    title = title,
                    showDrowDown,
                    dropDownOptions = dropDownOptions,
                    optionItemLeft = optionItemLeft,
                    optionItemMid = optionItemMid,
                    optionItemRight = optionItemRight,
                    dropDownItemColors = dropDownItemColors,
                    dropDownMenuColor = dropDownMenuColor,
                    topBarColors = topAppBarColors,
                    topBarIconButtonColors = topBarIconButtonColors,
                )
            },
            bottomBar = {
                PingwinekCooksNavigationBar(
                    selectedItem = selectedNaviItem ?: 0,
                    enabled = navigationBarEnabled,
                    navigationBarColor = navigationBarColor,
                    navigationBarItemColors = navigationBarItemColors,
                    menuItems = navigationBarItems,
                    onSelectedItemChange = onSelectedNavigationItemChange
                )
            },
            floatingActionButton = { FloatingActionButton?.let { it() } }
        ) { paddingValues ->
            ScaffoldContent(paddingValues)
        }
    }

    @Composable
    protected abstract fun ScaffoldContent(paddingValues: PaddingValues)

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
                navigationEntry.label,
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

    protected fun configureFloatingActionButton(
        icon: ImageVector,
        label: String,
        onClick: () -> Unit = {}
    )  {
        FloatingActionButtonAsLiveData.value = {
            FloatingActionButton(onClick = onClick) {
                Icon(icon, label)
            }
        }
    }
}