package com.pingwinek.jens.cookandbake.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.theming.Spacing

val MaterialTheme.spacing: Spacing
    get() = Spacing()

class PingwinekCooksComposableHelpers {

    data class OptionItem(
        val labelResourceId: Int,
        var icon: ImageVector,
        var onClick: () -> Unit
    )

    data class PingwinekCooksTabItem(
        val tabNameId: Int,
        val tabIcon: ImageVector,
        val content: @Composable () -> Unit
    )

    enum class Navigation(val label: Int, val icon: ImageVector) {
        RECIPE(R.string.recipes, Icons.Outlined.RestaurantMenu),
        LOGIN(R.string.profile, Icons.Outlined.Person)
    }
}