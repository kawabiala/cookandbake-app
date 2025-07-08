package com.pingwinek.jens.cookandbake.lib.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.pingwinek.jens.cookandbake.lib.firestore.SuspendedCoroutineWrapper.SuspendedCoroutineException
import java.util.LinkedList

abstract class FirestoreDataAccessManager {

    companion object {

        @Throws(SuspendedCoroutineException::class)
        suspend fun <T> getAll(collectionReference: Query, instantiator: (QueryDocumentSnapshot) -> T) : LinkedList<T> {
            val list = LinkedList<T>()
            val qs = SuspendedCoroutineWrapper.suspendedFunction(collectionReference.get())
            qs.forEach { queryDocumentSnapshot ->
                list.add(instantiator(queryDocumentSnapshot))
            }
            return list
        }

        @Throws(SuspendedCoroutineException::class)
        suspend fun <T> get(documentReference: DocumentReference, instantiator: (DocumentSnapshot) -> T) : T {
            return instantiator(SuspendedCoroutineWrapper.suspendedFunction(documentReference.get()))
        }

        @Throws(SuspendedCoroutineException::class)
        suspend fun <T> new(collectionReference: CollectionReference, insertItem: Any, instantiator: (DocumentSnapshot) -> T) : T {
            val docRef = SuspendedCoroutineWrapper.suspendedFunction(collectionReference.add(insertItem))
            return get(docRef, instantiator)
        }

        @Throws(SuspendedCoroutineException::class)
        suspend fun <T> update(documentReference: DocumentReference, updateItem: Any, instantiator: (DocumentSnapshot) -> T) : T {
            SuspendedCoroutineWrapper.suspendedFunction(documentReference.set(updateItem))
            return get(documentReference, instantiator)
        }

        @Throws(SuspendedCoroutineException::class)
        suspend fun delete(documentReference: DocumentReference, instantiator: (Void?) -> Boolean) : Boolean { // result of delete is null, not Void !
            return instantiator(SuspendedCoroutineWrapper.suspendedFunction(documentReference.delete()))
        }

        suspend fun <T> getAll(
            exceptionCallback: (SuspendedCoroutineException) -> Unit,
            collectionReference: Query,
            instantiator: (QueryDocumentSnapshot) -> T
        ): LinkedList<T>? {

            return try {
                getAll(collectionReference, instantiator)
            } catch (exception: SuspendedCoroutineException) {
                exceptionCallback(exception)
                null
            }
        }

        suspend fun <T> get(
            exceptionCallback: (SuspendedCoroutineException) -> Unit,
            documentReference: DocumentReference,
            instantiator: (DocumentSnapshot) -> T
        ): T? {
            return try {
                get(documentReference, instantiator)
            } catch (exception: SuspendedCoroutineException) {
                exceptionCallback(exception)
                null
            }
        }

        suspend fun <T> new(
            exceptionCallback: (SuspendedCoroutineException) -> Unit,
            collectionReference: CollectionReference,
            insertItem: Any,
            instantiator: (DocumentSnapshot) -> T
        ): T? {
            return try {
                new(collectionReference, insertItem, instantiator)
            } catch (exception: SuspendedCoroutineException) {
                exceptionCallback(exception)
                null
            }
        }

        suspend fun <T> update(
            exceptionCallback: (SuspendedCoroutineException) -> Unit,
            documentReference: DocumentReference,
            updateItem: Any,
            instantiator: (DocumentSnapshot) -> T
        ): T? {
            return try {
                update(documentReference, updateItem, instantiator)
            } catch (exception: SuspendedCoroutineException) {
                exceptionCallback(exception)
                null
            }
        }

        suspend fun delete(
            exceptionCallback: (SuspendedCoroutineException) -> Unit,
            documentReference: DocumentReference,
            instantiator: (Void?) -> Boolean
        ): Boolean {
            return try {
                delete(documentReference, instantiator)
            } catch (exception: SuspendedCoroutineException) {
                exceptionCallback(exception)
                false
            }
        }
    }
}