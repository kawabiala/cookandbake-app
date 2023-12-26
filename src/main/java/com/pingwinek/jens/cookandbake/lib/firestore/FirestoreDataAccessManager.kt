package com.pingwinek.jens.cookandbake.lib.firestore

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import java.util.LinkedList
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class FirestoreDataAccessManager {

    private class OnSuccessListener<T>(val onSuccessAction: (T) -> Unit) : com.google.android.gms.tasks.OnSuccessListener<T> {
        override fun onSuccess(result: T) {
            onSuccessAction(result)
        }

    }

    private class OnFailureListener(val tag: String? = this::class.java.name, val onFailureAction: (Exception) -> Unit) : com.google.android.gms.tasks.OnFailureListener {
        override fun onFailure(exception: Exception) {
            Log.e(tag, exception.toString())
            onFailureAction(exception)
        }

    }

    private class OnCanceledListener(val tag: String? = this::class.java.name, val onCanceledAction: () -> Unit) : com.google.android.gms.tasks.OnCanceledListener {
        override fun onCanceled() {
            Log.w(tag, "A data access task was cancelled")
            onCanceledAction()
        }

    }

    companion object {
        private const val TIMEOUT: Long = 10000 // 10sec

        suspend fun <T> getAll(collectionReference: CollectionReference, instantiator: (QueryDocumentSnapshot) -> T) : LinkedList<T> {
            Log.i(this::class.java.name, "getAll for collRef: ${collectionReference.path}")
            val list = LinkedList<T>()
            val qs = suspendedFunction(collectionReference.get())
            qs.forEach { queryDocumentSnapshot ->
                list.add(instantiator(queryDocumentSnapshot))
            }
            return list
        }

        suspend fun <T> get(documentReference: DocumentReference, instantiator: (DocumentSnapshot) -> T) : T {
            Log.i(this::class.java.name, "get for docRef: ${documentReference.path}")
            return instantiator(suspendedFunction(documentReference.get()))
        }

        suspend fun <T> new(collectionReference: CollectionReference, insertItem: Any, instantiator: (DocumentSnapshot) -> T) : T {
            Log.i(this::class.java.name, "new for collRef: ${collectionReference.path}")
            val docRef = suspendedFunction(collectionReference.add(insertItem))
            return get(docRef, instantiator)
        }

        suspend fun <T> update(documentReference: DocumentReference, updateItem: Any, instantiator: (DocumentSnapshot) -> T) : T {
            Log.i(this::class.java.name, "update for docRef: ${documentReference.path}")
            suspendedFunction(documentReference.set(updateItem))
            return get(documentReference, instantiator)
        }

        suspend fun delete(documentReference: DocumentReference, instantiator: (Void?) -> Boolean) : Boolean { // result of delete is null, not Void !
            Log.i(this::class.java.name, "delete for docRef: ${documentReference.path}")
            return instantiator(suspendedFunction(documentReference.delete()))
        }

        private suspend fun <TResult> suspendedFunction(task: Task<TResult>) : TResult {
            var result: TResult
            withTimeout(TIMEOUT) {
                result = suspendCoroutine { continuation ->
                    continuationFunction(continuation, task)
                }
            }
            return result
        }

        private fun <TResult> continuationFunction(continuation: Continuation<TResult>, task: Task<TResult>) {
            task
                .addOnSuccessListener(OnSuccessListener {
                    continuation.resume(it)
                })
                .addOnFailureListener(OnFailureListener {
                    continuation.resumeWithException(it)
                })
                .addOnCanceledListener(OnCanceledListener {
                    continuation.resumeWithException(CancellationException("a continuation was cancelled"))
                })

        }
    }
}