package com.pingwinek.jens.cookandbake.lib

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.composables.PingwinekCooks.PingwinekCooksNavigationBar
import com.pingwinek.jens.cookandbake.theming.DarkColors
import com.pingwinek.jens.cookandbake.theming.LightColors
import com.pingwinek.jens.cookandbake.theming.Spacing
import com.pingwinek.jens.cookandbake.theming.Typography
import java.util.LinkedList

val MaterialTheme.spacing: Spacing
    get() = Spacing()

class PingwinekCooksComposables {

    data class OptionItem(
        val labelResourceId: Int,
        var icon: ImageVector,
        var onClick: () -> Unit
    )

    data class PingwinekCooksTabItem(val tabNameId: Int, val tabIcon: ImageVector, val content: @Composable () -> Unit)

    enum class Navigation(val label: Int, val icon: ImageVector) {
        RECIPE(R.string.recipes, Icons.Outlined.RestaurantMenu),
        LOGIN(R.string.profile, Icons.Outlined.Person)
    }

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
        fun PingwinekCooksScaffold(
            title: String,
            showDropDown: Boolean = false,
            dropDownOptions: List<OptionItem> = LinkedList(),
            optionItemLeft: OptionItem? = null,
            optionItemMid: OptionItem? = null,
            optionItemRight: OptionItem? = null,
            selectedNavigationBarItem: Int = 0,
            navigationBarVisible: Boolean = true,
            navigationBarEnabled: Boolean = false,
            navigationBarItems: List<OptionItem> = LinkedList(),
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
                            Icon(it.icon, stringResource(id = it.labelResourceId))
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
                            Icon(it.icon, stringResource(id = it.labelResourceId))
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
                                Icon(it.icon, stringResource(id = it.labelResourceId))
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
                        leadingIcon = { Icon(imageVector = it.icon, contentDescription = stringResource(id = it.labelResourceId)) },
                        text = {
                            Text(stringResource(id = it.labelResourceId))
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
        fun PingwinekCooksTabElement(
            modifier: Modifier,
            selectedItem: Int,
            onSelectedItemChange: (Int) -> Unit,
            tabItems: List<PingwinekCooksTabItem>
        ) {
            Column(
                modifier = modifier
            ) {
                Surface(
                    color = Color.Transparent,
                ) {
                    PingwinekCooksTabRow(
                        selectedItem = selectedItem,
                        containerColor = Color.Transparent,
                        menuItems = mutableListOf<OptionItem>().apply{
                            tabItems.forEachIndexed { index, tabItem ->
                                add(OptionItem(
                                    tabItem.tabNameId,
                                    tabItem.tabIcon,
                                    {onSelectedItemChange(index)}
                                ))
                            }
                        }
                    )
                }

                if (0 <= selectedItem && selectedItem < tabItems.size) {
                    tabItems[selectedItem].content()
                } else {
                    throw IndexOutOfBoundsException()
                }
            }
        }

        @Composable
        fun PingwinekCooksTabRow(
            selectedItem: Int = 0,
            enabled: Boolean = true,
            containerColor: Color = MaterialTheme.colorScheme.surface,
            contentColor: Color = MaterialTheme.colorScheme.primary,
            menuItems: List<OptionItem>
        ) {
            TabRow(
                selectedTabIndex = selectedItem,
                containerColor = containerColor,
                contentColor = contentColor
            ) {
                menuItems.forEachIndexed { index, item ->
                    Tab(
                        selected = selectedItem == index,
                        enabled = enabled,
                        text = { Text(stringResource(id = item.labelResourceId)) },
                        icon = { Icon(item.icon, null) },
                        onClick = {
                            item.onClick()
                        }
                    )
                }
            }
        }

        @Composable
        fun EditPane(
            paddingValues: PaddingValues,
            onCancel: () -> Unit,
            onSave: () -> Unit,
            content: @Composable () -> Unit
        ) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                PingwinekCooksComposables.SpacerSmall()

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .clickable { onCancel() },
                        text = stringResource(R.string.close)
                    )
                    Text(
                        modifier = Modifier
                            .clickable { onSave() },
                        text = stringResource(R.string.save)
                    )
                }

                PingwinekCooksComposables.SpacerMedium()

                content()
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