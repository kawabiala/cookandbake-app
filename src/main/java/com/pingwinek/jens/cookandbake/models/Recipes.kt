package com.pingwinek.jens.cookandbake.models

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Recipes(jsonString: String) : LinkedList<RecipeRemote>() {

    private val tag = "Recipes"

    private val recipes = try {
        JSONArray(jsonString)
    } catch (jsonException: JSONException) {
        Log.e(tag, jsonException.message.toString())
        JSONArray()
    }

    init {
        for (i in 0 until recipes.length()) {
            if (recipes[i] is JSONObject) {
                push(RecipeRemote.parse(recipes[i] as JSONObject))
            }
        }
    }
}