package com.pingwinek.jens.cookandbake

import com.pingwinek.jens.cookandbake.models.RecipeRemote
import org.json.JSONException
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RecipeLocalTest {

    private val keyId = "remoteId"
    private val valueId1 = 1
    private val valueId2 = null
    private val keyTitle = "title"
    private val valueTitle1 = "Title"
    private val valueTitle2 = ""
    private val keyDescription = "description"
    private val valueDescription1 = "Description"
    private val valueDescription2 = null

    @Mock
    private var jsonObject = JSONObject()

    @Test
    fun asMap_test() {
        `when`(jsonObject.getInt(keyId))
            .thenReturn(valueId1)
        `when`(jsonObject.optString(keyTitle, "RecipeLocal"))
            .thenReturn(valueTitle1)
        `when`(jsonObject.getString(keyDescription))
            .thenReturn(valueDescription1)
        `when`(jsonObject.isNull(keyDescription))
            .thenReturn(false)
        var recipe = RecipeRemote.parse(jsonObject)
        assert(recipe.rowid == valueId1)
        assert(recipe.title == valueTitle1)
        assert(recipe.description == valueDescription1)

        `when`(jsonObject.getInt(keyId))
            .thenThrow(JSONException(""))
        `when`(jsonObject.optString(keyTitle, "RecipeLocal"))
            .thenReturn(valueTitle2)
        `when`(jsonObject.getString(keyDescription))
            .thenThrow(JSONException(""))
        `when`(jsonObject.isNull(keyDescription))
            .thenReturn(true)
        recipe = RecipeRemote.parse(jsonObject)
        assert(recipe.rowid == valueId2)
        assert(recipe.title == valueTitle2)
        assert(recipe.description == valueDescription2)
    }
}