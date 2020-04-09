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
class IngredientRemoteTest {

    @Mock
    private var jsonObject = JSONObject()

    @Test
    fun getUpdated_test() {
        val date1 = Date().time
        Thread.sleep(1)
        val date2 = Date().time

        val ingredient1 = IngredientRemote.fromLocal(
            IngredientLocal(1, 2, 3,null, null, "Ingredient Local 1", date1),
            4
        )
        val ingredient2 = ingredient1.getUpdated(
            IngredientRemote.fromLocal(
                IngredientLocal(5, 6, 7,null, null, "Ingredient Local 2", date2),
                8
            )
        )

        assert(ingredient1.lastModified == date1)
        assert(ingredient1.recipeId == 4)

        assert(ingredient2.lastModified == date2)
        assert(ingredient2.id == 2)
        assert(ingredient2.recipeId == 4)
        assert(ingredient2.name == "Ingredient Local 2")
    }

    @Test
    fun parse1_test() {
        val lastModified = Date().time

        `when`(jsonObject.getInt("id"))
            .thenReturn(1)
        `when`(jsonObject.optString("name", "IngredientRemote"))
            .thenReturn("Ingredient Remote 1")
        `when`(jsonObject.getString("unity"))
            .thenReturn("Ingredient Unity 1")
        `when`(jsonObject.isNull("unity"))
            .thenReturn(false)
        `when`(jsonObject.getLong("last_modified"))
            .thenReturn(lastModified)

        val ingredient = IngredientRemote.parse(jsonObject)
        assert(ingredient.id == 1)
        assert(ingredient.name == "Ingredient Remote 1")
        assert(ingredient.unity == "Ingredient Unity 1")
        assert(ingredient.lastModified == lastModified)

        val ingredientMap = ingredient.asMap()
        assert(ingredientMap["id"] == "1")
        assert(ingredientMap["name"] == "Ingredient Remote 1")
        assert(ingredientMap["unity"] == "Ingredient Unity 1")
        assert(ingredientMap["last_modified"] == lastModified.toString())
    }

    @Test
    fun parse2_test() {
        `when`(jsonObject.getInt("id"))
            .thenThrow(JSONException(""))
        `when`(jsonObject.optString("name", "IngredientRemote"))
            .thenReturn("")
        `when`(jsonObject.isNull("unity"))
            .thenReturn(true)

        val ingredient = IngredientRemote.parse(jsonObject)
        assert(ingredient.id == 0)
        assert(ingredient.name == "")
        assert(ingredient.unity == null)
        assert(ingredient.lastModified == 0L)

        val ingredientMap = ingredient.asMap()
        assert(ingredientMap["id"] == "0")
        assert(ingredientMap["name"] == "")
        assert(ingredientMap["unity"] == "")
        assert(ingredientMap["last_modified"] == 0.toString())
    }

    @Test
    fun fromLocal1_test() {
        val ingredient = IngredientRemote.fromLocal(
            IngredientLocal(1, 2, 3, null, null, "Ingredient Local 1"),
            4
        )
        assert(ingredient.id == 2)
        assert(ingredient.recipeId == 4)
    }

    @Test
    fun fromLocal2_test() {
        val ingredient = IngredientRemote.fromLocal(
            IngredientLocal(1, null, 3, null, null, "Ingredient Local 1"),
            4
        )
        assert(ingredient.id == 0)
        assert(ingredient.recipeId == 4)
    }

    @Test
    fun newFromLocal_test() {
        val ingredient = IngredientRemote.newFromLocal(
            IngredientLocal(1, 2, 3, null, null, "Ingredient Local 1"),
            4
        )
        assert(ingredient.id == 0)
        assert(ingredient.recipeId == 4)
    }
}