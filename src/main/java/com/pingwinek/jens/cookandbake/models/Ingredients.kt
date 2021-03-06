package com.pingwinek.jens.cookandbake.models

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Ingredients(jsonString: String) : LinkedList<IngredientRemote>() {

    private val tag = "Ingredients"

    private val ingredients = try {
        JSONArray(jsonString)
    } catch (jsonException: JSONException) {
        Log.e(tag, "Parsing Ingredients failed: ${jsonException.message}")
        JSONArray()
    }

    init {
        for (i in 0 until ingredients.length()) {
            if (ingredients[i] is JSONObject) {
                push(IngredientRemote.parse(ingredients[i] as JSONObject))
            }
        }
    }
}