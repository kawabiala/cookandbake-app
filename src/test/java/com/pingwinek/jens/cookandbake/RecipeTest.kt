package com.pingwinek.jens.cookandbake

import org.json.JSONException
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RecipeTest {

    private val keyId = "id"
    private val valueId1 = 1
    private val valueId2 = null
    private val keyTitle = "title"
    private val valueTitle1 = "Title"
    private val valueTitle2 = ""
    private val keyDescription = "description"
    private val valueDescription1 = "Description"
    private val valueDescription2 = null

    private val jsonString = "{id:null;title:Test;description:null}"

    @Mock
    private var jsonObject = JSONObject()

    @Test
    fun asMap_test() {
        `when`(jsonObject.getInt(keyId))
            .thenReturn(valueId1)
        `when`(jsonObject.optString(keyTitle, "Recipe"))
            .thenReturn(valueTitle1)
        `when`(jsonObject.getString(keyDescription))
            .thenReturn(valueDescription1)
        `when`(jsonObject.isNull(keyDescription))
            .thenReturn(false)
        var recipe = Recipe.getInstance(jsonObject)
        assert(recipe.id == valueId1)
        assert(recipe.title == valueTitle1)
        assert(recipe.description == valueDescription1)

        `when`(jsonObject.getInt(keyId))
            .thenThrow(JSONException(""))
        `when`(jsonObject.optString(keyTitle, "Recipe"))
            .thenReturn(valueTitle2)
        `when`(jsonObject.getString(keyDescription))
            .thenThrow(JSONException(""))
        `when`(jsonObject.isNull(keyDescription))
            .thenReturn(true)
        recipe = Recipe.getInstance(jsonObject)
        assert(recipe.id == valueId2)
        assert(recipe.title == valueTitle2)
        assert(recipe.description == valueDescription2)
    }
}