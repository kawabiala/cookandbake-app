package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.models.Recipe

@Composable
fun Recipe(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    recipe: Recipe,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    ) {
        Text(
            text = recipe.title,
            fontWeight = FontWeight.Bold
        )
        Text(recipe.description ?: "")
    }
}
