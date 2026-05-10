package com.pingwinek.jens.cookandbake.lib.firestore

import android.net.Uri
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.LinkedList

abstract class FirestoreDocumentAccessManager {

    companion object {

        @Suppress("SameReturnValue")
        suspend fun delete(storageReference: StorageReference) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.delete())
            return true
        }

        suspend fun getAll(storageReference: StorageReference) : LinkedList<StorageReference> {
            val qs = SuspendedCoroutineWrapper.suspendedFunction(storageReference.listAll())
            return LinkedList(qs.items)
        }

        suspend fun getUri(storageReference: StorageReference) : Uri {
            return SuspendedCoroutineWrapper.suspendedFunction(storageReference.downloadUrl)
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

        suspend fun updateMetadata(storageReference: StorageReference, storageMetadata: StorageMetadata) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.updateMetadata(storageMetadata))
            return true
        }

        @Suppress("SameReturnValue")
        suspend fun upload(storageReference: StorageReference, uri: Uri) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.putFile(uri))
            return true
        }

        @Suppress("unused", "SameReturnValue")
        suspend fun upload(storageReference: StorageReference, uri: Uri, metaData: StorageMetadata) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.putFile(uri, metaData))
            return true
        }

        @Suppress("SameReturnValue")
        suspend fun upload(storageReference: StorageReference, inputStream: InputStream) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.putStream(inputStream))
            return true
        }

        @Suppress("unused", "SameReturnValue")
        suspend fun upload(storageReference: StorageReference, inputStream: InputStream, metadata: StorageMetadata) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.putStream(inputStream, metadata))
            return true
        }
    }
}