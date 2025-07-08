package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooksComposableHelpers
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.ListPane
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksTabElement
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.VerticalScrollContainer
import java.util.LinkedList

enum class ListVariant(val nameID: Int) {
    A2ZList(R.string.a2z),
    ByLabel(R.string.by_category)
}

@Composable
fun ScaffoldContent(
    paddingValues: PaddingValues,
    recipes: LinkedList<Recipe>?,
    recipesByLabel: LinkedList<Pair<String, LinkedList<Recipe>>>?,
    loggedIn: Boolean,
    verified: Boolean,
    onOpenRecipe: (String) -> Unit,
    onShowSignIn: () -> Unit,
    onCheckDataProtection: () -> Unit
) {
    val tabItems = listOf(
        PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
            tabNameId = ListVariant.A2ZList.nameID,
            tabIcon = Icons.Filled.SortByAlpha,
            content = { A2ZList(recipes, onOpenRecipe) }
        ),
        PingwinekCooksComposableHelpers.PingwinekCooksTabItem(
            tabNameId = ListVariant.ByLabel.nameID,
            tabIcon = Icons.Filled.Category,
            content = { ByCategoryList(recipesByLabel, onOpenRecipe) }
        )
    )

    var listVariant: Int by remember {
        mutableIntStateOf(ListVariant.ByLabel.ordinal) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(PaddingValues(0.dp, paddingValues.calculateTopPadding(), 0.dp, paddingValues.calculateBottomPadding()))
    ) {
        SpacerSmall()

        if (verified) {

            PingwinekCooksTabElement(
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                paddingLeft = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                paddingRight = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                selectedItem = listVariant,
                onSelectedItemChange = { item -> listVariant=item },
                tabItems = tabItems
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
    VerticalScrollContainer {
        recipes?.forEach { recipe ->
            key(recipe.id) {
                RecipeComponent(recipe, onOpenRecipe)
            }
        }
    }
}

@Composable
fun ByCategoryList(
    recipesByLabel: LinkedList<Pair<String, LinkedList<Recipe>>>?,
    onOpenRecipe: (String) -> Unit
    ) {
    SpacerSmall()

    VerticalScrollContainer {
        recipesByLabel?.forEach { labelWithRecipes ->
            val labelText = labelWithRecipes.first
            val recipes = labelWithRecipes.second

            Text(
                text = labelText,
                style = MaterialTheme.typography.headlineMedium
            )

            recipes.forEach { recipe ->
                key(recipe.id) {
                    RecipeComponent(recipe, onOpenRecipe)
                }
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
    ) {
        Recipe(
            paddingValues = PaddingValues(0.dp),
            recipe = recipe,
            onClick = { recipeId ->
                onOpenRecipe(recipeId)
            }
        )
    }
}