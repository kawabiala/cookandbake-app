package com.pingwinek.jens.cookandbake.sync

import com.pingwinek.jens.cookandbake.lib.sync.ModelLocal
import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.lib.sync.SyncManager
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.sources.IngredientSourceLocal
import com.pingwinek.jens.cookandbake.sources.IngredientSourceRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class IngredientSyncManager(
    private val recipeSourceLocal: RecipeSourceLocal,
    private val ingredientSourceLocal: IngredientSourceLocal,
    private val ingredientSourceRemote: IngredientSourceRemote,
    syncLogic: SyncLogic<IngredientLocal, IngredientRemote>
) : SyncManager<IngredientLocal, IngredientRemote>(ingredientSourceLocal, ingredientSourceRemote, syncLogic) {

    override suspend fun getLocalParent(parentId: Int): ModelLocal? {
        return recipeSourceLocal.get(parentId)
    }

    override suspend fun getLocalsByParent(parentId: Int): LinkedList<IngredientLocal> {
        return ingredientSourceLocal.getAllForRecipeId(parentId)
    }

    override suspend fun getRemotesByParent(parentId: Int): LinkedList<IngredientRemote> {
        return ingredientSourceRemote.getAllForRecipeId(parentId)
    }

    override suspend fun newLocal(remote: IngredientRemote) {
        val localRecipeId = recipeSourceLocal.toLocalId(remote.recipeId) ?: return
        ingredientSourceLocal.new(IngredientLocal.newFromRemote(
                remote,
                localRecipeId
            ))
    }

    override suspend fun newRemote(local: IngredientLocal) {

        // Make sure that remote ingredient is not created twice
        Mutex().withLock {
            // Check if remote ingredient has been created in the meantime
            if (ingredientSourceLocal.get(local.id)?.remoteId != null) return

            // Retrieve remote recipe id
            val remoteRecipeId = recipeSourceLocal.toRemoteId(local.recipeId) ?: return

            // Create remote ingredient from local ingredient and remote recipe id
            val newIngredient = ingredientSourceRemote.new(
                IngredientRemote.newFromLocal(local, remoteRecipeId)) ?: return

            ingredientSourceLocal.update(IngredientLocal(
                local.id,
                newIngredient.id,
                local.recipeId,
                local.quantity,
                local.quantityVerbal,
                local.unity,
                local.name
            ))
        }
    }

    override suspend fun updateLocal(local: IngredientLocal, remote: IngredientRemote) {
        ingredientSourceLocal.update(local.getUpdated(remote))
    }

    override suspend fun updateRemote(local: IngredientLocal, remote: IngredientRemote) {
        ingredientSourceRemote.update(remote.getUpdated(local))
    }

    override suspend fun deleteLocal(local: IngredientLocal) {
        ingredientSourceLocal.delete(local.id)
    }

    override suspend fun deleteRemote(remote: IngredientRemote) {
        ingredientSourceRemote.delete(remote.id)
    }
}