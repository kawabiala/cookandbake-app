package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pingwinek.jens.cookandbake.models.Recipe

@Composable
fun Recipe(
    recipe: Recipe,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(recipe.id) }
    ) {
        Text(recipe.title)
        Text(recipe.description ?: "")
    }
}
