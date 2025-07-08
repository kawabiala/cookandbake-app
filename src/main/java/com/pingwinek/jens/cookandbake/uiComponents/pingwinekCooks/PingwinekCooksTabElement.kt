package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers

@Composable
fun PingwinekCooksTabElement(
    fillMaxHeight: Boolean = true,
    paddingLeft: Dp = 0.dp,
    paddingRight: Dp = 0.dp,
    backgroundColor: Color = Color.Unspecified,
    selectedItem: Int,
    onSelectedItemChange: (Int) -> Unit,
    tabItems: List<PingwinekCooksComposableHelpers.PingwinekCooksTabItem>
) {
    Column(
        modifier = if(fillMaxHeight) {
            Modifier.fillMaxHeight()
        } else {
            Modifier
        }
            .background(color = backgroundColor)
    ) {
        Surface(
            color = Color.Transparent,
        ) {
            PingwinekCooksTabRow(
                selectedItem = selectedItem,
                containerColor = Color.Transparent,
                menuItems = mutableListOf<PingwinekCooksComposableHelpers.OptionItem>().apply{
                    tabItems.forEachIndexed { index, tabItem ->
                        add(
                            PingwinekCooksComposableHelpers.OptionItem(
                            tabItem.tabNameId,
                            tabItem.tabIcon,
                            {onSelectedItemChange(index)}
                        ))
                    }
                }
            )
        }

        if (0 <= selectedItem && selectedItem < tabItems.size) {
            Column(
                modifier = Modifier
                    .padding(PaddingValues(
                        paddingLeft,
                        0.dp,
                        paddingRight,
                        0.dp))
            ) {
                tabItems[selectedItem].content()
            }
        } else {
            throw IndexOutOfBoundsException()
        }
    }
}
