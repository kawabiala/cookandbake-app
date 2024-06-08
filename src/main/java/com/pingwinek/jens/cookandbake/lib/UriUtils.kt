package com.pingwinek.jens.cookandbake.lib

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import com.pingwinek.jens.cookandbake.utils.SingletonHolder

class UriUtils private constructor(context: Context){

    private val contentResolver = context.contentResolver

    companion object : SingletonHolder<UriUtils, Context>(::UriUtils)

    fun getNameForUri(uri: Uri): String? {
        if (uri.scheme == null || uri.scheme != ContentResolver.SCHEME_CONTENT) {
            Log.i(this::class.java.name, "Not content scheme for uri $uri")
            return null
        }

        val projectionString1  = OpenableColumns.DISPLAY_NAME
        val projectionString2  = MediaStore.Images.Media.DATA
        val projection = arrayOf(projectionString1, projectionString2)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        var name: String? = null

        try {
            if (cursor?.moveToFirst() == true) {
                var nameIndex = cursor.getColumnIndex(projectionString1)
                if (nameIndex > -1) {
                    name = cursor.getString(nameIndex)
                }
                if (name == null) nameIndex = cursor.getColumnIndex(projectionString2)
                if (nameIndex > -1) {
                    name = cursor.getString(nameIndex)
                }
            }
        } finally {
            cursor?.close()
        }

        return name
    }

    fun getSizeForUri(uri: Uri): Long? {
        if (uri.scheme == null || uri.scheme != ContentResolver.SCHEME_CONTENT) {
            Log.i(this::class.java.name, "Not content scheme for uri $uri")
            return null
        }

        val projection = arrayOf(OpenableColumns.SIZE)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        var size: Long? = null

        try {
            if (cursor?.moveToFirst() == true) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex > -1) {
                    size = cursor.getLong(sizeIndex)
                }
            }
        } finally {
            cursor?.close()
        }

        return size
    }

    fun getTypeForUri(uri: Uri): String? {
        return contentResolver.getType(uri)
    }
}