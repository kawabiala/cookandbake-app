package com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooks

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers

@Composable
fun PingwinekCooksTabRow(
    selectedItem: Int = 0,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    menuItems: List<PingwinekCooksComposableHelpers.OptionItem>
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
