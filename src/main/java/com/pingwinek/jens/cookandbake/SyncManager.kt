package com.pingwinek.jens.cookandbake

import android.app.Application
import com.pingwinek.jens.cookandbake.models.*
import com.pingwinek.jens.cookandbake.sources.*
import java.util.*

class SyncManager private constructor(val application: Application) {

    private val recipeSourceRemote = RecipeSourceRemote.getInstance(application)
    private val recipeSourceLocal = RecipeSourceLocal.getInstance(application)

    private val ingredientSourceRemote = IngredientSourceRemote.getInstance(application)
    private var ingredientSourceLocal = IngredientSourceLocal.getInstance(application)

    class SyncTaskCounter(private val onAllEnded: () -> Unit) {

        private var counter = 0

        fun taskStarted(): SyncTaskCounter {
            counter++
            return this
        }

        fun taskEnded(): SyncTaskCounter {
            counter--
            if (counter == 0) {
                onAllEnded()
            }
            return this
        }
    }

    fun syncRecipes(onDone: () -> Unit) {
        val syncTaskCounter = SyncTaskCounter(onDone).taskStarted()
        recipeSourceRemote.getAll { _, recipesRemote ->
            recipeSourceLocal.getAll { _, recipesLocal ->
                compareRecipes(recipesLocal, recipesRemote, syncTaskCounter)
            }
        }
        syncTaskCounter.taskEnded()
    }

    fun syncRecipe(recipeId: Int, onDone: () -> Unit) {
        val syncTaskCounter = SyncTaskCounter(onDone).taskStarted()
        recipeSourceLocal.get(recipeId) { status, recipeLocal ->
            if (status == Source.Status.SUCCESS && recipeLocal != null) {
                val idOfRemote = recipeLocal.remoteId
                if (idOfRemote != null) {
                    recipeSourceRemote.get(idOfRemote) { _, recipeRemote ->
                        compareRecipe(recipeLocal, recipeRemote, syncTaskCounter)
                    }
                } else {
                    compareRecipe(recipeLocal, null, syncTaskCounter)
                }
            } else {
                // if local is null, we can't get remote either
            }
        }
        syncTaskCounter.taskEnded()
    }

    private fun compareRecipes(
        recipesLocal: LinkedList<RecipeLocal>,
        recipesRemote: LinkedList<RecipeRemote>,
        syncTaskCounter: SyncTaskCounter
    ) {
        syncTaskCounter.taskStarted()
        if (recipesLocal.size > 0) {
            recipesLocal.forEach { recipeLocal ->
                compareRecipe(
                    recipeLocal,
                    recipesRemote.find { recipeRemote ->
                        recipeRemote.rowid == recipeLocal.remoteId
                    },
                    syncTaskCounter
                )
            }
        }
        if (recipesRemote.size > 0) {
            recipesRemote.forEach { recipeRemote ->
                compareRecipe(
                    recipesLocal.find { recipeLocal ->
                        recipeLocal.remoteId == recipeRemote.rowid
                    },
                    recipeRemote,
                    syncTaskCounter
                )
            }
        }
        syncTaskCounter.taskEnded()
    }

    private fun compareRecipe(
        recipeLocal: RecipeLocal?,
        recipeRemote: RecipeRemote?,
        syncTaskCounter: SyncTaskCounter
    ) {
        syncTaskCounter.taskStarted()
        when {
            // CASE 1: doesn't make sense to call this method with both local and remote null
            recipeLocal == null && recipeRemote == null -> {
                syncTaskCounter.taskEnded()
                // do nothing
            }

            // CASE 2: local null -> add new remote locally
            recipeLocal == null -> {
                recipeSourceLocal.new(RecipeLocal.fromRemote(recipeRemote!!)) { _, _ ->
                    syncTaskCounter.taskEnded()
                }
            }

            // CASE 3: remote null and local.remoteId null -> add remotely
            recipeRemote == null && recipeLocal.remoteId == null -> {
                recipeSourceRemote.new(RecipeRemote.newFromLocal(recipeLocal)) { status, newRecipeRemote ->
                    if (status == Source.Status.SUCCESS && newRecipeRemote != null) {
                        recipeSourceLocal.update(
                            RecipeLocal(
                                recipeLocal.rowid,
                                newRecipeRemote.rowid,
                                recipeLocal.title,
                                recipeLocal.description,
                                recipeLocal.instruction
                            )
                        ) { _, _ ->
                            syncTaskCounter.taskEnded()
                        }
                    } else {
                        syncTaskCounter.taskEnded()
                    }
                }
            }

            // CASE 4: remote null and local.remoteId not null -> delete locally
            recipeRemote == null -> {
                recipeSourceLocal.delete(recipeLocal.rowid) {
                    syncTaskCounter.taskEnded()
                }
            }

            // CASE 5: local and remote do not refer to the same recipe => BUG!!!
            recipeLocal.remoteId != recipeRemote.rowid -> {
                // do nothing
            }

            // CASE 6: remote more recent -> update locally
            recipeLocal.lastModified < recipeRemote.lastModified -> {
                recipeSourceLocal.update(
                    recipeLocal.getUpdated(
                        recipeRemote.title,
                        recipeRemote.description,
                        recipeRemote.instruction
                    )
                ) { _, _ ->
                    syncTaskCounter.taskEnded()
                }
            }

            // CASE 7: check
            recipeLocal.lastModified >= recipeRemote.lastModified -> {
                recipeSourceRemote.update(
                    recipeRemote.getUpdated(
                        recipeLocal.title,
                        recipeLocal.description,
                        recipeLocal.instruction
                    )
                ) { _, _ ->
                    syncTaskCounter.taskEnded()
                }
            }

            // there shouldn't be anything left
            else -> {
                syncTaskCounter.taskEnded()
            }
        }
        syncTaskCounter.taskEnded()
    }


