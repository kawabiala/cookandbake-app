package com.pingwinek.jens.cookandbake.composables.PingwinekCooks

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.graphics.vector.ImageVector
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import com.pingwinek.jens.cookandbake.lib.spacing
import java.util.LinkedList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PingwinekCooksScaffold(
    title: String,
    showDropDown: Boolean = false,
    dropDownOptions: List<PingwinekCooksComposables.OptionItem> = LinkedList(),
    optionItemLeft: PingwinekCooksComposables.OptionItem? = null,
    optionItemMid: PingwinekCooksComposables.OptionItem? = null,
    optionItemRight: PingwinekCooksComposables.OptionItem? = null,
    selectedNavigationBarItem: Int = 0,
    navigationBarVisible: Boolean = true,
    navigationBarEnabled: Boolean = false,
    navigationBarItems: List<PingwinekCooksComposables.OptionItem> = LinkedList(),
    onSelectedNavigationItemChange: (Int) -> Unit = {},
    showFab: Boolean = false,
    fabIcon: ImageVector? = null,
    fabIconLabel: String = "",
    onFabClicked: () -> Unit = {},
    scaffoldContent: @Composable (PaddingValues) -> Unit = {}
) {
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
                showDropDown = showDropDown,
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
            if (navigationBarVisible) {
                PingwinekCooksNavigationBar(
                    selectedItem = selectedNavigationBarItem,
                    enabled = navigationBarEnabled,
                    navigationBarColor = navigationBarColor,
                    navigationBarItemColors = navigationBarItemColors,
                    menuItems = navigationBarItems,
                    onSelectedItemChange = onSelectedNavigationItemChange
                )
            }
        },
        floatingActionButton = {
            if (showFab && fabIcon != null) {
                FloatingActionButton(onClick = onFabClicked) {
                    Icon(imageVector = fabIcon, contentDescription = fabIconLabel)
                }
            }
        }
    ) { paddingValues ->
        scaffoldContent(paddingValues)
    }
}
