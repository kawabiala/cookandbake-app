package com.pingwinek.jens.cookandbake.theming

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun PingwinekCooksAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PingwinekCooksTopAppBar(
    title: String,
    showDropDown: Boolean,
    drawerState: DrawerState,
    options: List<OptionItem>,
    optionItem1: OptionItem?,
    optionItem2: OptionItem?,
    optionItem3: OptionItem?,
) {
    val topBarIconButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.primary
    )
    var expanded by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(title)
        },
        navigationIcon = {
            optionItem1?.let {
                IconButton(
                    onClick = {
                        it.onClick
                    },
                    colors = topBarIconButtonColors
                ) {
                    Icon(it.icon, it.label)
                }
            }
        },
        actions = {
            optionItem2?.let {
                IconButton(
                    onClick = {
                        it.onClick
                    },
                    colors = topBarIconButtonColors
                ) {
                    Icon(it.icon, it.label)
                }
            }
            if (showDropDown) {
                IconButton(
                    onClick = {
                              expanded = true
/*                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }

 */
                    },
                    colors = topBarIconButtonColors
                ) {
                    Icon(Icons.Filled.MoreVert, null)
                }
            } else {
                optionItem3?.let {
                    IconButton(
                        onClick = {
                            it.onClick
                        },
                        colors = topBarIconButtonColors
                    ) {
                        Icon(it.icon, it.label)
                    }
                }
            }
        })
    PingwinekCooksDropDown(expanded = expanded, options = options)
}

@Composable
fun PingwinekCooksDropDown(
    expanded: Boolean,
    options: List<OptionItem>
) {
    var isExpanded = expanded
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false }
    ) {
        options.forEach {
            DropdownMenuItem(
                leadingIcon = { Icon(imageVector = it.icon, contentDescription = it.label) },
                text = {
                    Text(it.label)
                },
                onClick = it.onClick
            )
        }
    }
}

@Composable
fun PingwinekCooksHamburgerMenu(
    drawerState: DrawerState,
    options: List<OptionItem>,
    scaffoldContent: @Composable() () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                options.forEach {
                    NavigationDrawerItem(
                        icon = { Icon(imageVector = it.icon, contentDescription = it.label) },
                        label = {
                            Text(it.label)
                        },
                        selected = false,
                        onClick = it.onClick
                    )
                }
            }
        }
    ) {
        scaffoldContent()
    }
}

data class OptionItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

