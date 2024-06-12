package com.pingwinek.jens.cookandbake.lib

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.theming.Spacing

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