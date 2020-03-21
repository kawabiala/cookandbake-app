package com.pingwinek.jens.cookandbake.lib.sync

import com.pingwinek.jens.cookandbake.utils.CallbackLoopCounter
import java.util.*

class SyncHelper(
    private val syncManager: SynchManager<ModelLocal, Model>,
    private val onDone: () -> Unit
) {
    private var localList: LinkedList<ModelLocal>? = null
    private var remoteList: LinkedList<Model>? = null

    fun setLocalList(list: LinkedList<ModelLocal>) {
        localList = list
        checkLists()
    }

    fun setRemoteList(list: LinkedList<Model>) {
        remoteList = list
        checkLists()
    }

    private fun checkLists() {
        if (localList != null && remoteList != null) {
            loopLists()
        }
    }

    private fun loopLists() {
        // Sync each local entry with the corresponding remote entry or null,
        // if there is no corresponding remote entry
        val syncTaskCounter = CallbackLoopCounter(onDone)
        syncTaskCounter.taskStarted()
        localList?.forEach { localEntry ->
            syncTaskCounter.taskStarted()
            syncManager.sync(
                localEntry,
                remoteList?.find { remoteEntry ->
                    remoteEntry.id == localEntry.remoteId
                }
            ) { syncTaskCounter.taskEnded() }
        }

        // Sync only those remote entries, that have no corresponding local entry
        remoteList?.forEach { remoteEntry ->
            val checkLocal = localList?.find { localEntry ->
                remoteEntry.id == localEntry.remoteId
            }
            if (checkLocal == null) {
                syncTaskCounter.taskStarted()
                syncManager.sync(
                    null,
                    remoteEntry
                ) { syncTaskCounter.taskEnded() }
            }
        }
        syncTaskCounter.taskEnded()
    }
}