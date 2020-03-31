package com.pingwinek.jens.cookandbake.sync

import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote

class RecipeSyncLogic :
    SyncLogic<RecipeLocal, RecipeRemote> {

    override fun compare(local: RecipeLocal?, remote: RecipeRemote?): SyncLogic.SyncAction {
        return when {
            // CASE 1: doesn't make sense to call this method with both local and remote null
            local == null && remote == null -> SyncLogic.SyncAction.DO_NOTHING

            // CASE 2: we have a new remote recipe, that we want to insert into local
            local == null -> SyncLogic.SyncAction.NEW_LOCAL

            // CASE 3: local has deleted flag, delete remotely
            local.flagAsDeleted -> SyncLogic.SyncAction.DELETE_REMOTE

            // CASE 4: we have a new local recipe, that we want to insert into remote
            remote == null && local.remoteId == null -> SyncLogic.SyncAction.NEW_REMOTE

            // CASE 5: remote deleted, i.e. existence of local.remoteid indicates, that there was a remote recipe
            remote == null && local.remoteId != null -> SyncLogic.SyncAction.DELETE_LOCAL

            // CASE 6: local and remote do not refer to the same recipe => BUG!!!
            local.remoteId != remote!!.id -> SyncLogic.SyncAction.DO_NOTHING

            // CASE 7: remote has more recent updates, so we update local
            local.lastModified <= remote.lastModified -> SyncLogic.SyncAction.UPDATE_LOCAL

            // CASE 8: local has more recent updates, so we update remote
            local.lastModified > remote.lastModified -> SyncLogic.SyncAction.UPDATE_REMOTE

            // There shouldn't be anything left
            else -> SyncLogic.SyncAction.DO_NOTHING
        }
    }

}