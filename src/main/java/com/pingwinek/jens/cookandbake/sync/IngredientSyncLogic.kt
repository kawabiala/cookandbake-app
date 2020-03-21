package com.pingwinek.jens.cookandbake.sync

import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote

class IngredientSyncLogic :
    SyncLogic<IngredientLocal, IngredientRemote> {
    override fun compare(local: IngredientLocal?, remote: IngredientRemote?): SyncLogic.SyncAction {
        return when {
            // CASE 1: doesn't make sense to call this method with both local and remote null
            local == null && remote == null -> SyncLogic.SyncAction.DO_NOTHING

            // CASE 2: we have a new remote ingredient, that we want to insert into local
            local == null -> SyncLogic.SyncAction.NEW_LOCAL

            // CASE 3: we have a new local ingredient, that we want to insert into remote
            remote == null && local.remoteId == null -> SyncLogic.SyncAction.NEW_REMOTE

            // CASE 4: remote deleted, i.e. existence of local.remoteid indicates, that there was a remote ingredient
            remote == null && local.remoteId != null -> SyncLogic.SyncAction.DELETE_LOCAL

            // CASE 5: local and remote do not refer to the same ingredient => BUG!!!
            local.remoteId != remote!!.id -> SyncLogic.SyncAction.DO_NOTHING

            // CASE 6: remote has more recent updates, so we update local
            local.lastModified < remote.lastModified -> SyncLogic.SyncAction.UPDATE_LOCAL

            // CASE 7: local has deleted flag, delete remotely
            local.flagAsDeleted -> SyncLogic.SyncAction.DELETE_REMOTE

            // CASE 8: local has more recent updates, so we update remote
            local.lastModified > remote.lastModified -> SyncLogic.SyncAction.UPDATE_REMOTE

            // There shouldn't be anything left
            else -> SyncLogic.SyncAction.DO_NOTHING
        }
    }

}