package com.pingwinek.jens.cookandbake.lib.sync

import com.pingwinek.jens.cookandbake.utils.CallbackLoopCounter
import java.util.*

abstract class SyncManager<TLocal: ModelLocal, TRemote: Model>(
    private val localSource: SourceLocal<TLocal>,
    private val remoteSource: Source<TRemote>,
    private val syncLogic: SyncLogic<TLocal, TRemote>
) {

    fun syncEntry(local: TLocal?, remote: TRemote?, onDone: () -> Unit) {
        when (syncLogic.compare(local, remote)) {
            SyncLogic.SyncAction.NEW_LOCAL -> {
                remote?.let { nonNullRemote ->
                    newLocal(nonNullRemote, onDone)
                } ?: onDone()
            }
            SyncLogic.SyncAction.NEW_REMOTE -> {
                local?.let { nonNullLocal ->
                    newRemote(nonNullLocal, onDone)
                } ?: onDone()
            }
            SyncLogic.SyncAction.UPDATE_LOCAL -> {
                local?.let { nonNullLocal ->
                    remote?.let { nonNullRemote ->
                        updateLocal(nonNullLocal, nonNullRemote, onDone)
                    } ?: onDone()
                } ?: onDone()
            }
            SyncLogic.SyncAction.UPDATE_REMOTE -> {
                local?.let { nonNullLocal ->
                    remote?.let { nonNullRemote ->
                        updateRemote(nonNullLocal, nonNullRemote, onDone)
                    } ?: onDone()
                } ?: onDone()
            }
            SyncLogic.SyncAction.DELETE_LOCAL -> {
                local?.let { nonNullLocal ->
                    deleteLocal(nonNullLocal, onDone)
                } ?: onDone()
            }
            SyncLogic.SyncAction.DELETE_REMOTE -> {
                remote?.let { nonNullRemote ->
                    deleteRemote(nonNullRemote, onDone)
                } ?: onDone()
            }
            SyncLogic.SyncAction.DO_NOTHING -> { onDone() }
        }
    }

    fun syncEntry(localId: Int, onDone: () -> Unit) {
        val syncTaskCounter = CallbackLoopCounter(onDone)
        syncTaskCounter.taskStarted()

        getLocal(localId).setResultHandler { localResult ->
            val local = localResult.value
            if (localResult.status == Promise.Status.SUCCESS && local != null) {
                getRemote(local).setResultHandler { remoteResult ->
                    val remote = remoteResult.value
                    if (remoteResult.status == Promise.Status.SUCCESS) {
                        syncEntry(local, remote, onDone)
                    } else {
                        onDone()
                    }
                }
            } else {
                onDone()
            }
        }
    }

    @Suppress("Unchecked_Cast")
    fun syncByParentId(localParentId: Int, onDone: () -> Unit) {
        val syncTaskCounter = CallbackLoopCounter(onDone)
        syncTaskCounter.taskStarted()
        val syncHelper = SyncHelper(this as SyncManager<ModelLocal, Model>) { syncTaskCounter.taskEnded() }

        getLocalsByParent(localParentId).setResultHandler { result ->
            if (result.status == Promise.Status.SUCCESS) {
                syncHelper.setLocalList(result.value as LinkedList<ModelLocal>)
            } else {
                syncTaskCounter.taskEnded()
            }
        }
        getLocalParent(localParentId).setResultHandler { parentResult ->
            val remoteId = parentResult.value?.remoteId
            if (parentResult.status == Promise.Status.SUCCESS && remoteId != null) {
                getRemotesByParent(remoteId).setResultHandler { result ->
                    if (result.status == Promise.Status.SUCCESS) {
                        syncHelper.setRemoteList(result.value as LinkedList<Model>)
                    } else {
                        syncTaskCounter.taskEnded()
                    }
                }
            } else {
                syncTaskCounter.taskEnded()
            }
        }
    }

    @Suppress("Unchecked_Cast")
    fun sync(onDone: () -> Unit) {
        val syncTaskCounter = CallbackLoopCounter(onDone)
        syncTaskCounter.taskStarted()
        val syncHelper = SyncHelper(this as SyncManager<ModelLocal, Model>) { syncTaskCounter.taskEnded() }

        getLocals().setResultHandler { result ->
            if (result.status == Promise.Status.SUCCESS) {
                syncHelper.setLocalList(result.value as LinkedList<ModelLocal>)
            } else {
                syncTaskCounter.taskEnded()
            }
        }
        getRemotes().setResultHandler { result ->
            if (result.status == Promise.Status.SUCCESS) {
                syncHelper.setRemoteList(result.value as LinkedList<Model>)
            } else {
                syncTaskCounter.taskEnded()
            }
        }
    }

    fun getLocal(id: Int) : Promise<TLocal> {
        return localSource.get(id)
    }

    fun getRemote(id: Int) : Promise<TRemote> {
        return remoteSource.get(id)
    }

    @Suppress("Unused")
    fun getLocal(remote: TRemote) : Promise<TLocal> {
        return localSource.getForRemoteId(remote.id)
    }

    fun getRemote(local: TLocal) : Promise<TRemote> {
        return if (local.remoteId != null && local.remoteId is Int) {
            remoteSource.get(local.remoteId as Int)
        } else {
            Promise<TRemote>().apply {
                setResult(Promise.Status.FAILURE, null)
            }
        }
    }

    fun getLocals() : Promise<LinkedList<TLocal>> {
        return localSource.getAll()
    }

    fun getRemotes() : Promise<LinkedList<TRemote>> {
        return remoteSource.getAll()
    }

    abstract fun getLocalParent(parentId: Int) : Promise<ModelLocal>
    abstract fun getLocalsByParent(parentId: Int) : Promise<LinkedList<TLocal>>
    abstract fun getRemotesByParent(parentId: Int) : Promise<LinkedList<TRemote>>

    abstract fun newLocal(remote: TRemote, onDone: () -> Unit)
    abstract fun newRemote(local: TLocal, onDone: () -> Unit)
    abstract fun updateLocal(local: TLocal, remote: TRemote, onDone: () -> Unit)
    abstract fun updateRemote(local: TLocal, remote: TRemote, onDone: () -> Unit)
    abstract fun deleteLocal(local: TLocal, onDone: () -> Unit)
    abstract fun deleteRemote(remote: TRemote, onDone: () -> Unit)
}