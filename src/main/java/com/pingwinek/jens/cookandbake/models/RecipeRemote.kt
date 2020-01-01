package com.pingwinek.jens.cookandbake.models

import org.json.JSONException
import org.json.JSONObject

class RecipeRemote private constructor(
    override var id: Int = 0,
    override val title: String,
    override val description: String?,
    override val instruction: String?,
    override var lastModified: Long
) : Recipe() {

    private constructor(
        id: Int = 0,
        title: String,
        description: String?,
        instruction: String?
    ) : this(id, title, description, instruction, 0)

    fun asMap(): Map<String, String> {
        val map = HashMap<String, String>()
        map["id"] = id.toString()
        map["title"] = title
        map["description"] = description ?: ""
        map["instruction"] = instruction ?: ""
        map["lastModified"] = lastModified.toString()

        return map
    }

    override fun toString(): String {
        return JSONObject(asMap()).toString()
    }

    override fun getUpdated(recipe: Recipe): RecipeRemote {
        return RecipeRemote(id, recipe.title, recipe.description, recipe.instruction, recipe.lastModified)
    }

    companion object {

        fun parse(jsonObject: JSONObject): RecipeRemote {

            val id = try {
                jsonObject.getInt("id")
            } catch (jsonException: JSONException) {
                0
            }

            val title = jsonObject.optString("title", "RecipeRemote")

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
                null -> RecipeRemote(id, title, description, instruction)
                else -> RecipeRemote(id, title, description, instruction, lastModified)
            }
        }

        fun fromLocal(recipeLocal: RecipeLocal): RecipeRemote {
            return when (recipeLocal.remoteId) {
                null -> newFromLocal(recipeLocal)
                else -> RecipeRemote(
                    recipeLocal.remoteId,
                    recipeLocal.title,
                    recipeLocal.description,
                    recipeLocal.instruction,
                    recipeLocal.lastModified
                )
            }
        }

        /*
        Returns always a RecipeRemote with id = 0
         */
        fun newFromLocal(recipeLocal: RecipeLocal): RecipeRemote {
            return RecipeRemote(
                0,
                recipeLocal.title,
                recipeLocal.description,
                recipeLocal.instruction,
                recipeLocal.lastModified
            )
        }
    }
}