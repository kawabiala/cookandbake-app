package com.pingwinek.jens.cookandbake.lib.sync

import android.util.Log
import java.util.*

abstract class SyncManager<TLocal: ModelLocal, TRemote: Model>(
    private val localSource: SourceLocal<TLocal>,
    private val remoteSource: Source<TRemote>,
    private val syncLogic: SyncLogic<TLocal, TRemote>
) {

    suspend fun syncEntry(local: TLocal?, remote: TRemote?) {
        Log.v(this::class.java.name, "syncEntry local: $local remote: $remote")
        when (syncLogic.compare(local, remote)) {
            SyncLogic.SyncAction.NEW_LOCAL -> {
                remote?.let { nonNullRemote ->
                    newLocal(nonNullRemote)
                }
            }
            SyncLogic.SyncAction.NEW_REMOTE -> {
                local?.let { nonNullLocal ->
                    newRemote(nonNullLocal)
                }
            }
            SyncLogic.SyncAction.UPDATE_LOCAL -> {
                local?.let { nonNullLocal ->
                    remote?.let { nonNullRemote ->
                        updateLocal(nonNullLocal, nonNullRemote)
                    }
                }
            }
            SyncLogic.SyncAction.UPDATE_REMOTE -> {
                local?.let { nonNullLocal ->
                    remote?.let { nonNullRemote ->
                        updateRemote(nonNullLocal, nonNullRemote)
                    }
                }
            }
            SyncLogic.SyncAction.DELETE_LOCAL -> {
                local?.let { nonNullLocal ->
                    deleteLocal(nonNullLocal)
                }
            }
            SyncLogic.SyncAction.DELETE_REMOTE -> {
                remote?.let { nonNullRemote ->
                    deleteRemote(nonNullRemote)
                }
            }
            SyncLogic.SyncAction.DO_NOTHING -> {}
        }
    }

    suspend fun syncEntry(localId: Int) {
        val local = getLocal(localId)
        if (local?.remoteId != null) {
            syncEntry(local, getRemote(local))
        } else if (local != null) {
            syncEntry(local, null)
        }
    }

    suspend fun syncByParentId(localParentId: Int) {
        val locals = getLocalsByParent(localParentId)
        val remoteId = getLocalParent(localParentId)?.remoteId
        val remotes = remoteId?.let { getRemotesByParent(it) } ?: LinkedList()
        sync(locals, remotes)
    }

    suspend fun sync() {
        val locals = getLocals()
        val remotes = getRemotes()
        sync(locals, remotes)
    }

    suspend fun sync(locals: List<TLocal>, remotes: List<TRemote>) {
        locals.forEach { local ->
            val remote = remotes.find {
                it.id == local.remoteId
            }
            syncEntry(local, remote)
        }
        remotes.forEach { remote ->
            if (locals.none {
                    it.remoteId == remote.id
                }) {
                syncEntry(null, remote)
            }
        }
    }

    suspend fun getLocal(id: Int) : TLocal? {
        return localSource.get(id)
    }

    suspend fun getRemote(id: Int) : TRemote? {
        return remoteSource.get(id)
    }

    @Suppress("Unused")
    suspend fun getLocal(remote: TRemote) : TLocal? {
        return localSource.getForRemoteId(remote.id)
    }

    suspend fun getRemote(local: TLocal) : TRemote? {
        return if (local.remoteId != null && local.remoteId is Int) {
            remoteSource.get(local.remoteId as Int)
        } else {
            null
        }
    }

    suspend fun getLocals() : LinkedList<TLocal> {
        return localSource.getAll()
    }

    suspend fun getRemotes() : LinkedList<TRemote> {
        return remoteSource.getAll()
    }

    abstract suspend fun getLocalParent(parentId: Int) : ModelLocal?
    abstract suspend fun getLocalsByParent(parentId: Int) : LinkedList<TLocal>
    abstract suspend fun getRemotesByParent(parentId: Int) : LinkedList<TRemote>

    abstract suspend fun newLocal(remote: TRemote)
    abstract suspend fun newRemote(local: TLocal)
    abstract suspend fun updateLocal(local: TLocal, remote: TRemote)
    abstract suspend fun updateRemote(local: TLocal, remote: TRemote)
    abstract suspend fun deleteLocal(local: TLocal)
    abstract suspend fun deleteRemote(remote: TRemote)
}