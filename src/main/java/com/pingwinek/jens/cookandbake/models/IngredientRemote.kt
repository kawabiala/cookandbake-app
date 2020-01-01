package com.pingwinek.jens.cookandbake.models

import org.json.JSONException
import org.json.JSONObject

class IngredientRemote private constructor(
    override val id: Int = 0,
    override val recipeId: Int,
    override val quantity: Double?,
    override val unity: String?,
    override val name: String,
    override var lastModified: Long
) : Ingredient() {

    private constructor(
        id: Int,
        recipeId: Int,
        quantity: Double?,
        unity: String?,
        name: String
    ) : this(id, recipeId, quantity, unity, name, 0)

    fun asMap(): Map<String, String> {
        val map = HashMap<String, String>()
        map["id"] = id.toString()
        map["recipe_id"] = recipeId.toString()
        map["quantity"] = quantity.toString()
        map["unity"] = unity ?: ""
        map["name"] = name
        map["lastModified"] = lastModified.toString()

        return map
    }

    override fun toString(): String {
        return JSONObject(asMap()).toString()
    }

    override fun getUpdated(ingredient: Ingredient): IngredientRemote {
        return IngredientRemote(id, recipeId, ingredient.quantity, ingredient.unity, ingredient.name, ingredient.lastModified)
    }

    companion object {

        fun parse(jsonObject: JSONObject): IngredientRemote {
            val id = try {
                jsonObject.getInt("id")
            } catch (jsonException: JSONException) {
                0
            }

            val recipeId = jsonObject.optInt("recipe_id", -1)

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

            val lastModified = try {
                if (jsonObject.isNull("lastModified")) {
                    null
                } else {
                    jsonObject.getLong("lastModified")
                }
            } catch (jsonException: JSONException) {
                null
            }

            return when (lastModified) {
                null -> IngredientRemote(id, recipeId, quantity, unity, name)
                else -> IngredientRemote(id, recipeId, quantity, unity, name, lastModified)
            }
        }

        fun fromLocal(ingredientLocal: IngredientLocal, recipeId: Int): IngredientRemote {
            return when (ingredientLocal.remoteId) {
                null -> newFromLocal(ingredientLocal, recipeId)
                else -> IngredientRemote(
                    ingredientLocal.remoteId,
                    recipeId,
                    ingredientLocal.quantity,
                    ingredientLocal.unity,
                    ingredientLocal.name,
                    ingredientLocal.lastModified
                )
            }
        }

        fun newFromLocal(ingredientLocal: IngredientLocal, recipeId: Int): IngredientRemote {
            return IngredientRemote(
                0,
                recipeId,
                ingredientLocal.quantity,
                ingredientLocal.unity,
                ingredientLocal.name,
                ingredientLocal.lastModified
            )
        }
    }
}