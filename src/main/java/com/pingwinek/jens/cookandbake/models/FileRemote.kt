package com.pingwinek.jens.cookandbake.models

import org.json.JSONException
import org.json.JSONObject

class FileRemote(
    override var id: Int = 0,
    override val fileName: String,
    override var entityId: Int?,
    override var entity: String?,
    override var lastModified: Long
) : File {

    fun asMap() : Map<String, String> {
        val map = HashMap<String, String>()
        map["id"] = id.toString()
        map["file_name"] = fileName
        map["entity_id"] = entityId.toString()
        map["entity"] = entity ?: ""
        map["last_modified"] = lastModified.toString()
        return map
    }

    override fun toString(): String {
        return JSONObject(asMap()).toString()
    }

    companion object {

        fun parse(jsonObject: JSONObject) : FileRemote {
            val id = try {
                jsonObject.getInt("id")
            } catch (jsonException: JSONException) {
                0
            }

            val fileName = jsonObject.optString("file_name", "")

            val entityId = jsonObject.optInt("entity_id", -1)

            val entity = jsonObject.optString("entity", "")

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
                null -> FileRemote(id, fileName, entityId, entity,0)
                else -> FileRemote(id, fileName, entityId, entity, lastModified)
            }
        }
    }
}