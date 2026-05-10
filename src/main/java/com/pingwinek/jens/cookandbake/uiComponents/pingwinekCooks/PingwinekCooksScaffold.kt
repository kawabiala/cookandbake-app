package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
    fabContainerSecondaryColor: Color = FloatingActionButtonDefaults.containerColor,
    fabIconSecondaryColor: Color = IconButtonDefaults.iconButtonColors().contentColor,
    fabMenuItems: List<PingwinekCooksComposableHelpers.OptionItem> = listOf(),
    onFabClicked: () -> Unit = {},
    snackbarMessage: String? = null,
    onHasShownSnackbar: () -> Unit = {},
    scaffoldContent: @Composable (PaddingValues) -> Unit = {}
) {
    val bottomPadding = if (navigationBarVisible) {
        PaddingValues()
    } else {
        WindowInsets.navigationBars.only(WindowInsetsSides.Bottom).asPaddingValues()
    }

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
        modifier = Modifier
            .padding(bottomPadding),
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
                FloatingActionButtonMenu(
                    visible = !fabMenuItems.isEmpty(),
                    fabContainerColor = fabContainerColor,
                    fabIconColor = fabIconColor,
                    fabIconLabel = fabIconLabel,
                    fabIcon = fabIcon,
                    fabContainerSecondaryColor = fabContainerSecondaryColor,
                    fabIconSecondaryColor = fabIconSecondaryColor,
                    fabMenuItems = fabMenuItems,
                    onFabClicked = onFabClicked
                )
            }
        }
    ) { paddingValues ->
        scaffoldContent(paddingValues)
    }
}

@Composable
fun FloatingActionButtonMenu(
    visible: Boolean = false,
    fabContainerColor: Color,
    fabIconColor: Color,
    fabIcon: ImageVector,
    fabIconLabel: String,
    fabContainerSecondaryColor: Color,
    fabIconSecondaryColor: Color,
    fabMenuItems: List<PingwinekCooksComposableHelpers.OptionItem>,
    onFabClicked: () -> Unit
) {
    var expanded by remember(visible) { mutableStateOf(false) }

    val fabIcon by remember(expanded) {
        derivedStateOf{ if (!expanded) fabIcon else Icons.Filled.Close }
    }

    val localFabIconColor by remember(expanded) {
        derivedStateOf{ if (expanded) fabIconSecondaryColor else fabIconColor }
    }

    val localFabContainerColor by remember(expanded) {
        derivedStateOf{ if (expanded) fabContainerSecondaryColor else fabContainerColor }
    }

    val onFabClicked: () -> Unit = {
        expanded = (visible && !expanded)
        onFabClicked()
    }

    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.End,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spacerSmall)
    ) {

        AnimatedVisibility(
            visible = expanded
        ) {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.End,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spacerSmall)
            ) {
                fabMenuItems.forEach { menuItem ->
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = fabContainerColor,
                            contentColor = fabIconColor
                        ),
                        onClick = {
                            menuItem.onClick()
                            expanded = !expanded
                        }
                    ) {
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spacerSmall)
                        ) {
                            Icon(
                                imageVector = menuItem.icon,
                                contentDescription = stringResource(menuItem.labelResourceId),
                            )
                            Text(
                                text = stringResource(menuItem.labelResourceId),
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            containerColor = localFabContainerColor,
            contentColor = localFabIconColor,
            onClick = onFabClicked
        ) {
            Icon(imageVector = fabIcon, contentDescription = fabIconLabel)
        }
    }
}