package com.pingwinek.jens.cookandbake.sync

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import org.junit.Test
import java.util.*

class RecipeSyncLogicTest {

    private val recipeSyncLogic = RecipeSyncLogic()

    private val nullRecipeLocal: RecipeLocal? = null
    private val recipeLocalWithRemoteId = RecipeLocal(1, 2, "Recipe Local 1", null, null)
    private val recipeLocalWithoutRemoteId = RecipeLocal(2, null, "Recipe Local 2", null, null)
    private val recipeLocalFlaggedAsDeleted = RecipeLocal(1, 2, "Recipe Local Flagged as Deleted", null, null, Date().time, true)

    private val nullRecipeRemote: RecipeRemote? =  null
    private val recipeRemote = RecipeRemote.fromLocal(recipeLocalWithRemoteId)

    @Test
    fun compare() {
        // Case 1: local and remote are null -> do nothing
        assert(recipeSyncLogic.compare(nullRecipeLocal, nullRecipeRemote) == SyncLogic.SyncAction.DO_NOTHING)

        // Case 2: only local is null -> new local
        assert(recipeSyncLogic.compare(nullRecipeLocal, recipeRemote) == SyncLogic.SyncAction.NEW_LOCAL)

        // Case 3: local is flagged as deleted -> delete remote
        assert(recipeSyncLogic.compare(recipeLocalFlaggedAsDeleted, recipeRemote) == SyncLogic.SyncAction.DELETE_REMOTE)
        assert(recipeSyncLogic.compare(recipeLocalFlaggedAsDeleted, nullRecipeRemote) == SyncLogic.SyncAction.DELETE_REMOTE)

        // Case 4: only remote is null and remoteId of local is null -> new remote
        assert(recipeSyncLogic.compare(recipeLocalWithoutRemoteId, nullRecipeRemote) == SyncLogic.SyncAction.NEW_REMOTE)

        // Case 5: only remote is null and remoteId of local is not null -> delete local
        assert(recipeSyncLogic.compare(recipeLocalWithRemoteId, nullRecipeRemote) == SyncLogic.SyncAction.DELETE_LOCAL)

        // Case 6: id of remote and remoteId of local don't match -> do nothing
        assert(recipeSyncLogic.compare(recipeLocalWithoutRemoteId, recipeRemote) == SyncLogic.SyncAction.DO_NOTHING)

        // Case 7: remote is more recent -> update local
        val recipeRemote = mock<RecipeRemote>()
        whenever(recipeRemote.id).thenReturn(2)
        Thread.sleep(1)
        whenever(recipeRemote.lastModified).thenReturn(Date().time)
        assert(recipeSyncLogic.compare(recipeLocalWithRemoteId, recipeRemote) == SyncLogic.SyncAction.UPDATE_LOCAL)

        // Case 8: local is more recent -> update remote
        Thread.sleep(1)
        val recipeLocal = RecipeLocal(1, 2, "Recipe Local Most Recent", null, null)
        assert(recipeSyncLogic.compare(recipeLocal, recipeRemote) == SyncLogic.SyncAction.UPDATE_REMOTE)

        // Case 9: local and remote have same timestamp
        val lastmodified = Date().time
        val local = mock<RecipeLocal>()
        val remote = mock<RecipeRemote>()
        whenever(local.remoteId).thenReturn(1)
        whenever(remote.id).thenReturn(1)
        whenever(local.lastModified).thenReturn(lastmodified)
        whenever(remote.lastModified).thenReturn(lastmodified)
        assert(recipeSyncLogic.compare(local, remote) == SyncLogic.SyncAction.DO_NOTHING)
    }
}