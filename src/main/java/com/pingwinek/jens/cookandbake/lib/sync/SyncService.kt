package com.pingwinek.jens.cookandbake.lib.sync

import android.app.Application
import com.pingwinek.jens.cookandbake.SingletonHolder
import com.pingwinek.jens.cookandbake.utils.CallbackLoopCounter
import java.util.*

class SyncService private constructor(val application: Application) {

    val syncManagerIdentifiers = LinkedList<SyncManagerIdentifier<ModelLocal, Model>>()

    @Suppress("Unchecked_Cast")
    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>registerSyncManager(syncManager: SyncManager<TLocal, TRemote>) {
        syncManagerIdentifiers.add(
            SyncManagerIdentifier(
                TLocal::class.java as Class<ModelLocal>,
                TRemote::class.java as Class<Model>,
                syncManager as SyncManager<ModelLocal, Model>
            )
        )
    }

    @Suppress("Unchecked_Cast")
    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>getSyncManager() : SyncManager<TLocal, TRemote>? {
        val syncManagerIdentifier = syncManagerIdentifiers.find { syncManagerIdentifier ->
            syncManagerIdentifier.isSuitableFor(TLocal::class.java, TRemote::class.java)
        }
        return if (syncManagerIdentifier != null) {
            syncManagerIdentifier.syncManager as SyncManager<TLocal, TRemote>
        } else {
            null
        }
    }

    @Suppress("Unused")
    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>removeSyncManager() {
        syncManagerIdentifiers.removeAll {
            it.isSuitableFor(TLocal::class.java, TRemote::class.java)
        }
    }

    fun removeAllSyncManagers() {
        syncManagerIdentifiers.clear()
    }

    @Suppress("Unchecked_Cast")
    inline fun <reified TLocal: ModelLocal, reified TRemote: Model> syncEntry(
        local: TLocal?,
        remote: TRemote?,
        noinline onDone: () -> Unit
    ) {
        try {
            val synchManager = getSyncManager<TLocal, TRemote>() as SyncManager<ModelLocal, Model>
            synchManager.sync(local, remote, onDone)
        } catch (classCastException: ClassCastException) {
            onDone()
        }
    }

    @Suppress("Unchecked_Cast")
    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>syncEntry(
        localId: Int,
        noinline onDone: () -> Unit
    ) {
        try {
            val synchManager = getSyncManager<TLocal, TRemote>() as SyncManager<ModelLocal, Model>
            val localSource = SourceProvider.getLocalSource(TLocal::class.java)
            val remoteSource = SourceProvider.getRemoteSource(TRemote::class.java)

            if (localSource != null && remoteSource != null) {
                localSource.get(localId).setResultHandler { localResult ->
                    val local = localResult.value
                    if (localResult.status == Promise.Status.SUCCESS && local != null) {
                        local.remoteId?.let { remoteId ->
                            remoteSource.get(remoteId).setResultHandler { remoteResult ->
                                val remote = remoteResult.value
                                if (remoteResult.status == Promise.Status.SUCCESS) {
                                    synchManager.sync(local, remote, onDone)
                                } else {
                                    onDone()
                                }
                            }
                        } ?: onDone()
                    } else {
                        onDone()
                    }
                }
            } else {
                onDone()
            }
        } catch (classCastException: ClassCastException) {
            onDone()
        }

    }

    @Suppress("Unchecked_Cast")
    inline fun <reified TLocal: ModelLocal, reified TRemote: Model> sync(
        noinline onDone: () -> Unit
    ) {
        val syncTaskCounter = CallbackLoopCounter(onDone)
        syncTaskCounter.taskStarted()
        try {
            val synchManager = getSyncManager<TLocal, TRemote>() as SyncManager<ModelLocal, Model>
            val syncHelper =
                SyncHelper(synchManager) { syncTaskCounter.taskEnded() }
            val localSource = SourceProvider.getLocalSource(TLocal::class.java)
            val remoteSource = SourceProvider.getRemoteSource(TRemote::class.java)

            if (localSource != null && remoteSource != null) {
                localSource.getAll().setResultHandler { result ->
                    if (result.status == Promise.Status.SUCCESS) {
                        syncHelper.setLocalList(result.value as LinkedList<ModelLocal>)
                    } else {
                        syncTaskCounter.taskEnded()
                    }
                }
                remoteSource.getAll().setResultHandler { result ->
                    if (result.status == Promise.Status.SUCCESS) {
                        syncHelper.setRemoteList(result.value as LinkedList<Model>)
                    } else {
                        syncTaskCounter.taskEnded()
                    }
                }
            } else {
                syncTaskCounter.taskEnded()
            }
        } catch (classCastException: ClassCastException) {
            syncTaskCounter.taskEnded()
        }
    }

    fun syncAll(onDone: () -> Unit) {
        val syncTaskCounter = CallbackLoopCounter(onDone)
        syncTaskCounter.taskStarted()
        syncManagerIdentifiers.forEach { syncManagerIdentifier ->
            syncTaskCounter.taskStarted()
            val syncHelper = SyncHelper(
                syncManagerIdentifier.syncManager
            ) { syncTaskCounter.taskEnded() }
            val localSource = SourceProvider.getLocalSource(syncManagerIdentifier.localClass)
            val remoteSource = SourceProvider.getRemoteSource(syncManagerIdentifier.remoteClass)

            if (localSource != null && remoteSource != null) {
                localSource.getAll().setResultHandler { result ->
                    val linkedList = result.value
                    if (result.status == Promise.Status.SUCCESS && linkedList != null) {
                        syncHelper.setLocalList(linkedList)
                    } else {
                        syncTaskCounter.taskEnded()
                    }
                }
                remoteSource.getAll().setResultHandler { result ->
                    val linkedList = result.value
                    if (result.status == Promise.Status.SUCCESS && linkedList != null) {
                        syncHelper.setRemoteList(linkedList)
                    } else {
                        syncTaskCounter.taskEnded()
                    }
                }
            } else {
                syncTaskCounter.taskEnded()
            }
        }
        syncTaskCounter.taskEnded()
    }

    class SyncManagerIdentifier<TLocal: ModelLocal, TRemote: Model>(
        val localClass: Class<ModelLocal>,
        val remoteClass: Class<Model>, val syncManager: SyncManager<TLocal, TRemote>
    ) {

        fun isSuitableFor(localClass: Class<*>, remoteClass: Class<*>) : Boolean {
            return (localClass == this.localClass && remoteClass == this.remoteClass)
        }
    }

    companion object : SingletonHolder<SyncService, Application>(::SyncService)
}