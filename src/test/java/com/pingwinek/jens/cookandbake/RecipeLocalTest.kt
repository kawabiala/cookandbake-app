package com.pingwinek.jens.cookandbake

import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import org.junit.Test
import java.util.*

class RecipeLocalTest {

    @Test
    fun getUpdated_test() {
        val date1 = Date().time
        Thread.sleep(1)
        val date2 = Date().time
        Thread.sleep(1)

        val recipe1 = RecipeLocal(1, 2, "Recipe Local 1", null, null, date1)
        val recipe2 = recipe1.getUpdated(
            RecipeLocal(3, 4, "Recipe Local 2", null, null, date2)
        )
        Thread.sleep(1)

        assert(recipe1.lastModified == date1)

        assert(recipe2.id == 1)
        assert(recipe2.remoteId == 2)
        assert(recipe2.title == "Recipe Local 2")
        assert(recipe2.lastModified > date1)
        assert(recipe2.lastModified > date2)
    }

    @Test
    fun newFromRemote_test() {
        val earliest = Date().time

        Thread.sleep(1)
        val recipeRemote = RecipeRemote.fromLocal(
            RecipeLocal(1, 2, "Recipe Local 1", null, null)
        )
        Thread.sleep(1)
        val recipeLocal = RecipeLocal.newFromRemote(recipeRemote)

        assert(recipeRemote.id == 2)
        assert(recipeRemote.title == "Recipe Local 1")
        assert(recipeRemote.lastModified > earliest)
        assert(recipeRemote.lastModified < Date().time)

        assert(recipeLocal.id == 0)
        assert(recipeLocal.remoteId == recipeRemote.id)
        assert(recipeLocal.title == "Recipe Local 1")
        assert(recipeLocal.lastModified == recipeRemote.lastModified)
    }
}