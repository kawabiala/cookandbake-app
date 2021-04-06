package com.pingwinek.jens.cookandbake.models

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Files(jsonString: String) : LinkedList<FileRemote>() {

    private val tag = "Files"

    private val files = try {
        JSONArray(jsonString)
    } catch (jsonException: JSONException) {
        Log.e(tag, "Parsing Files failed: ${jsonException.message}")
        JSONArray()
    }

    init {
        for (i in 0 until files.length()) {
            if (files[i] is JSONObject) {
                push(FileRemote.parse(files[i] as JSONObject))
            }
        }
    }
}