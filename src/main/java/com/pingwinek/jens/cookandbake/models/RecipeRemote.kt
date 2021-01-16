package com.pingwinek.jens.cookandbake.models

import android.net.Uri
import org.json.JSONException
import org.json.JSONObject

class RecipeRemote private constructor(
    override var id: Int = 0,
    override val title: String,
    override val description: String?,
    override val instruction: String?,
    override val uri: String?,
    override var lastModified: Long
) : Recipe() {

    private constructor(
        id: Int = 0,
        title: String,
        description: String?,
        instruction: String?,
        uri: String?
    ) : this(id, title, description, instruction, uri,0)

    fun asMap(): Map<String, String> {
        val map = HashMap<String, String>()
        map["id"] = id.toString()
        map["title"] = title
        map["description"] = description ?: ""
        map["instruction"] = instruction ?: ""
        map["uri"] = uri ?: ""
        map["last_modified"] = lastModified.toString()

        return map
    }

    override fun toString(): String {
        return JSONObject(asMap()).toString()
    }

    override fun getUpdated(recipe: Recipe): RecipeRemote {
        return RecipeRemote(id, recipe.title, recipe.description, recipe.instruction, recipe.uri, recipe.lastModified)
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

            val uri = try {
                if (jsonObject.isNull("uri")) {
                    null
                } else {
                    jsonObject.getString("uri")
                }
            } catch (jsonException: JSONException) {
                null
            }

            val lastModified = try {
                if (jsonObject.isNull("last_modified")) {
                    null
                } else {
                    jsonObject.getLong("last_modified")
                }
            } catch (jsonException: JSONException) {
                null
            }

            return when (lastModified) {
                null -> RecipeRemote(id, title, description, instruction, uri)
                else -> RecipeRemote(id, title, description, instruction, uri, lastModified)
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
                    recipeLocal.uri,
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
                recipeLocal.uri,
                recipeLocal.lastModified
            )
        }
    }
}