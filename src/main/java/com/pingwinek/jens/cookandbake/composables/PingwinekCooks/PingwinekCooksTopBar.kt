package com.pingwinek.jens.cookandbake.composables.PingwinekCooks

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables
import java.util.LinkedList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PingwinekCooksTopAppBar(
    title: String = "",
    showDropDown: Boolean = false,
    dropDownOptions: List<PingwinekCooksComposables.OptionItem> = LinkedList<PingwinekCooksComposables.OptionItem>(),
    optionItemLeft: PingwinekCooksComposables.OptionItem? = null,
    optionItemMid: PingwinekCooksComposables.OptionItem? = null,
    optionItemRight: PingwinekCooksComposables.OptionItem? = null,
    dropDownItemColors: MenuItemColors = MenuDefaults.itemColors(),
    dropDownMenuColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    topBarColors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    topBarIconButtonColors: IconButtonColors = IconButtonDefaults.filledIconButtonColors()
) {
    var expanded by remember { mutableStateOf(false) }

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