    fun syncIngredients(localRecipeId: Int, onDone: () -> Unit) {
        val syncTaskCounter = SyncTaskCounter(onDone).taskStarted()
        retrieveRemoteRecipeId(localRecipeId) { remoteRecipeId ->
            if (remoteRecipeId != null) {
                ingredientSourceRemote.getAllForRecipeId(remoteRecipeId) { _, ingredientsRemote ->
                    ingredientSourceLocal.getAllForRecipeId(localRecipeId) { _, ingredientsLocal ->
                        compareIngredients(ingredientsLocal, ingredientsRemote, syncTaskCounter)
                    }
                }
            } else {
                ingredientSourceLocal.getAllForRecipeId(localRecipeId) { _, ingredientsLocal ->
                    compareIngredients(ingredientsLocal, LinkedList(), syncTaskCounter)
                }
            }
        }
        syncTaskCounter.taskEnded()
    }

    fun syncIngredient(ingredientId: Int, onDone: () -> Unit) {
        val syncTaskCounter = SyncTaskCounter(onDone).taskStarted()
        ingredientSourceLocal.get(ingredientId) { status, ingredientLocal ->
            if (status == Source.Status.SUCCESS && ingredientLocal != null) {
                val idOfRemote = ingredientLocal.remoteId
                if (idOfRemote != null) {
                    ingredientSourceRemote.get(idOfRemote) { _, ingredientRemote ->
                        // if local has remoteid, but remote is null, remote has been deleted -> will be handled in compareIngredient
                        compareIngredient(ingredientLocal, ingredientRemote, syncTaskCounter)
                    }
                } else {
                    // if local has no remoteid, local is new and not yet been synced to remote
                    compareIngredient(ingredientLocal, null, syncTaskCounter)
                }
            } else {
                // if local is null, we can't get remote either
            }
        }
        syncTaskCounter.taskEnded()
    }

    private fun compareIngredients(
        ingredientsLocal: LinkedList<IngredientLocal>,
        ingredientsRemote: LinkedList<IngredientRemote>,
        syncTaskCounter: SyncTaskCounter
    ) {
        syncTaskCounter.taskStarted()
        if (ingredientsLocal.size > 0) {
            ingredientsLocal.forEach { ingredientLocal ->
                compareIngredient(
                    ingredientLocal,
                    ingredientsRemote.find { _ingredientRemote ->
                        ingredientLocal.remoteId == _ingredientRemote.id
                    },
                    syncTaskCounter
                )
            }
        }
        if (ingredientsRemote.size > 0) {
            ingredientsRemote.forEach { ingredientRemote ->
                compareIngredient(
                    ingredientsLocal.find { ingredientLocal ->
                        ingredientLocal.remoteId == ingredientRemote.id
                    },
                    ingredientRemote,
                    syncTaskCounter
                )
            }
        }
        syncTaskCounter.taskEnded()
    }

