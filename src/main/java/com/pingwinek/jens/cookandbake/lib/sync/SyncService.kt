package com.pingwinek.jens.cookandbake.lib.sync

import android.app.Application
import com.pingwinek.jens.cookandbake.SingletonHolder
import com.pingwinek.jens.cookandbake.utils.CallbackLoopCounter
import java.lang.ClassCastException
import java.util.*

class SyncService private constructor(val application: Application) {

    val syncManagerIdentifiers = LinkedList<SyncManagerIdentifier<ModelLocal, Model>>()

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>registerSyncManager(synchManager: SynchManager<TLocal, TRemote>) {
        syncManagerIdentifiers.add(
            SyncManagerIdentifier<ModelLocal, Model>(
                TLocal::class.java as Class<ModelLocal>,
                TRemote::class.java as Class<Model>,
                synchManager as SynchManager<ModelLocal, Model>
            )
        )
    }

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>getSyncManager() : SynchManager<TLocal, TRemote>? {
        val syncManagerIdentifier = syncManagerIdentifiers.find { syncManagerIdentifier ->
            syncManagerIdentifier.isSuitableFor(TLocal::class.java, TRemote::class.java)
        }
        return if (syncManagerIdentifier != null) {
            syncManagerIdentifier.synchManager as SynchManager<TLocal, TRemote>
        } else {
            null
        }
    }

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>removeSyncManager() {
        syncManagerIdentifiers.removeAll {
            it.isSuitableFor(TLocal::class.java, TRemote::class.java)
        }
    }

    fun removeAllSyncManagers() {
        syncManagerIdentifiers.clear()
    }

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model> syncEntry(
        local: TLocal?,
        remote: TRemote?,
        noinline onDone: () -> Unit
    ) {
        try {
            val synchManager = getSyncManager<TLocal, TRemote>() as SynchManager<ModelLocal, Model>
            synchManager.sync(local, remote, onDone)
        } catch (classCastException: ClassCastException) {
            onDone()
        }
    }

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model>syncEntry(
        localId: Int,
        noinline onDone: () -> Unit
    ) {
        try {
            val synchManager = getSyncManager<TLocal, TRemote>() as SynchManager<ModelLocal, Model>
            val localSource = SourceProvider.getLocalSource(TLocal::class.java)
            val remoteSource = SourceProvider.getRemoteSource(TRemote::class.java)

            if (localSource != null && remoteSource != null) {
                localSource.get(localId) { status, local ->
                    if (status == Source.Status.SUCCESS && local != null) {
                        local.remoteId?.let { remoteId ->
                            remoteSource.get(remoteId) { status, remote ->
                                if (status == Source.Status.SUCCESS) {
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

    inline fun <reified TLocal: ModelLocal, reified TRemote: Model> sync(
        noinline onDone: () -> Unit
    ) {
        val syncTaskCounter = CallbackLoopCounter(onDone)
        syncTaskCounter.taskStarted()
        try {
            val synchManager = getSyncManager<TLocal, TRemote>() as SynchManager<ModelLocal, Model>
            val syncHelper =
                SyncHelper(synchManager) { syncTaskCounter.taskEnded() }
            val localSource = SourceProvider.getLocalSource(TLocal::class.java)
            val remoteSource = SourceProvider.getRemoteSource(TRemote::class.java)

            if (localSource != null && remoteSource != null) {
                localSource.getAll() { status, linkedList ->
                    if (status == Source.Status.SUCCESS) {
                        syncHelper.setLocalList(linkedList as LinkedList<ModelLocal>)
                    } else {
                        syncTaskCounter.taskEnded()
                    }
                }
                remoteSource.getAll() { status, linkedList ->
                    if (status == Source.Status.SUCCESS) {
                        syncHelper.setRemoteList(linkedList as LinkedList<Model>)
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
                syncManagerIdentifier.synchManager
            ) { syncTaskCounter.taskEnded() }
            val localSource = SourceProvider.getLocalSource(syncManagerIdentifier.localClass)
            val remoteSource = SourceProvider.getRemoteSource(syncManagerIdentifier.remoteClass)

            if (localSource != null && remoteSource != null) {
                localSource.getAll() { status, linkedList ->
                    if (status == Source.Status.SUCCESS) {
                        syncHelper.setLocalList(linkedList)
                    } else {
                        syncTaskCounter.taskEnded()
                    }
                }
                remoteSource.getAll() { status, linkedList ->
                    if (status == Source.Status.SUCCESS) {
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
        val remoteClass: Class<Model>, val synchManager: SynchManager<TLocal, TRemote>
    ) {

        fun isSuitableFor(localClass: Class<*>, remoteClass: Class<*>) : Boolean {
            return (localClass == this.localClass && remoteClass == this.remoteClass)
        }
    }

    companion object : SingletonHolder<SyncService, Application>(::SyncService)
}