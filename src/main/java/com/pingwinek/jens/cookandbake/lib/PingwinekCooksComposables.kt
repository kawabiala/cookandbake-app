package com.pingwinek.jens.cookandbake.lib

import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.viewinterop.AndroidView
import com.pingwinek.jens.cookandbake.theming.DarkColors
import com.pingwinek.jens.cookandbake.theming.LightColors

class PingwinekCooksComposables {

    data class OptionItem(
        val label: String,
        val icon: ImageVector,
        val onClick: () -> Unit
    )

    companion object {
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
            dropDownOptions: List<OptionItem>,
            optionItemLeft: OptionItem?,
            optionItemMid: OptionItem?,
            optionItemRight: OptionItem?,
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
                    optionItemLeft?.let {
                        IconButton(
                            onClick = {
                                it.onClick()
                            },
                            colors = topBarIconButtonColors
                        ) {
                            Icon(it.icon, it.label)
                        }
                    }
                },
                actions = {
                    optionItemMid?.let {
                        IconButton(
                            onClick = {
                                it.onClick()
                            },
                            colors = topBarIconButtonColors
                        ) {
                            Icon(it.icon, it.label)
                        }
                    }
                    if (showDropDown) {
                        IconButton(
                            onClick = {
                                expanded = !expanded
                            },
                            colors = topBarIconButtonColors
                        ) {
                            Icon(Icons.Filled.MoreVert, null)
                            PingwinekCooksDropDown(
                                expanded = expanded,
                                options = dropDownOptions
                            ) { expanded = false }
                        }
                    } else {
                        optionItemRight?.let {
                            IconButton(
                                onClick = {
                                    it.onClick()
                                },
                                colors = topBarIconButtonColors
                            ) {
                                Icon(it.icon, it.label)
                            }
                        }
                    }
                })
        }

        @Composable
        fun PingwinekCooksDropDown(
            expanded: Boolean,
            options: List<OptionItem>,
            onSelected: () -> Unit
        ) {
            val menuItemColors = MenuItemColors(
                textColor = MaterialTheme.colorScheme.secondary,
                leadingIconColor = MaterialTheme.colorScheme.secondary,
                trailingIconColor = MaterialTheme.colorScheme.secondary,
                disabledLeadingIconColor = MaterialTheme.colorScheme.secondary,
                disabledTextColor = MaterialTheme.colorScheme.secondary,
                disabledTrailingIconColor = MaterialTheme.colorScheme.secondary
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onSelected() }
            ) {
                options.forEach {
                    DropdownMenuItem(
                        leadingIcon = { Icon(imageVector = it.icon, contentDescription = it.label) },
                        text = {
                            Text(it.label)
                        },
                        onClick = {
                            it.onClick()
                            onSelected()
                        },
                        colors = menuItemColors
                    )
                }
            }
        }

        @Composable
        fun PingwinekCooksNavigationBar() {
            NavigationBar() {
                NavigationBarItem(selected = false, onClick = { /*TODO*/ }, icon = { /*TODO*/ })
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

        @Composable
        fun WebView(url: String) {
            val scrollState = rememberScrollState()
            AndroidView(factory = {
                android.webkit.WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }, update = {
                it.loadUrl(url)
            }, modifier = Modifier.verticalScroll(scrollState))
        }
    }
}