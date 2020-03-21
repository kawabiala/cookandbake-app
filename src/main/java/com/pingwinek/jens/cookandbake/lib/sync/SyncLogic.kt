package com.pingwinek.jens.cookandbake.lib.sync

interface SyncLogic<TLocal, TRemote> {

    enum class SyncAction {
        NEW_LOCAL,
        NEW_REMOTE,
        UPDATE_LOCAL,
        UPDATE_REMOTE,
        DELETE_LOCAL,
        DELETE_REMOTE,
        DO_NOTHING
    }

    fun compare(local: TLocal?, remote: TRemote?) : SyncAction
}