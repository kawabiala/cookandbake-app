package com.pingwinek.jens.cookandbake.sync

import com.pingwinek.jens.cookandbake.lib.sync.ModelLocal
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.lib.sync.SyncLogic
import com.pingwinek.jens.cookandbake.lib.sync.SyncManager
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.RecipeSourceLocal
import com.pingwinek.jens.cookandbake.sources.RecipeSourceRemote
import com.pingwinek.jens.cookandbake.utils.Locker
import java.util.*

class RecipeSyncManager(
    private val recipeSourceLocal: RecipeSourceLocal,
    private val recipeSourceRemote: RecipeSourceRemote,
    syncLogic: SyncLogic<RecipeLocal, RecipeRemote>
) : SyncManager<RecipeLocal, RecipeRemote>(recipeSourceLocal, recipeSourceRemote, syncLogic) {

    private val locker = Locker()

    /**
     * Recipe does not have a parent
     *
     * @param parentId
     * @return returns a promise with always status [Promise.Status.FAILURE]
     */
    override fun getLocalParent(parentId: Int): Promise<ModelLocal> {
        return Promise<ModelLocal>().apply {
            setResult(Promise.Status.FAILURE, null)
        }
    }

    /**
     * Recipe has no parent
     *
     * @param parentId
     * @return returns a promise, that has always status [Promise.Status.FAILURE]
     */
    override fun getLocalsByParent(parentId: Int): Promise<LinkedList<RecipeLocal>> {
        return Promise<LinkedList<RecipeLocal>>().apply {
            setResult(Promise.Status.FAILURE, null)
        }
    }

    /**
     * Recipe has no parent
     *
     * @param parentId
     * @return returns a promise, that has always status [Promise.Status.FAILURE]
     */
    override fun getRemotesByParent(parentId: Int): Promise<LinkedList<RecipeRemote>> {
        return Promise<LinkedList<RecipeRemote>>().apply {
            setResult(Promise.Status.FAILURE, null)
        }
    }

    override fun newLocal(remote: RecipeRemote, onDone: () -> Unit) {
        recipeSourceLocal.new(RecipeLocal.newFromRemote(remote))
            .setResultHandler{
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
        recipeSourceLocal.get(local.id)
            .setResultHandler{ localResult ->
                val localStatus = localResult.status
                val checkedRecipeLocal = localResult.value
                if (localStatus == Promise.Status.SUCCESS && checkedRecipeLocal?.remoteId == null) {

                    // Create the remote recipe
                    recipeSourceRemote.new(RecipeRemote.newFromLocal(local))
                        .setResultHandler{ remoteResult ->
                            val remoteStatus = remoteResult.status
                            val newRecipeRemote = remoteResult.value

                            /* If the remote recipe was successfully created
                            /  we add the remoteId to the local recipe
                             */
                            if (remoteStatus == Promise.Status.SUCCESS && newRecipeRemote != null) {
                                recipeSourceLocal.update(
                                    RecipeLocal(
                                        local.id,
                                        newRecipeRemote.id,
                                        local.title,
                                        local.description,
                                        local.instruction
                                    )
                                ).setResultHandler {
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
        ).setResultHandler {
            onDone()
        }
    }

    override fun updateRemote(local: RecipeLocal, remote: RecipeRemote, onDone: () -> Unit) {
        recipeSourceRemote.update(
            remote.getUpdated(local)
        ).setResultHandler {
            onDone()
        }
    }

    override fun deleteLocal(local: RecipeLocal, onDone: () -> Unit) {
        recipeSourceLocal.delete(local.id)
            .setResultHandler {
                onDone()
        }
    }

    override fun deleteRemote(remote: RecipeRemote, onDone: () -> Unit) {
        println("delete remote $remote")
        recipeSourceRemote.delete(remote.id)
            .setResultHandler{
                onDone()
        }
    }

}