package com.pingwinek.jens.cookandbake

import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import org.junit.Test
import java.util.*

class IngredientLocalTest {

    @Test
    fun getUpdated_test() {
        val date1 = Date().time
        Thread.sleep(1)
        val date2 = Date().time
        Thread.sleep(1)

        val ingredient1 = IngredientLocal(1, 2, 3, null, null, "Ingredient Local 1", date1)
        val ingredient2 = ingredient1.getUpdated(
            IngredientLocal(4, 5, 6, null, null, "Ingredient Local 2", date2)
        )
        Thread.sleep(1)

        assert(ingredient1.lastModified == date1)

        assert(ingredient2.id == 1)
        assert(ingredient2.remoteId == 2)
        assert(ingredient2.recipeId == 3)
        assert(ingredient2.name == "Ingredient Local 2")
        assert(ingredient2.lastModified > date1)
        assert(ingredient2.lastModified > date2)
    }

    @Test
    fun newFromRemote_test() {
        val earliest = Date().time

        Thread.sleep(1)
        val ingredientRemote = IngredientRemote.fromLocal(
            IngredientLocal(1, 2, 3, null, null, "Ingredient Local 1"),
            4
        )
        Thread.sleep(1)
        val ingredientLocal = IngredientLocal.newFromRemote(ingredientRemote, 5)

        assert(ingredientRemote.id == 2)
        assert(ingredientRemote.recipeId == 4)
        assert(ingredientRemote.name == "Ingredient Local 1")
        assert(ingredientRemote.lastModified > earliest)
        assert(ingredientRemote.lastModified < Date().time)

        assert(ingredientLocal.id == 0)
        assert(ingredientLocal.remoteId == 2)
        assert(ingredientLocal.recipeId == 5)
        assert(ingredientLocal.remoteId == ingredientRemote.id)
        assert(ingredientLocal.name == "Ingredient Local 1")
        assert(ingredientLocal.lastModified == ingredientRemote.lastModified)
    }
}