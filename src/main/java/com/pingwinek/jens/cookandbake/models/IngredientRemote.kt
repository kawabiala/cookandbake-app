package com.pingwinek.jens.cookandbake.models

import org.json.JSONException
import org.json.JSONObject

data class IngredientRemote(
    override val id: Int?,
    override val recipeId: Int,
    override val quantity: Double?,
    override val unity: String?,
    override val name: String
) : Ingredient() {

    fun asMap() : Map<String, String> {
        val map = HashMap<String, String>()
        if (id != null) {
            map.put("remoteId", id.toString())
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

    fun getUpdated(quantity: Double?, unity: String?, name: String) : IngredientRemote {
        return IngredientRemote(id, recipeId, quantity, unity, name)
    }

    companion object {

        fun getInstance(jsonObject: JSONObject) : IngredientRemote {
            val id = try {
                jsonObject.getInt("remoteId")
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

            val name = jsonObject.optString("name", "IngredientRemote")

            return IngredientRemote(
                id,
                recipeId,
                quantity,
                unity,
                name
            )
        }

        fun newFromLocal(local: IngredientLocal, recipeId: Int) : IngredientRemote {
            return IngredientRemote(0, recipeId, local.quantity, local.unity, local.name)
        }
    }
}