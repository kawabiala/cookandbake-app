package com.pingwinek.jens.cookandbake.lib

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pingwinek.jens.cookandbake.theming.DarkColors
import com.pingwinek.jens.cookandbake.theming.LightColors
import com.pingwinek.jens.cookandbake.theming.Spacing
import com.pingwinek.jens.cookandbake.theming.Typography
import java.util.LinkedList

val MaterialTheme.spacing: Spacing
    get() = Spacing()

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
                typography = Typography,
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
            dropDownItemColors: MenuItemColors = MenuDefaults.itemColors(),
            dropDownMenuColor: Color = MaterialTheme.colorScheme.surfaceContainer,
            topBarColors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
            topBarIconButtonColors: IconButtonColors = IconButtonDefaults.filledIconButtonColors()
        ) {
            var expanded by remember {
                mutableStateOf(false)
            }
            TopAppBar(
                colors = topBarColors,
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
                                menuItemColors = dropDownItemColors,
                                menuColor = dropDownMenuColor,
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
            menuItemColors: MenuItemColors = MenuDefaults.itemColors(),
            menuColor: Color = MaterialTheme.colorScheme.surfaceContainer,
            options: List<OptionItem>,
            onSelected: () -> Unit
        ) {
            DropdownMenu(
                expanded = expanded,
                modifier = Modifier.background(
                    color = menuColor
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
            navigationBarColor: Color = NavigationBarDefaults.containerColor,
            navigationBarItemColors: NavigationBarItemColors = NavigationBarItemDefaults.colors(),
            menuItems: List<OptionItem>
        ) {
            var selectedMenuItem by remember {
                mutableIntStateOf(selectedItem)
            }
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
        fun PasswordField(
            password: String,
            label: String? = null,
            onValueChange: (String) -> Unit
        ) {
            var passwordHidden: Boolean by remember { mutableStateOf(true) }
            TextField(
                value = password,
                textStyle = MaterialTheme.typography.bodyMedium,
                label = {
                                 if (!label.isNullOrEmpty()) {
                                     Text(text = label)
                                    }
                                 },
                onValueChange = onValueChange,
                visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val icon = if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordHidden) "show password" else "hide password"
                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )
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
        fun LabelledSwitch(
            checked : Boolean = false,
            label : String,
            labelTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
            onCheckedChange : (checked: Boolean) -> Unit = {}
        ) {
            var checkedLocal by remember { mutableStateOf(checked) }
            Row(
                modifier = Modifier
                    .toggleable(
                        value = checkedLocal,
                        onValueChange = {
                            checkedLocal = it
                            onCheckedChange(it)
                        }
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(0.6F),
                    text = label,
                    style = labelTextStyle,
                )
                Spacer(
                    modifier = Modifier.weight(0.1F)
                )
                Switch(
                    checked = checkedLocal,
                    onCheckedChange = {
                        checkedLocal = it
                        onCheckedChange(it)
                    })
            }
        }

        @Composable
        fun Expandable(
            headerText: String,
            headerTextStyle: TextStyle = MaterialTheme.typography.headlineMedium,
            headerTextColor: Color = MaterialTheme.colorScheme.onSurface,
            contentTextStyle: TextStyle = MaterialTheme.typography.bodyMedium,
            boxColor: Color = MaterialTheme.colorScheme.surfaceContainer,
            padding: Dp = Dp(0F),
            content: @Composable (TextStyle) -> Unit
        ) {
            var isOpen by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .background(color = boxColor)
                    .padding(padding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isOpen = !isOpen },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = headerText,
                        style = headerTextStyle,
                        color = headerTextColor,
                    )
                    if (isOpen) {
                        Icon(
                            Icons.Filled.ArrowDropUp,
                            ""
                        )
                    } else {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            ""
                        )
                    }
                }

                if (isOpen) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        content(contentTextStyle)
                    }
                }
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

        @Composable
        fun SpacerSmall() {
            Spacer(modifier = Modifier.padding(MaterialTheme.spacing.spacerSmall))
        }

        @Composable
        fun SpacerMedium() {
            Spacer(modifier = Modifier.padding(MaterialTheme.spacing.spacerMedium))
        }

        @Composable
        fun SpacerLarge() {
            Spacer(modifier = Modifier.padding(MaterialTheme.spacing.spacerLarge))
        }
    }
}