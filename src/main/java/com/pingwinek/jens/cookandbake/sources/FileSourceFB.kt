package com.pingwinek.jens.cookandbake.sources

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.pingwinek.jens.cookandbake.lib.firestore.FirestoreDocumentAccessManager
import com.pingwinek.jens.cookandbake.models.FileInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileSourceFB {

    companion object {
        private val auth: FirebaseAuth = Firebase.auth
        private val storage: FirebaseStorage = Firebase.storage
        private const val BASEPATH: String = "/user"

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

            return if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
                FirestoreDocumentAccessManager.delete(
                    getStorageReference(pathString, auth.currentUser!!.uid)
                )
            } else {
                false
            }
        }

        private fun getStorageReference(pathString: String, userId: String): StorageReference {
            return storage.reference.child("$BASEPATH/$userId/$pathString")
        }

        private fun getPathString(storageReference: StorageReference, userId: String): String {
            return storageReference.path.removePrefix("$BASEPATH/$userId/")
        }


    }
}