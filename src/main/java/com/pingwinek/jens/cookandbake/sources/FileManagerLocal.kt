package com.pingwinek.jens.cookandbake.sources

import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.util.Log
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.util.*

class FileManagerLocal(application: PingwinekCooksApplication) {

    private val externalFilesDir = application.applicationContext.getExternalFilesDir(null)

    fun loadFile(name: String) : ParcelFileDescriptor? {
        val file = File(externalFilesDir, name)
        if (!file.canRead()) return null

        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    suspend fun newFile(parcelFileDescriptor: ParcelFileDescriptor) : String {
        val fileName = uniqueLocalFileName()
        saveFile(parcelFileDescriptor, fileName)
        return fileName
    }

    suspend fun updateFile(parcelFileDescriptor: ParcelFileDescriptor, name: String) {
        saveFile(parcelFileDescriptor, name)
    }

    private suspend fun saveFile(parcelFileDescriptor: ParcelFileDescriptor, name: String) {
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(externalFilesDir, name)
        if (externalFilesDir?.canWrite() == true && (!file.exists() || file.canWrite())) {

            withContext(Dispatchers.IO) {
                val fileOutputStream = FileOutputStream(file)

                try {
                    fileOutputStream.write(inputStream.readBytes())
                } catch (ioException: IOException) {
                    Log.e(
                        this::class.java.name,
                        "Could not save file due to exception: $ioException"
                    )
                } finally {
                    fileOutputStream.close()
                }
            }
        }
    }

    private fun uniqueLocalFileName() : String {
        return "local_${UUID.randomUUID()}"
    }
}