package com.pingwinek.jens.cookandbake.lib.firestore

import com.google.android.gms.tasks.Task
import com.pingwinek.jens.cookandbake.TIMEOUT
import com.pingwinek.jens.cookandbake.UNKNOWN_EXCEPTIOM
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SuspendedCoroutineWrapper {

    class SuspendedCoroutineException(exception: Exception?) : Exception(exception) {

        constructor(message: String): this(Exception(message))
    }

    companion object {

        @Throws(SuspendedCoroutineException::class)
        suspend fun <T> suspendedFunction(task: Task<T>): T {
            return suspendedFunction(TIMEOUT, task)
        }

        @Throws(SuspendedCoroutineException::class)
        suspend fun <T> suspendedFunction(timeout: Long, task: Task<T>): T {
            return withTimeout(timeout) {
                suspendCoroutine { continuation ->
                    task.addOnCompleteListener { resultingTask ->
                        if (resultingTask.isSuccessful) {
                            continuation.resume(task.result)
                        } else {
                            task.exception?.let {
                                continuation.resumeWithException(SuspendedCoroutineException(it))
                            } ?: continuation.resumeWithException(SuspendedCoroutineException(UNKNOWN_EXCEPTIOM))
                        }
                    }
                }
            }
        }

    }
}