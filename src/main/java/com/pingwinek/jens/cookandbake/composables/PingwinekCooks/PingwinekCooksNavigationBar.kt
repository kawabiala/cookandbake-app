package com.pingwinek.jens.cookandbake.composables.PingwinekCooks

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.composables.PingwinekCooksComposableHelpers

@Composable
fun PingwinekCooksNavigationBar(
    selectedItem: Int = 0,
    enabled: Boolean = true,
    navigationBarColor: Color = NavigationBarDefaults.containerColor,
    navigationBarItemColors: NavigationBarItemColors = NavigationBarItemDefaults.colors(),
    menuItems: List<PingwinekCooksComposableHelpers.OptionItem>,
    onSelectedItemChange: (Int) -> Unit
) {
    NavigationBar(
        containerColor = navigationBarColor
    ) {
        menuItems.forEachIndexed { index, item ->
            NavigationBarItem(
                colors = navigationBarItemColors,
                icon =  { Icon(item.icon, null) } ,
                label = { Text(stringResource(id = item.labelResourceId)) },
                selected = selectedItem == index && enabled,
                enabled = enabled,
                onClick = {
                    onSelectedItemChange(index)
                    item.onClick()
                }
            )
        }
    }
}
