package com.pingwinek.jens.cookandbake.sync

import com.pingwinek.jens.cookandbake.lib.sync.ModelLocal
import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.lib.sync.SyncManager
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.sources.RecipeSourceRemote
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class RecipeSyncManager(
    private val recipeSourceLocal: RecipeSourceLocal,
    private val recipeSourceRemote: RecipeSourceRemote,
    syncLogic: SyncLogic<RecipeLocal, RecipeRemote>
) : SyncManager<RecipeLocal, RecipeRemote>(recipeSourceLocal, recipeSourceRemote, syncLogic) {

    /**
     * Recipe does not have a parent
     *
     * @param parentId
     * @return returns null
     */
    override suspend fun getLocalParent(parentId: Int): ModelLocal? {
        return null
    }

    /**
     * Recipe has no parent
     *
     * @param parentId
     * @return returns an empty List
     */
    override suspend fun getLocalsByParent(parentId: Int): LinkedList<RecipeLocal> {
        return LinkedList<RecipeLocal>()
    }

    /**
     * Recipe has no parent
     *
     * @param parentId
     * @return returns an empty List
     */
    override suspend fun getRemotesByParent(parentId: Int): LinkedList<RecipeRemote> {
        return LinkedList<RecipeRemote>()
    }

    override suspend fun newLocal(remote: RecipeRemote) {
        recipeSourceLocal.new(RecipeLocal.newFromRemote(remote))
    }

    override suspend fun newRemote(local: RecipeLocal) {

        // Make sure, that new remote is not created twice
        Mutex().withLock {
            // Check, if new remote has been created in the meantime
            if (recipeSourceLocal.get(local.id)?.remoteId != null) return

            val newRecipe = recipeSourceRemote.new(RecipeRemote.newFromLocal(local)) ?: return
            recipeSourceLocal.update(RecipeLocal(
                local.id,
                newRecipe.id,
                local.title,
                local.description,
                local.instruction
                )
            )
        }
    }

    override suspend fun updateLocal(local: RecipeLocal, remote: RecipeRemote) {
        recipeSourceLocal.update(local.getUpdated(remote))
    }

    override suspend fun updateRemote(local: RecipeLocal, remote: RecipeRemote) {
        recipeSourceRemote.update(remote.getUpdated(local))
    }

    override suspend fun deleteLocal(local: RecipeLocal) {
        recipeSourceLocal.delete(local.id)
    }

    override suspend fun deleteRemote(remote: RecipeRemote) {
        recipeSourceRemote.delete(remote.id)
    }

}