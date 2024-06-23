package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall
import java.util.LinkedList

@Composable
fun ScaffoldContent(
    paddingValues: PaddingValues,
    recipes: LinkedList<Recipe>?,
    loggedIn: Boolean,
    verified: Boolean,
    onOpenRecipe: (String) -> Unit,
    onShowSignIn: () -> Unit,
    onCheckDataProtection: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(scrollState)
    ) {
        SpacerSmall()

        if (verified) {
            recipes?.forEachIndexed { index, recipe ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 5.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                }
                key(recipe.id) {
                    Recipe(recipe = recipe) { recipeId ->
                        onOpenRecipe(recipeId)
                    }
                }
            }
        } else if (loggedIn) {
            NotVerifiedView(
                onLogIn = onShowSignIn
            )
        } else {
            NoProfileView(
                onLogIn = onShowSignIn,
                onCheckDataProtection = onCheckDataProtection
            )
        }
    }
}
