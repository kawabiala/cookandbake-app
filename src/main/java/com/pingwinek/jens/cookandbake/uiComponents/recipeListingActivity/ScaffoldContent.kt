package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.ListPane
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall
import com.pingwinek.jens.cookandbake.uiComponents.spacing
import java.util.LinkedList

@Composable
fun ScaffoldContent(
    paddingValues: PaddingValues,
    recipes: LinkedList<Recipe>?,
    label: String?,
    loggedIn: Boolean,
    verified: Boolean,
    onOpenRecipe: (String) -> Unit,
    onShowSignIn: () -> Unit,
    onCheckDataProtection: () -> Unit
) {


    Column(
        modifier = Modifier
            .padding(PaddingValues(0.dp, paddingValues.calculateTopPadding(), 0.dp, paddingValues.calculateBottomPadding()))
    ) {
        SpacerSmall()

        if (verified) {

            if (! label.isNullOrBlank()) {
                Text(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.standardPadding),
                    text = label,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                SpacerSmall()
            }

            A2ZList(
                recipes = recipes,
                onOpenRecipe = onOpenRecipe
            )

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

@Composable
fun A2ZList(
    recipes: LinkedList<Recipe>?,
    onOpenRecipe: (String) -> Unit
) {
    recipes?.let { rList ->

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.spacerSmall)
        ) {
            items(
                rList.toList(),
                { recipe -> recipe.id }
            ) { recipe ->
                RecipeComponent(recipe, onOpenRecipe)
            }
        }
    }
}

@Composable
fun RecipeComponent(
    recipe: Recipe,
    onOpenRecipe: (String) -> Unit
) {
    ListPane (
        modifier = Modifier
            .clickable { onOpenRecipe(recipe.id) }
    ) {
        Recipe(
            paddingValues = PaddingValues(0.dp),
            recipe = recipe
        )
    }
}