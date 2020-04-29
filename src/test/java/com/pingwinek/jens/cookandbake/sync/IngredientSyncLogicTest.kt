package com.pingwinek.jens.cookandbake.sync

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import org.junit.Test
import java.util.*

class IngredientSyncLogicTest {

    private val ingredientSyncLogic = IngredientSyncLogic()

    private val nullIngredientLocal: IngredientLocal? = null
    private val ingredientLocalWithRemoteId = IngredientLocal(1, 2, 3, null, null, null, "Ingredient Local 1")
    private val ingredientLocalWithoutRemoteId = IngredientLocal(2, null, 3, null, null, null, "Ingredient Local 2")
    private val ingredientLocalFlaggedAsDeleted = IngredientLocal(1, 2, 3, null, null, null, "Ingredient Local Deleted", Date().time, true)

    private val nullIngredientRemote: IngredientRemote? = null
    private val ingredientRemote = IngredientRemote.fromLocal(ingredientLocalWithRemoteId, 3)

    @Test
    fun compare() {
        // Case 1: local and remote are null -> do nothing
        assert(ingredientSyncLogic.compare(nullIngredientLocal, nullIngredientRemote) == SyncLogic.SyncAction.DO_NOTHING)

        // Case 2: only local is null -> new local
        assert(ingredientSyncLogic.compare(nullIngredientLocal, ingredientRemote) == SyncLogic.SyncAction.NEW_LOCAL)

        // Case 3: local is flagged as deleted -> delete remote
        assert(ingredientSyncLogic.compare(ingredientLocalFlaggedAsDeleted, ingredientRemote) == SyncLogic.SyncAction.DELETE_REMOTE)
        assert(ingredientSyncLogic.compare(ingredientLocalFlaggedAsDeleted, nullIngredientRemote) == SyncLogic.SyncAction.DELETE_REMOTE)

        // Case 4: only remote is null, and local has remoteId -> delete local
        assert(ingredientSyncLogic.compare(ingredientLocalWithRemoteId, nullIngredientRemote) == SyncLogic.SyncAction.DELETE_LOCAL)

        // Case 5: only remote is null, and local has no remoteId -> new remote
        assert(ingredientSyncLogic.compare(ingredientLocalWithoutRemoteId, nullIngredientRemote) == SyncLogic.SyncAction.NEW_REMOTE)

        // Case 6: id of remote and remoteId of local don't match -> do nothing
        assert(ingredientSyncLogic.compare(ingredientLocalWithoutRemoteId, ingredientRemote) == SyncLogic.SyncAction.DO_NOTHING)

        // Case 7: remote is more recent -> update local
        val ingredientRemote = mock<IngredientRemote>()
        whenever(ingredientRemote.id).thenReturn(2)
        Thread.sleep(1)
        whenever(ingredientRemote.lastModified).thenReturn(Date().time)
        assert(ingredientSyncLogic.compare(ingredientLocalWithRemoteId, ingredientRemote) == SyncLogic.SyncAction.UPDATE_LOCAL)

        // Case 8: local is more recent -> update remote
        Thread.sleep(1)
        val ingredientLocal = IngredientLocal(1, 2, 3, null, null, null, "Ingredient Local Most Recent")
        assert(ingredientSyncLogic.compare(ingredientLocal, ingredientRemote) == SyncLogic.SyncAction.UPDATE_REMOTE)

        // Case 9: local and remote have same timestamp
        val lastModified = Date().time
        val local = mock<IngredientLocal>()
        val remote = mock<IngredientRemote>()
        whenever(local.remoteId).thenReturn(1)
        whenever(remote.id).thenReturn(1)
        whenever(local.lastModified).thenReturn(lastModified)
        whenever(remote.lastModified).thenReturn(lastModified)
        assert(ingredientSyncLogic.compare(local, remote) == SyncLogic.SyncAction.DO_NOTHING)
    }
}