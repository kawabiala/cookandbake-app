package com.pingwinek.jens.cookandbake.lib.firestore

import android.net.Uri
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.LinkedList

abstract class FirestoreDocumentAccessManager {

    companion object {

        suspend fun delete(storageReference: StorageReference) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.delete())
            return true
        }

        suspend fun getAll(storageReference: StorageReference) : LinkedList<StorageReference> {
            val qs = SuspendedCoroutineWrapper.suspendedFunction(storageReference.listAll())
            return LinkedList(qs.items)
        }

        suspend fun getMetadata(storageReference: StorageReference): StorageMetadata {
            return SuspendedCoroutineWrapper.suspendedFunction(
                storageReference.metadata
            )
        }

        suspend fun writeToFile(file: File, storageReference: StorageReference) {
            val streamDownloadTask = SuspendedCoroutineWrapper.suspendedFunction(
                storageReference.stream
            )

            val inputStream = streamDownloadTask.stream
            val outputStream = file.outputStream()

            withContext(Dispatchers.IO) {
                outputStream.write(inputStream.readBytes())
                inputStream.close()
                outputStream.close()
            }
        }

        suspend fun upload(storageReference: StorageReference, uri: Uri) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.putFile(uri))
            return true
        }

        suspend fun upload(storageReference: StorageReference, uri: Uri, metaData: StorageMetadata) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.putFile(uri, metaData))
            return true
        }
    }
}