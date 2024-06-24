package com.pingwinek.jens.cookandbake.uiComponents.signInActivity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksTabRow
import java.util.LinkedList

@Composable
fun SignInTabRow(
    highlightLeft: Boolean,
    toggleItem: () -> Unit
) {
    PingwinekCooksTabRow(
        selectedItem = if (highlightLeft) { 0 } else { 1 },
        menuItems = LinkedList<PingwinekCooksComposableHelpers.OptionItem>().apply {
            add(
                PingwinekCooksComposableHelpers.OptionItem(
                    R.string.register, Icons.Filled.Person, toggleItem
                ))
            add(
                PingwinekCooksComposableHelpers.OptionItem(
                    R.string.login, Icons.Filled.Person, toggleItem
                ))
        }
    )
}