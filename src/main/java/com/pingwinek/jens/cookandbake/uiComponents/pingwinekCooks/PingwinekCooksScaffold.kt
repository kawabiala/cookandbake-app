package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.spacing
import java.util.LinkedList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PingwinekCooksScaffold(
    title: String,
    showDropDown: Boolean = false,
    dropDownOptions: List<PingwinekCooksComposableHelpers.OptionItem> = LinkedList(),
    optionItemLeft: PingwinekCooksComposableHelpers.OptionItem? = null,
    optionItemMid: PingwinekCooksComposableHelpers.OptionItem? = null,
    optionItemRight: PingwinekCooksComposableHelpers.OptionItem? = null,
    selectedNavigationBarItem: Int = 0,
    navigationBarVisible: Boolean = true,
    navigationBarEnabled: Boolean = false,
    navigationBarItems: List<PingwinekCooksComposableHelpers.OptionItem> = LinkedList(),
    onSelectedNavigationItemChange: (Int) -> Unit = {},
    showFab: Boolean = false,
    fabIcon: ImageVector? = null,
    fabIconLabel: String = "",
    fabContainerColor: Color = FloatingActionButtonDefaults.containerColor,
    fabIconColor: Color = IconButtonDefaults.iconButtonColors().contentColor,
    onFabClicked: () -> Unit = {},
    snackbarMessage: String? = null,
    onHasShownSnackbar: () -> Unit = {},
    scaffoldContent: @Composable (PaddingValues) -> Unit = {}
) {
    val topAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    /**
     * redefining selectedIconColor and selectedIndicatorColor
     * all other colors are default
     */
    val navigationBarItemColors = NavigationBarItemColors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedTextColor = NavigationBarItemDefaults.colors().selectedTextColor,
        selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
        unselectedIconColor = NavigationBarItemDefaults.colors().unselectedIconColor,
        unselectedTextColor = NavigationBarItemDefaults.colors().unselectedTextColor,
        disabledIconColor = NavigationBarItemDefaults.colors().disabledIconColor,
        disabledTextColor = NavigationBarItemDefaults.colors().disabledTextColor
    )

    Scaffold(
        contentWindowInsets = WindowInsets(
            MaterialTheme.spacing.mainWindowPadding.value.toInt(),
            MaterialTheme.spacing.mainWindowPadding.value.toInt(),
            MaterialTheme.spacing.mainWindowPadding.value.toInt(),
            MaterialTheme.spacing.mainWindowPadding.value.toInt(),
        ),
        snackbarHost = {
            Snackbar(
                snackbarMessage = snackbarMessage,
                onHasShown = onHasShownSnackbar
            )
       },
        topBar = {
            PingwinekCooksTopAppBar(
                title = title,
                showDropDown = showDropDown,
                dropDownOptions = dropDownOptions,
                optionItemLeft = optionItemLeft,
                optionItemMid = optionItemMid,
                optionItemRight = optionItemRight,
                topBarColors = topAppBarColors,
            )
        },
        bottomBar = {
            if (navigationBarVisible) {
                PingwinekCooksNavigationBar(
                    selectedItem = selectedNavigationBarItem,
                    enabled = navigationBarEnabled,
                    navigationBarItemColors = navigationBarItemColors,
                    menuItems = navigationBarItems,
                    onSelectedItemChange = onSelectedNavigationItemChange
                )
            }
        },
        floatingActionButton = {
            if (showFab && fabIcon != null) {
                FloatingActionButton(
                    containerColor = fabContainerColor,
                    contentColor = fabIconColor,
                    onClick = onFabClicked
                ) {
                    Icon(imageVector = fabIcon, contentDescription = fabIconLabel)
                }
            }
        }
    ) { paddingValues ->
        scaffoldContent(paddingValues)
    }
}