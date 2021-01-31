package com.pingwinek.jens.cookandbake

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.Executors

class RefreshManager(private val refresh: Refresh) {

    interface Refresh {
        suspend fun doRefresh() : Boolean
    }

    private val synchronizer = Mutex()
//    private var currentCallbackList = LinkedList<(result: Boolean) -> Unit>()
//    private var inProgress = false
    private var result: Deferred<Boolean>? = null
//    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
/*
    fun refresh(callback: (Boolean) -> Unit) {
        var needsRefresh = false
        synchronize {
            currentCallbackList.add(callback)
            if (!inProgress) {
                inProgress = true
                needsRefresh = true
            }
        }
        if (needsRefresh) {
            response(refresh.doRefresh())
        }
    }
*/
    suspend fun refresh() : Boolean {
        synchronizer.withLock {
            if (result == null || result?.isCompleted == true || result?.isCancelled == true) {
                result = CoroutineScope(Dispatchers.IO).async {
                    refresh.doRefresh()
                }
            }
        }
        return result?.await() ?: false
    }
/*
    private fun response(result: Boolean) {
        synchronize {
            currentCallbackList.forEach { callback ->
                callback(result)
            }
            currentCallbackList = LinkedList()
            inProgress = false
        }
    }
*/
    /*
    private fun synchronize(synchronizedBlock: () -> Unit) = runBlocking {
        withContext(singleThreadDispatcher) {
            synchronizedBlock()
        }
    }

     */
}
