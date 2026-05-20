package com.pingwinek.jens.cookandbake.lib

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.FileNotFoundException
import java.io.InputStream

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

    fun openInputStream(uri: Uri): InputStream? {
        var inputStream: InputStream? = null

        try {
            inputStream = contentResolver.openInputStream(uri)
        } catch (e: FileNotFoundException) {
            Log.e(this::class.java.name, e.toString())
        }

        return inputStream
    }

    fun toBitmap(uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        var orientation = 0

        openInputStream(uri)?.let { inputStream ->
            orientation = ExifInterface(inputStream).rotationDegrees
            inputStream.close()
        }

        openInputStream(uri)?.let { inputStream ->
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        }

        bitmap = bitmap?.rotate(orientation.toFloat())

        return bitmap
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = android.graphics.Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
}