package com.pingwinek.jens.cookandbake.sources

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata
import com.pingwinek.jens.cookandbake.lib.firestore.FirestoreDocumentAccessManager
import com.pingwinek.jens.cookandbake.models.FileInfo
import com.pingwinek.jens.cookandbake.models.ImageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class FileSourceFB {

    companion object {
        private val auth: FirebaseAuth = Firebase.auth
        private val storage: FirebaseStorage = Firebase.storage
        private const val BASEPATH: String = "/user"
        private const val IMAGE_NAME_KEY: String = "imageName"

        suspend fun getFile(cacheDir: File, filePathString: String): FileInfo? {
            if (filePathString.isEmpty()) return null

            var returnFileInfo: FileInfo? = null

            if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                val storageReference =
                    getStorageReference(filePathString, auth.currentUser!!.uid)

                withContext(Dispatchers.IO) {
                    val contentType = FirestoreDocumentAccessManager
                        .getMetadata(storageReference)
                        .contentType

                    val file = File.createTempFile(storageReference.name, null, cacheDir)
                    FirestoreDocumentAccessManager.writeToFile(file, storageReference)

                    returnFileInfo = FileInfo(file, contentType ?: "")
                }
            }

            return returnFileInfo
        }

        suspend fun getMetadata(pathString: String): ImageInfo? {
            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                getMetadata(getStorageReference(pathString, auth.currentUser!!.uid))
            } else {
                null
            }
        }

        suspend fun listAll(pathString: String): List<String> {
            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                FirestoreDocumentAccessManager.getAll(
                    getStorageReference(pathString, auth.currentUser!!.uid)
                ).map {  storageReference ->
                    getPathString(storageReference, auth.currentUser!!.uid)
                }
            } else {
                listOf()
            }
        }

        internal suspend fun listAllImages(pathString: String): List<ImageInfo> {
            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                FirestoreDocumentAccessManager.getAll(
                    getStorageReference(pathString, auth.currentUser!!.uid)
                ).map {  storageReference ->
                    getMetadata(storageReference)
                }
            } else {
                listOf()
            }
        }

        suspend fun updateImageName(pathString: String, imageName: String): ImageInfo? {
            if (pathString.isEmpty()) return null

            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                val metadata = storageMetadata {
                    setCustomMetadata(IMAGE_NAME_KEY, imageName)
                }
                val storageReference = getStorageReference(pathString, auth.currentUser!!.uid)
                FirestoreDocumentAccessManager.updateMetadata(
                    storageReference, metadata)
                getMetadata(storageReference)
            } else {
                null
            }
        }

        suspend fun uploadFile(pathString: String, uri: Uri): Boolean {
            if (pathString.isEmpty()) return false

            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                FirestoreDocumentAccessManager.upload(
                    getStorageReference(pathString, auth.currentUser!!.uid),
                    uri
                )
            } else {
                false
            }
        }

        suspend fun uploadInputStream(pathString: String, inputStream: InputStream, imageName: String): ImageInfo? {
            if (pathString.isEmpty()) return null

            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                val metadata = storageMetadata {
                    setCustomMetadata(IMAGE_NAME_KEY, imageName)
                }
                val storageReference = getStorageReference(pathString, auth.currentUser!!.uid)
                FirestoreDocumentAccessManager.upload(
                    storageReference = getStorageReference(pathString, auth.currentUser!!.uid),
                    inputStream = inputStream,
                    metadata = metadata
                )
                getMetadata(storageReference)
            } else {
                null
            }
        }

        @Suppress("unused")
        suspend fun deleteDir(pathString: String): Boolean {
            if (pathString.isEmpty()) return false

            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                FirestoreDocumentAccessManager.delete(
                    getStorageReference(pathString, auth.currentUser!!.uid)
                )
            } else {
                false
            }
        }

        suspend fun deleteFile(pathString: String): Boolean {
            if (pathString.isEmpty()) return false
            Log.i(this::class.java.name, "deleteFile: $pathString")

            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                FirestoreDocumentAccessManager.delete(
                    getStorageReference(pathString, auth.currentUser!!.uid)
                )
            } else {
                false
            }
        }

        private suspend fun getMetadata(storageReference: StorageReference) : ImageInfo {
            val metaData = FirestoreDocumentAccessManager.getMetadata(storageReference)
            Log.i(this::class.java.name, "getMetadata - path: ${metaData.path}, name: ${metaData.name}")

            return ImageInfo(
                imageId = metaData.name ?: "",
                imageName = metaData.getCustomMetadata(IMAGE_NAME_KEY) ?: "",
                downloadUri =  FirestoreDocumentAccessManager.getUri(storageReference)
            )
        }

        private fun getStorageReference(pathString: String, userId: String): StorageReference {
            return storage.reference.child("$BASEPATH/$userId/$pathString")
        }

        private fun getPathString(storageReference: StorageReference, userId: String): String {
            return storageReference.path.removePrefix("$BASEPATH/$userId/")
        }


    }
}