    private fun compareIngredient(
        ingredientLocal: IngredientLocal?,
        ingredientRemote: IngredientRemote?,
        syncTaskCounter: SyncTaskCounter
    ) {
        syncTaskCounter.taskStarted()
        when {
            // CASE 1: doesn't make sense to call this method with both local and remote null
            ingredientLocal == null && ingredientRemote == null -> {
                // do nothing
                syncTaskCounter.taskEnded()
            }

            // CASE 2: we have a new remote ingredient, that we want to insert into local
            ingredientLocal == null -> {
                // we need the id of local recipe (!) to generate a local ingredient
                retrieveLocalRecipeIdFromRemoteIngredient(ingredientRemote!!) { recipeId ->
                    if (recipeId != null) {
                        ingredientSourceLocal.new(
                            IngredientLocal.newFromRemote(
                                ingredientRemote,
                                recipeId
                            )
                        ) { _, _ ->
                            syncTaskCounter.taskEnded()
                        }
                    } else {
                        // ignore
                        syncTaskCounter.taskEnded()
                    }
                }
            }

            // CASE 3: we have a new local ingredient, that we want to insert into remote
            ingredientRemote == null && ingredientLocal.remoteId == null -> {
                // the remoteid of the local recipe (!) is the recipeId for the remote ingredient we want to create
                retrieveRemoteRecipeIdFromLocalIngredient(ingredientLocal) { recipeId ->
                    if (recipeId != null) {
                        ingredientSourceRemote.new(
                            IngredientRemote.newFromLocal(
                                ingredientLocal,
                                recipeId
                            )
                        ) { status, newIngredientRemote ->
                            if (status == Source.Status.SUCCESS && newIngredientRemote != null) {
                                ingredientSourceLocal.update(
                                    IngredientLocal(
                                        ingredientLocal.id,
                                        newIngredientRemote.id,
                                        ingredientLocal.recipeId,
                                        ingredientLocal.quantity,
                                        ingredientLocal.unity,
                                        ingredientLocal.name
                                    )
                                ) { _, _ ->
                                    syncTaskCounter.taskEnded()
                                }
                            } else {
                                syncTaskCounter.taskEnded()
                            }
                        }
                    } else {
                        // ignore
                        syncTaskCounter.taskEnded()
                    }
                }
            }

            // CASE 4: remote deleted, i.e. existence of local.remoteid indicates, that there was a remote ingredient
            ingredientRemote == null && ingredientLocal.remoteId != null -> {
                ingredientLocal.id?.let {
                    ingredientSourceLocal.delete(it) {
                        syncTaskCounter.taskEnded()
                    }
                }

            }

            // CASE 5: local and remote do not refer to the same ingredient => BUG!!!
            ingredientLocal.remoteId != ingredientRemote!!.id -> {
                // if this happens, we have a major bug!!!
                syncTaskCounter.taskEnded()
            }

            // CASE 6: remote has more recent updates, so we update local
            ingredientLocal.lastModified < ingredientRemote.lastModified -> {
                ingredientSourceLocal.update(
                    ingredientLocal.getUpdated(
                        ingredientRemote.quantity,
                        ingredientRemote.unity,
                        ingredientRemote.name
                    )
                ) { _, _ ->
                    syncTaskCounter.taskEnded()
                }
            }

            // CASE 7: local has more recent updates, so we update remote
            ingredientLocal.lastModified >= ingredientRemote.lastModified -> {
                ingredientSourceRemote.update(
                    ingredientRemote.getUpdated(
                        ingredientLocal.quantity,
                        ingredientLocal.unity,
                        ingredientLocal.name
                    )
                ) { _, _ ->
                    syncTaskCounter.taskEnded()
                }
            }

            // There shouldn't be anything left
            else -> {
                syncTaskCounter.taskEnded()
            }
        }
    }

    /*
    The callback returns the local recipeId - or null, if the ingredientRemote ingredient has no id
     */
    private fun retrieveLocalRecipeIdFromRemoteIngredient(
        ingredientRemote: IngredientRemote,
        callback: (Int?) -> Unit
    ) {
        ingredientRemote.id?.let { idOfRemote ->
            recipeSourceLocal.getRecipeForRemoteId(idOfRemote) { _, recipeLocal ->
                callback(recipeLocal?.rowid)
            }
        } ?: callback(null)
    }

    /*
    The callback returns the remote recipeId - or null, if the ingredientLocal recipe has not yet a remoteId, i.e. has not been synced with remote
     */
    private fun retrieveRemoteRecipeIdFromLocalIngredient(
        ingredientLocal: IngredientLocal,
        callback: (Int?) -> Unit
    ) {
        retrieveRemoteRecipeId(ingredientLocal.recipeId, callback)
    }

    /*
    The callback returns the remote recipeId - or null, if the local recipe has not yet a remoteId, i.e. has not been synced with remote
     */
    private fun retrieveRemoteRecipeId(recipeLocalId: Int, callback: (Int?) -> Unit) {
        recipeSourceLocal.get(recipeLocalId) { _, recipeLocal ->
            recipeLocal?.remoteId?.let { recipeRemoteId ->
                callback(recipeRemoteId)
            } ?: callback(null)
        }
    }

    companion object : SingletonHolder<SyncManager, Application>(::SyncManager)
}