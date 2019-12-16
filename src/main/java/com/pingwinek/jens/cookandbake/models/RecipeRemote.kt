package com.pingwinek.jens.cookandbake.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.json.JSONException
import org.json.JSONObject

data class RecipeRemote(
    override var rowid: Int = 0,
    override val title: String,
    override val description: String?,
    override val instruction: String?
) : Recipe() {

    fun asMap() : Map<String, String> {
        val map = HashMap<String, String>()
        if (rowid != null) {
            map.put("id", rowid.toString())
        }
        map.put("title", title)
        map.put("description", description ?: "")
        map.put("instruction", instruction ?: "")

        return map
    }

    override fun toString() : String {
        return JSONObject(asMap()).toString()
    }

    fun getUpdated(title: String, description: String?, instruction: String?) : RecipeRemote {
        return RecipeRemote(rowid, title, description, instruction)
    }

    companion object {

        fun parse(jsonObject: JSONObject) : RecipeRemote {

            val id = try {
                jsonObject.getInt("id")
            } catch (jsonException: JSONException) {
                0
            }

            val title = jsonObject.optString("title", "RecipeLocal")

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

            return RecipeRemote(
                id,
                title,
                description,
                instruction
            )
        }

        fun fromLocal(recipeLocal: RecipeLocal) : RecipeRemote {
            return if (recipeLocal.remoteId != null) {
                RecipeRemote(recipeLocal.remoteId, recipeLocal.title, recipeLocal.description, recipeLocal.instruction)
            } else {
                RecipeRemote(0, recipeLocal.title, recipeLocal.description, recipeLocal.instruction)
            }
        }

        /*
        Returns always a RecipeRemote with rowid = 0
         */
        fun newFromLocal(recipeLocal: RecipeLocal) : RecipeRemote {
            return RecipeRemote(0, recipeLocal.title, recipeLocal.description, recipeLocal.instruction)
        }
    }
}