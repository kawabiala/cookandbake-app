package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.clickable
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
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(recipe.id) }
            .padding(paddingValues)
    ) {
        Text(
            text = recipe.title,
            fontWeight = FontWeight.Bold
        )
        Text(recipe.description ?: "")
    }
}
