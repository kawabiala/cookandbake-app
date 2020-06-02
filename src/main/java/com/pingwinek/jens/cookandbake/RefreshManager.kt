package com.pingwinek.jens.cookandbake

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Executors

class RefreshManager(private val refresh: Refresh) {

    interface Refresh {
        fun doRefresh(callback: (code: Int, response: String) -> Unit)
    }

    private var currentCallbackList = LinkedList<(result: Boolean) -> Unit>()
    private var inProgress = false
    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

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
            refresh.doRefresh { code, _ ->
                if (code == 200) {
                    response(true)
                } else {
                    response(false)
                }
            }
        }
    }

    private fun response(result: Boolean) {
        synchronize {
            currentCallbackList.forEach { callback ->
                callback(result)
            }
            currentCallbackList = LinkedList()
            inProgress = false
        }
    }

    private fun synchronize(synchronizedBlock: () -> Unit) = runBlocking {
        withContext(singleThreadDispatcher) {
            synchronizedBlock()
        }
    }
}
