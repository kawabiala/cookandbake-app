package com.pingwinek.jens.cookandbake.lib.firestore

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.pingwinek.jens.cookandbake.lib.sync.Model
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
            Log.i(this::class.java.name, "onSuccess")
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
        const val TIMEOUT: Long = 10000 // 10sec

        suspend fun <T : Model> getAll(collectionReference: CollectionReference, /*cls: Class<T>,*/ instantiator: (QueryDocumentSnapshot) -> T) : LinkedList<T> {
            val list = LinkedList<T>()
            Log.i(this::class.java.name, "before suspendedFunction")
            suspendedFunction(collectionReference.get()).forEach { queryDocumentSnapshot ->
                //list.add(cls.getDeclaredConstructor(QueryDocumentSnapshot::class.java).newInstance(queryDocumentSnapshot))
                list.add(instantiator(queryDocumentSnapshot))
            }
            Log.i(this::class.java.name, "after suspendedFunction")
            return list
        }

        suspend fun <T: Model> get(documentReference: DocumentReference, instantiator: (DocumentSnapshot) -> T) : T {
            return instantiator(suspendedFunction(documentReference.get()))
        }

        suspend fun <T: Model> new(collectionReference: CollectionReference, insertItem: Any, instantiator: (DocumentSnapshot) -> T) : T {
            val docRef = suspendedFunction(collectionReference.add(insertItem))
            return get(docRef, instantiator)
        }

        suspend fun <T: Model> update(documentReference: DocumentReference, updateItem: Any, instantiator: (DocumentSnapshot) -> T) : T {
            suspendedFunction(documentReference.set(updateItem))
            return get(documentReference, instantiator)
        }

        suspend fun <T: Model> delete(documentReference: DocumentReference, instantiator: (Void) -> Boolean) : Boolean {
            return instantiator(suspendedFunction(documentReference.delete()))
        }

        private suspend fun <TResult> suspendedFunction(task: Task<TResult>) : TResult {
            var result: TResult
            Log.i(this::class.java.name, "before withTimeout")
            withTimeout(TIMEOUT) {
                result = suspendCoroutine { continuation ->
                    continuationFunction(continuation, task)
                }
            }
            Log.i(this::class.java.name, "after withTimeout")
            return result
        }

        private fun <TResult> continuationFunction(continuation: Continuation<TResult>, task: Task<TResult>) {
            Log.i(this::class.java.name, "before adding listeners")
            task
                .addOnSuccessListener(OnSuccessListener {
                    Log.i(this::class.java.name, "before resume")
                    continuation.resume(it)
                })
                .addOnFailureListener(OnFailureListener {
                    continuation.resumeWithException(it)
                })
                .addOnCanceledListener(OnCanceledListener {
                    continuation.resumeWithException(CancellationException("a continuation was cancelled"))
                })
            Log.i(this::class.java.name, "after adding listeners")

        }

    }
}