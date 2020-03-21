package com.pingwinek.jens.cookandbake.sync

import android.app.Application
import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.lib.sync.SynchManager
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.sources.IngredientSourceLocal
import com.pingwinek.jens.cookandbake.sources.IngredientSourceRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.lib.sync.Source
import com.pingwinek.jens.cookandbake.utils.Locker

class IngredientSyncManager(
    override val syncLogic: SyncLogic<IngredientLocal, IngredientRemote>,
    val application: Application
) : SynchManager<IngredientLocal, IngredientRemote> {

    private val recipeSourceLocal = RecipeSourceLocal.getInstance(application)
    private val ingredientSourceLocal = IngredientSourceLocal.getInstance(application)
    private val ingredientSourceRemote = IngredientSourceRemote.getInstance(application)

    private val locker = Locker()

    override fun newLocal(remote: IngredientRemote, onDone: () -> Unit) {
        recipeSourceLocal.toLocalId(remote.recipeId) { recipeLocalId ->
            recipeLocalId?.let {
                ingredientSourceLocal.new(
                    IngredientLocal.newFromRemote(
                        remote,
                        recipeLocalId
                    )
                ) { _, _ ->
                    onDone()
                }
            } ?: onDone()
        }
    }

    override fun newRemote(local: IngredientLocal, onDone: () -> Unit) {
        // Acquire lock in order to prevent creating the entry twice
        if (!locker.lock(local.id)) {
            onDone()
            return
        }

        /* Translate the local recipeId into remote recipeId:
        /  Look up the local recipe and retrieve its remoteId
        */
        recipeSourceLocal.toRemoteId(local.recipeId) { recipeId ->
            if (recipeId == null) {
                locker.unlock(local.id)
                onDone()
                return@toRemoteId
            }

            /* Check, if a new remote entry has been created, before we could acquire the lock
            /  Retrieve the local ingredient once more and check, if a remoteId has already been set
             */
            ingredientSourceLocal.get(local.id) { status, checkedIngredientLocal ->
                if (status == Source.Status.SUCCESS && checkedIngredientLocal?.remoteId == null) {

                    // Create the remote ingredient
                    ingredientSourceRemote.new(
                        IngredientRemote.newFromLocal(
                            local,
                            recipeId
                        )
                    ) { statusRemote, newIngredientRemote ->

                        /* If the remote ingredient was successfully created
                        /  we add the remoteId to the local ingredient
                         */
                        if (statusRemote == Source.Status.SUCCESS && newIngredientRemote != null) {
                            ingredientSourceLocal.update(
                                IngredientLocal(
                                    local.id,
                                    newIngredientRemote.id,
                                    local.recipeId,
                                    local.quantity,
                                    local.unity,
                                    local.name
                                )
                            ) { _, _ ->
                                locker.unlock(local.id)
                                onDone()
                            }
                        } else {
                            locker.unlock(local.id)
                            onDone()
                        }
                    }
                } else {
                    locker.unlock(local.id)
                    onDone()
                }
            }
        }
    }

    override fun updateLocal(local: IngredientLocal, remote: IngredientRemote, onDone: () -> Unit) {
        ingredientSourceLocal.update(
            local.getUpdated(remote)
        ) { _, _ ->
            onDone()
        }
    }

    override fun updateRemote(local: IngredientLocal, remote: IngredientRemote, onDone: () -> Unit) {
        ingredientSourceRemote.update(
            remote.getUpdated(local)
        ) { _, _ ->
            onDone()
        }
    }

    override fun deleteLocal(local: IngredientLocal, onDone: () -> Unit) {
        ingredientSourceLocal.delete(local.id) {
            onDone()
        }
    }

    override fun deleteRemote(remote: IngredientRemote, onDone: () -> Unit) {
        ingredientSourceRemote.delete(remote.id) {
            onDone()
        }
    }
}