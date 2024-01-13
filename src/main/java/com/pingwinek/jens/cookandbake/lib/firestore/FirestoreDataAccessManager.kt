package com.pingwinek.jens.cookandbake.lib.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.LinkedList

abstract class FirestoreDataAccessManager {

    companion object {

        suspend fun <T> getAll(collectionReference: CollectionReference, instantiator: (QueryDocumentSnapshot) -> T) : LinkedList<T> {
            val list = LinkedList<T>()
            val qs = SuspendedCoroutineWrapper.suspendedFunction(collectionReference.get())
            qs.forEach { queryDocumentSnapshot ->
                list.add(instantiator(queryDocumentSnapshot))
            }
            return list
        }

        suspend fun <T> get(documentReference: DocumentReference, instantiator: (DocumentSnapshot) -> T) : T {
            return instantiator(SuspendedCoroutineWrapper.suspendedFunction(documentReference.get()))
        }

        suspend fun <T> new(collectionReference: CollectionReference, insertItem: Any, instantiator: (DocumentSnapshot) -> T) : T {
            val docRef = SuspendedCoroutineWrapper.suspendedFunction(collectionReference.add(insertItem))
            return get(docRef, instantiator)
        }

        suspend fun <T> update(documentReference: DocumentReference, updateItem: Any, instantiator: (DocumentSnapshot) -> T) : T {
            SuspendedCoroutineWrapper.suspendedFunction(documentReference.set(updateItem))
            return get(documentReference, instantiator)
        }

        suspend fun delete(documentReference: DocumentReference, instantiator: (Void?) -> Boolean) : Boolean { // result of delete is null, not Void !
            return instantiator(SuspendedCoroutineWrapper.suspendedFunction(documentReference.delete()))
        }
    }
}