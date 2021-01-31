package com.pingwinek.jens.cookandbake.models

import org.json.JSONException
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class RecipeRemoteTest {
/*
    @Mock
    private var jsonObject = JSONObject()

    @Test
    fun getUpdated_test() {
        val date1 = Date().time
        Thread.sleep(1)
        val date2 = Date().time

        val recipe1 = RecipeRemote.fromLocal(
            RecipeLocal(1, 2, "Recipe Local 1", null, null, date1)
        )
        val recipe2 = recipe1.getUpdated(
            RecipeRemote.fromLocal(
                RecipeLocal(3, 4, "Recipe Local 2", null, null, date2)
            )
        )

        assert(recipe1.lastModified == date1)

        assert(recipe2.lastModified == date2)
        assert(recipe2.id == 2)
        assert(recipe2.title == "Recipe Local 2")
    }

    @Test
    fun parse1_test() {
        val lastModified = Date().time

        `when`(jsonObject.getInt("id"))
            .thenReturn(1)
        `when`(jsonObject.optString("title", "RecipeRemote"))
            .thenReturn("Recipe Remote 1")
        `when`(jsonObject.getString("description"))
            .thenReturn("Recipe Description 1")
        `when`(jsonObject.isNull("description"))
            .thenReturn(false)
        `when`(jsonObject.getLong("last_modified"))
            .thenReturn(lastModified)

        val recipe = RecipeRemote.parse(jsonObject)
        assert(recipe.id == 1)
        assert(recipe.title == "Recipe Remote 1")
        assert(recipe.description == "Recipe Description 1")
        assert(recipe.lastModified == lastModified)

        val recipeMap = recipe.asMap()
        assert(recipeMap["id"] == "1")
        assert(recipeMap["title"] == "Recipe Remote 1")
        assert(recipeMap["description"] == "Recipe Description 1")
        assert(recipeMap["last_modified"] == lastModified.toString())
    }

    @Test
    fun parse2_test() {
        `when`(jsonObject.getInt("id"))
            .thenThrow(JSONException(""))
        `when`(jsonObject.optString("title", "RecipeRemote"))
            .thenReturn("")
        `when`(jsonObject.isNull("description"))
            .thenReturn(true)

        val recipe = RecipeRemote.parse(jsonObject)
        assert(recipe.id == 0)
        assert(recipe.title == "")
        assert(recipe.description == null)
        assert(recipe.lastModified == 0L)

        val recipeMap = recipe.asMap()
        assert(recipeMap["id"] == "0")
        assert(recipeMap["title"] == "")
        assert(recipeMap["description"] == "")
        assert(recipeMap["last_modified"] == 0.toString())
    }

    @Test
    fun fromLocal1_test() {
        val recipe = RecipeRemote.fromLocal(
            RecipeLocal(1, 2, "Recipe Local 1", null, null)
        )
        assert(recipe.id == 2)
    }

    @Test
    fun fromLocal2_test() {
        val recipe = RecipeRemote.fromLocal(
            RecipeLocal(1, null, "Recipe Local 1", null, null)
        )
        assert(recipe.id == 0)
    }

    @Test
    fun newFromLocal_test() {
        val recipe = RecipeRemote.newFromLocal(
            RecipeLocal(1, 2, "Recipe Local 1", null, null)
        )
        assert(recipe.id == 0)
    }

 */
}