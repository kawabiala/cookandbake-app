package com.pingwinek.jens.cookandbake

import org.json.JSONException
import org.json.JSONObject

data class Ingredient(val id: Int?, val recipeId: Int, val quantity: Double?, val unity: String?, val name: String) {

    fun asMap() : Map<String, String> {
        val map = HashMap<String, String>()
        if (id != null) {
            map.put("id", id.toString())
        }
        map.put("recipeId", recipeId.toString())
        map.put("quantity", quantity.toString())
        map.put("unity", unity ?: "")
        map.put("name", name)

        return map
    }

    override fun toString() : String {
        return JSONObject(asMap()).toString()
    }

    companion object {

        fun getInstance(jsonObject: JSONObject) : Ingredient {

            val id = try {
                jsonObject.getInt("id")
            } catch (jsonException: JSONException) {
                null
            }

            val recipeId = jsonObject.optInt("recipeId", -1)

            val quantity = try {
                if (jsonObject.isNull("quantity")) {
                    null
                } else {
                    jsonObject.getDouble("quantity")
                }
            } catch (jsonException: JSONException) {
                null
            }

            val unity = try {
                if (jsonObject.isNull("unity")) {
                    null
                } else {
                    jsonObject.getString("unity")
                }
            } catch (jsonException: JSONException) {
                null
            }

            val name = jsonObject.optString("name", "Ingredient")

            return Ingredient(id, recipeId, quantity, unity, name)
        }
    }
}