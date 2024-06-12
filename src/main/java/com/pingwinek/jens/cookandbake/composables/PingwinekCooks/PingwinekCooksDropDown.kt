package com.pingwinek.jens.cookandbake.composables.PingwinekCooks

import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables

@Composable
fun PingwinekCooksDropDown(
    expanded: Boolean,
    menuItemColors: MenuItemColors = MenuDefaults.itemColors(),
    menuColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    options: List<PingwinekCooksComposables.OptionItem>,
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
