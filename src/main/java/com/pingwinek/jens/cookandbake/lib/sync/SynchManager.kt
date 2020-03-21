package com.pingwinek.jens.cookandbake.lib.sync

interface SynchManager<TLocal: ModelLocal, TRemote: Model> {

    val syncLogic: SyncLogic<TLocal, TRemote>

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

    fun newLocal(remote: TRemote, onDone: () -> Unit)
    fun newRemote(local: TLocal, onDone: () -> Unit)
    fun updateLocal(local: TLocal, remote: TRemote, onDone: () -> Unit)
    fun updateRemote(local: TLocal, remote: TRemote, onDone: () -> Unit)
    fun deleteLocal(local: TLocal, onDone: () -> Unit)
    fun deleteRemote(remote: TRemote, onDone: () -> Unit)
}