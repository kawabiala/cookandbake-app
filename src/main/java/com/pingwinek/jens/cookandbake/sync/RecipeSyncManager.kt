package com.pingwinek.jens.cookandbake.sync

import android.app.Application
import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.lib.sync.SynchManager
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.sources.RecipeSourceRemote
import com.pingwinek.jens.cookandbake.lib.sync.Source
import com.pingwinek.jens.cookandbake.utils.Locker

class RecipeSyncManager(
    override val syncLogic: SyncLogic<RecipeLocal, RecipeRemote>,
    val application: Application
) : SynchManager<RecipeLocal, RecipeRemote> {

    private val recipeSourceLocal = RecipeSourceLocal.getInstance(application)
    private val recipeSourceRemote = RecipeSourceRemote.getInstance(application)

    private val locker = Locker()

    override fun newLocal(remote: RecipeRemote, onDone: () -> Unit) {
        recipeSourceLocal.new(RecipeLocal.newFromRemote(remote)) { _, _ ->
            onDone()
        }
    }

    override fun newRemote(local: RecipeLocal, onDone: () -> Unit) {
        // Acquire lock in order to prevent creating the entry twice
        if (!locker.lock(local.id)) {
            onDone()
            return
        }

        /* Check, if a new remote entry has been created, before we could acquire the lock
        /  Retrieve the local recipe once more and check, if a remoteId has already been set
         */
        recipeSourceLocal.get(local.id) { status, checkedRecipeLocal ->
            if (status == Source.Status.SUCCESS && checkedRecipeLocal?.remoteId == null) {

                // Create the remote recipe
                recipeSourceRemote.new(RecipeRemote.newFromLocal(local)) { statusRemote, newRecipeRemote ->

                    /* If the remote recipe was successfully created
                    /  we add the remoteId to the local recipe
                     */
                    if (statusRemote == Source.Status.SUCCESS && newRecipeRemote != null) {
                        recipeSourceLocal.update(
                            RecipeLocal(
                                local.id,
                                newRecipeRemote.id,
                                local.title,
                                local.description,
                                local.instruction
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

    override fun updateLocal(local: RecipeLocal, remote: RecipeRemote, onDone: () -> Unit) {
        recipeSourceLocal.update(
            local.getUpdated(remote)
        ) { _, _ ->
            onDone()
        }
    }

    override fun updateRemote(local: RecipeLocal, remote: RecipeRemote, onDone: () -> Unit) {
        recipeSourceRemote.update(
            remote.getUpdated(local)
        ) { _, _ ->
            onDone()
        }
    }

    override fun deleteLocal(local: RecipeLocal, onDone: () -> Unit) {
        recipeSourceLocal.delete(local.id) {
            onDone()
        }
    }

    override fun deleteRemote(remote: RecipeRemote, onDone: () -> Unit) {
        recipeSourceRemote.delete(remote.id) {
            onDone()
        }
    }

}