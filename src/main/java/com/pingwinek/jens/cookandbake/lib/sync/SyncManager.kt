package com.pingwinek.jens.cookandbake.lib.sync

abstract class SyncManager<TLocal: ModelLocal, TRemote: Model> {

    abstract val syncLogic: SyncLogic<TLocal, TRemote>

    fun sync(local: TLocal?, remote: TRemote?, onDone: () -> Unit) {
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

    abstract fun newLocal(remote: TRemote, onDone: () -> Unit)
    abstract fun newRemote(local: TLocal, onDone: () -> Unit)
    abstract fun updateLocal(local: TLocal, remote: TRemote, onDone: () -> Unit)
    abstract fun updateRemote(local: TLocal, remote: TRemote, onDone: () -> Unit)
    abstract fun deleteLocal(local: TLocal, onDone: () -> Unit)
    abstract fun deleteRemote(remote: TRemote, onDone: () -> Unit)
}