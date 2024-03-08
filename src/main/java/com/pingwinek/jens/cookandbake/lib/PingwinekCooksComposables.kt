package com.pingwinek.jens.cookandbake.lib

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.viewinterop.AndroidView
import com.pingwinek.jens.cookandbake.theming.DarkColors
import com.pingwinek.jens.cookandbake.theming.LightColors
import java.util.LinkedList

class PingwinekCooksComposables {

    data class OptionItem(
        val label: String,
        val icon: ImageVector,
        val onClick: () -> Unit
    )

    data class NavigationBarItem(
        val icon: ImageVector,
        val label: String = "",
        val selected: Boolean = false,
        val enabled: Boolean = true,
        val onClick: () -> Unit = {}
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
            title: String = "",
            showDropDown: Boolean = false,
            dropDownOptions: List<OptionItem> = LinkedList<OptionItem>(),
            optionItemLeft: OptionItem? = null,
            optionItemMid: OptionItem? = null,
            optionItemRight: OptionItem? = null,
        ) {
            val topAppBarColors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            val topBarIconButtonColors = IconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
            var expanded by remember {
                mutableStateOf(false)
            }
            TopAppBar(
                colors = topAppBarColors,
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
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                leadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                trailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
            DropdownMenu(
                expanded = expanded,
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.secondaryContainer
                ),
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
        fun PingwinekCooksNavigationBar(
            selectedItem: Int = 0,
            enabled: Boolean = true,
            menuItems: List<OptionItem>
        ) {
            var selectedMenuItem by remember {
                mutableIntStateOf(selectedItem)
            }
            val navigationBarColor = MaterialTheme.colorScheme.secondaryContainer
            val navigationBarItemColors = NavigationBarItemColors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
            NavigationBar(
                containerColor = navigationBarColor
            ) {
                menuItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        colors = navigationBarItemColors,
                        icon =  { Icon(item.icon, null) } ,
                        label = { Text(item.label) },
                        selected = selectedMenuItem == index && enabled,
                        enabled = enabled,
                        onClick = {
                            selectedMenuItem = index
                            item.onClick()
                        }
                    )
                }
            }
        }

        @Composable
        fun PingwinekCooksTabRow(
            selectedItem: Int = 0,
            enabled: Boolean = true,
            menuItems: List<OptionItem>
        ) {
            var selectedTab by remember {
                mutableIntStateOf(selectedItem)
            }
            TabRow(selectedTab) {
                menuItems.forEachIndexed { index, item ->
                    Tab(
                        selected = selectedTab == index,
                        enabled = enabled,
                        text = { Text(item.label) },
                        icon = { Icon(item.icon, null) },
                        onClick = {
                            selectedTab = index
                            item.onClick()
                        }
                    )
                }
            }
        }

        @Composable
        fun EditableText(
            label: String? = null,
            text: String,
            supportingText: String? = null,
            editable: Boolean = false,
            onValueChange: (text: String) -> Unit = {},
            onSupportingTextClicked: () -> Unit = {}
        ) {
            if (editable) {
                TextField(
                    value = text,
                    label = {
                        if (!label.isNullOrEmpty()) {
                            Text(label)
                        }
                    },
                    supportingText = {
                        if (!supportingText.isNullOrEmpty()) {
                            Text(
                                text = supportingText,
                                modifier = Modifier.clickable { onSupportingTextClicked() },
                            )
                        }
                    },
                    onValueChange = onValueChange,
                )
            } else {
                Text(text)
            }
        }

        @Composable
        fun LabelledCheckBox(
            checked : Boolean = false,
            label : String,
            onCheckedChange : (checked: Boolean) -> Unit = {}
        ) {
            var checkedLocal by remember { mutableStateOf(checked) }
            Row(
                Modifier.toggleable(
                    value = checked,
                    onValueChange = {
                        checkedLocal = it
                        onCheckedChange(it)
                    }
                )
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        checkedLocal = it
                        onCheckedChange(it)
                    }
                )
                Text(text = label)
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