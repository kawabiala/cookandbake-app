package com.pingwinek.jens.cookandbake.composables.PingwinekCooks

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables

@Composable
fun PingwinekCooksTabElement(
    modifier: Modifier,
    selectedItem: Int,
    onSelectedItemChange: (Int) -> Unit,
    tabItems: List<PingwinekCooksComposables.PingwinekCooksTabItem>
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
                menuItems = mutableListOf<PingwinekCooksComposables.OptionItem>().apply{
                    tabItems.forEachIndexed { index, tabItem ->
                        add(PingwinekCooksComposables.OptionItem(
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
