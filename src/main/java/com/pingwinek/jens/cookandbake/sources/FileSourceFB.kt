package com.pingwinek.jens.cookandbake.sources

import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.firestore.FirestoreDocumentAccessManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class FileSourceFB(application: PingwinekCooksApplication) {

    private val externalFilesDir = application.applicationContext.getExternalFilesDir(null)

    private val auth: FirebaseAuth = Firebase.auth
    private val storage: FirebaseStorage = Firebase.storage
    private val basePath: String = "/user"


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

    companion object {
        private val auth: FirebaseAuth = Firebase.auth
        private val storage: FirebaseStorage = Firebase.storage
        private const val BASEPATH: String = "/user"

        suspend fun getFile(cacheDir: File, pathString: String): File? {
            if (pathString.isEmpty()) return null

            var returnFile: File? = null

            val storageReference =
                getStorageReference(pathString, auth.currentUser!!.uid)

            if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                withContext(Dispatchers.IO) {
                    val contentType = FirestoreDocumentAccessManager
                        .getMetadata(storageReference)
                        .contentType
                    val suffix = if (contentType == "application/pdf") ".pdf" else ""

                    returnFile = File.createTempFile(storageReference.name, suffix, cacheDir)
                    returnFile?.let {
                        FirestoreDocumentAccessManager.writeToFile(it, storageReference)
                    }
                }
            }

            Log.i(this::class.java.name, "size: ${returnFile?.length()}")

            return returnFile
        }

        suspend fun uploadFile(pathString: String, uri: Uri): Boolean {
            if (pathString.isEmpty()) return false

//            val metaData: StorageMetadata = StorageMetadata()

            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                FirestoreDocumentAccessManager.upload(
                    getStorageReference(pathString, auth.currentUser!!.uid),
                    uri
                )
            } else {
                false
            }
        }

        private fun getStorageReference(pathString: String, userId: String): StorageReference {
            return storage.reference.child("$BASEPATH/$userId/$pathString")
        }


    }
}