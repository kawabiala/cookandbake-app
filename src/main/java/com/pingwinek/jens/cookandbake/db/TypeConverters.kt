package com.pingwinek.jens.cookandbake.db

import android.net.Uri
import androidx.room.TypeConverter

class TypeConverters {
    @TypeConverter
    fun uriToString(uri: Uri?) : String {
        return uri?.toString() ?: ""
    }

    @TypeConverter
    fun stringToUri(uriString: String?) : Uri? {
        return if (uriString == null || uriString.isEmpty()) {
            null
        } else {
            Uri.parse(uriString)
        }
    }
}