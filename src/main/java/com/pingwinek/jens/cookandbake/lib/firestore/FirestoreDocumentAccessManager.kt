package com.pingwinek.jens.cookandbake.lib.firestore

import android.net.Uri
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.LinkedList

abstract class FirestoreDocumentAccessManager {

    companion object {
        const val MAX_FILE_SIZE: Long = 1000000

        suspend fun getAll(storageReference: StorageReference) : LinkedList<StorageReference> {
            val qs = SuspendedCoroutineWrapper.suspendedFunction(storageReference.listAll())
            return LinkedList(qs.items)
        }

        suspend fun get(storageReference: StorageReference) : ByteArray {
            val stream = storageReference.getStream().snapshot.stream
            val byteArray = ByteArray(MAX_FILE_SIZE.toInt())
            withContext(Dispatchers.IO) {
                var i = 0
                while (stream.available() > 0) {
                    byteArray[i] = stream.read().toByte()
                    i++
                }
                stream.close()
            }
            return byteArray
        }

        suspend fun upload(storageReference: StorageReference, uri: Uri) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.putFile(uri))
            return true
        }

        suspend fun upload(storageReference: StorageReference, uri: Uri, metaData: StorageMetadata) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.putFile(uri, metaData))
            return true
        }

        suspend fun delete(storageReference: StorageReference) : Boolean {
            SuspendedCoroutineWrapper.suspendedFunction(storageReference.delete())
            return true
        }
    }
}