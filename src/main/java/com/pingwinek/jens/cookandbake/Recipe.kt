package com.pingwinek.jens.cookandbake

import org.json.JSONException
import org.json.JSONObject

data class Recipe(val id: Int?, val title: String, val description: String?, val instruction: String?) {

    fun asMap() : Map<String, String> {
        val map = HashMap<String, String>()
        if (id != null) {
            map.put("id", id.toString())
        }
        map.put("title", title)
        map.put("description", description ?: "")
        map.put("instruction", instruction ?: "")

        return map
    }

    override fun toString() : String {
        return JSONObject(asMap()).toString()
    }

    companion object {

        fun getInstance(jsonObject: JSONObject) : Recipe {

            val id = try {
                jsonObject.getInt("id")
            } catch (jsonException: JSONException) {
                null
            }

            val title = jsonObject.optString("title", "Recipe")

            val description = try {
                if (jsonObject.isNull("description")) {
                    null
                } else {
                    jsonObject.getString("description")
                }
            } catch (jsonException: JSONException) {
                null
            }

            val instruction = try {
                if (jsonObject.isNull("instruction")) {
                    null
                } else {
                    jsonObject.getString("instruction")
                }
            } catch (jsonException: JSONException) {
                null
            }

            return Recipe(id, title, description, instruction)
        }
    }
}