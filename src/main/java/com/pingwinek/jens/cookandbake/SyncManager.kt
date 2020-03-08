package com.pingwinek.jens.cookandbake

import android.app.Application
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.IngredientRemote
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.models.RecipeRemote
import com.pingwinek.jens.cookandbake.sources.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SyncManager private constructor(val application: Application) {

    private val recipeSourceRemote = RecipeSourceRemote.getInstance(application)
    private val recipeSourceLocal = RecipeSourceLocal.getInstance(application)

    private val ingredientSourceRemote = IngredientSourceRemote.getInstance(application)
    private var ingredientSourceLocal = IngredientSourceLocal.getInstance(application)

    class SyncTaskCounter(private val onAllEnded: () -> Unit) {

        private var counter = 0

        fun taskStarted() {
            counter++
        }

        fun taskEnded() {
            counter--
            if (counter == 0) {
                onAllEnded()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// Recipes ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun syncRecipes(onDone: () -> Unit) {
        recipeSourceRemote.getAll { status, recipesRemote ->
            if (status == Source.Status.SUCCESS) {
                recipeSourceLocal.getAll { _, recipesLocal ->
                    compareRecipes(recipesLocal, recipesRemote, onDone)
                }
            } else {
                onDone()
            }
        }
    }

    fun syncRecipe(recipeId: Int, onDone: () -> Unit) {
        recipeSourceLocal.get(recipeId) { status, recipeLocal ->
            if (status == Source.Status.SUCCESS && recipeLocal != null) {
                val idOfRemote = recipeLocal.remoteId
                if (idOfRemote != null) {
                    recipeSourceRemote.get(idOfRemote) { statusRemote, recipeRemote ->
                        if (statusRemote == Source.Status.SUCCESS) {
                            compareRecipe(recipeLocal, recipeRemote, onDone)
                        } else {
                            onDone()
                        }
                    }
                } else {
                    compareRecipe(recipeLocal, null, onDone)
                }
            } else {
                // if local is null, we can't get remote either
                onDone()
            }
        }
    }

    private fun compareRecipes(
        recipesLocal: LinkedList<RecipeLocal>,
        recipesRemote: LinkedList<RecipeRemote>,
        onDone: () -> Unit
    ) {
        val syncTaskCounter = SyncTaskCounter(onDone)
        syncTaskCounter.taskStarted()
        syncTaskCounter.taskStarted()
        loopLocalRecipes(recipesLocal, recipesRemote) { syncTaskCounter.taskEnded() }
        loopRemoteRecipes(recipesLocal, recipesRemote) { syncTaskCounter.taskEnded() }
    }

    private fun loopLocalRecipes(
        recipesLocal: LinkedList<RecipeLocal>,
        recipesRemote: LinkedList<RecipeRemote>,
        onDone: () -> Unit
    ) {
        val syncTaskCounter = SyncTaskCounter(onDone)
        if (recipesLocal.size > 0) {
            recipesLocal.forEach { recipeLocal ->
                syncTaskCounter.taskStarted()
                compareRecipe(
                    recipeLocal,
                    recipesRemote.find { recipeRemote ->
                        recipeRemote.id == recipeLocal.remoteId
                    }
                ) { syncTaskCounter.taskEnded() }
            }
        } else {
            onDone()
        }
    }

    private fun loopRemoteRecipes(
        recipesLocal: LinkedList<RecipeLocal>,
        recipesRemote: LinkedList<RecipeRemote>,
        onDone: () -> Unit
    ) {
        val syncTaskCounter = SyncTaskCounter(onDone)
        if (recipesRemote.size > 0) {
            recipesRemote.forEach { recipeRemote ->
                if (recipesLocal.find { recipeLocal ->
                        recipeLocal.remoteId == recipeRemote.id
                    } == null) {
                    syncTaskCounter.taskStarted()
                    compareRecipe(
                        null,
                        recipeRemote
                    ) { syncTaskCounter.taskEnded() }
                } else {
                    onDone()
                }
            }
        } else {
            onDone()
        }
    }

    private fun compareRecipe(
        recipeLocal: RecipeLocal?,
        recipeRemote: RecipeRemote?,
        onDone: () -> Unit
    ) {
        when {
            // CASE 1: doesn't make sense to call this method with both local and remote null
            recipeLocal == null && recipeRemote == null -> onDone()

            // CASE 2: local null -> add new remote locally
            recipeLocal == null -> newLocalFromRemote(recipeRemote!!, onDone)

            // CASE 3: remote null and local.remoteId null -> add remotely
            recipeRemote == null && recipeLocal.remoteId == null -> newRemoteFromLocal(recipeLocal, onDone)

            // CASE 4: remote null and local.remoteId not null -> delete locally
            recipeRemote == null -> deleteLocal(recipeLocal, onDone)

            // CASE 5: local and remote do not refer to the same recipe => BUG!!!
            recipeLocal.remoteId != recipeRemote.id -> onDone()

            // CASE 6: remote more recent -> update locally
            recipeLocal.lastModified < recipeRemote.lastModified -> updateLocal(recipeLocal, recipeRemote, onDone)

            // CASE 7: local has flag deleted -> delete remote
            recipeLocal.flagAsDeleted -> deleteRemote(recipeLocal, onDone)

            // CASE 8: check
            recipeLocal.lastModified > recipeRemote.lastModified -> updateRemote(recipeLocal, recipeRemote, onDone)

            // there shouldn't be anything left
            else -> onDone()
        }
    }

    private fun newLocalFromRemote(
        recipeRemote: RecipeRemote,
        onDone: () -> Unit
    ) {
        recipeSourceLocal.new(RecipeLocal.newFromRemote(recipeRemote)) { _, _ ->
            onDone()
        }
    }

    private fun newRemoteFromLocal(
        recipeLocal: RecipeLocal,
        onDone: () -> Unit
    ) {
        recipeSourceRemote.new(RecipeRemote.newFromLocal(recipeLocal)) { status, newRecipeRemote ->
            if (status == Source.Status.SUCCESS && newRecipeRemote != null) {
                recipeSourceLocal.update(
                    RecipeLocal(
                        recipeLocal.id,
                        newRecipeRemote.id,
                        recipeLocal.title,
                        recipeLocal.description,
                        recipeLocal.instruction
                    )
                ) { _, _ ->
                    onDone()
                }
            } else {
                onDone()
            }
        }
    }

    private fun updateLocal(
        recipeLocal: RecipeLocal,
        recipeRemote: RecipeRemote,
        onDone: () -> Unit
    ) {
        recipeSourceLocal.update(
            recipeLocal.getUpdated(
                recipeRemote
            )
        ) { _, _ ->
            onDone()
        }
    }

    private fun updateRemote(
        recipeLocal: RecipeLocal,
        recipeRemote: RecipeRemote,
        onDone: () -> Unit
    ) {
        recipeSourceRemote.update(
            recipeRemote.getUpdated(
                recipeLocal
            )
        ) { _, _ ->
            onDone()
        }
    }

    private fun deleteLocal(
        recipeLocal: RecipeLocal,
        onDone: () -> Unit
    ) {
        recipeSourceLocal.delete(recipeLocal.id) {
            onDone()
        }
    }

    private fun deleteRemote(
        recipeLocal: RecipeLocal,
        onDone: () -> Unit
    ) {
        recipeLocal.remoteId?.let { remoteId ->
            recipeSourceRemote.delete(remoteId) {
                onDone()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// Ingredients ///////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun syncIngredients(localRecipeId: Int, onDone: () -> Unit) {
        recipeSourceLocal.toRemoteId(localRecipeId) { remoteRecipeId ->
            if (remoteRecipeId != null) {
                ingredientSourceRemote.getAllForRecipeId(remoteRecipeId) { status, ingredientsRemote ->
                    if (status == Source.Status.SUCCESS) {
                        ingredientSourceLocal.getAllForRecipeId(localRecipeId) { _, ingredientsLocal ->
                            compareIngredients(ingredientsLocal, ingredientsRemote, onDone)
                        }
                    } else {
                        onDone()
                    }
                }
            } else {
                ingredientSourceLocal.getAllForRecipeId(localRecipeId) { _, ingredientsLocal ->
                    compareIngredients(ingredientsLocal, LinkedList(), onDone)
                }
            }
        }
    }

    fun syncIngredient(ingredientId: Int, onDone: () -> Unit) {
        ingredientSourceLocal.get(ingredientId) { status, ingredientLocal ->
            if (status == Source.Status.SUCCESS && ingredientLocal != null) {
                val idOfRemote = ingredientLocal.remoteId
                if (idOfRemote != null) {
                    ingredientSourceRemote.get(idOfRemote) { statusRemote, ingredientRemote ->
                        // if local has remoteid, but remote is null, remote has been deleted -> will be handled in compareIngredient
                        if (statusRemote == Source.Status.SUCCESS) {
                            compareIngredient(ingredientLocal, ingredientRemote, onDone)
                        } else {
                            onDone()
                        }
                    }
                } else {
                    // if local has no remoteid, local is new and not yet been synced to remote
                    compareIngredient(ingredientLocal, null, onDone)
                }
            } else {
                // if local is null, we can't get remote either
                onDone()
            }
        }
    }

    private fun compareIngredients(
        ingredientsLocal: LinkedList<IngredientLocal>,
        ingredientsRemote: LinkedList<IngredientRemote>,
        onDone: () -> Unit
    ) {
        val syncTaskCounter = SyncTaskCounter(onDone)
        syncTaskCounter.taskStarted()
        syncTaskCounter.taskStarted()
        loopLocalIngredients(ingredientsLocal, ingredientsRemote) { syncTaskCounter.taskEnded() }
        loopRemoteIngredients(ingredientsLocal, ingredientsRemote) { syncTaskCounter.taskEnded() }
    }

    private fun loopLocalIngredients(
        ingredientsLocal: LinkedList<IngredientLocal>,
        ingredientsRemote: LinkedList<IngredientRemote>,
        onDone: () -> Unit
    ) {
        val syncTaskCounter = SyncTaskCounter(onDone)
        if (ingredientsLocal.size > 0) {
            ingredientsLocal.forEach { ingredientLocal ->
                syncTaskCounter.taskStarted()
                compareIngredient(
                    ingredientLocal,
                    ingredientsRemote.find { _ingredientRemote ->
                        ingredientLocal.remoteId == _ingredientRemote.id
                    }
                ) { syncTaskCounter.taskEnded() }
            }
        } else {
            onDone()
        }
    }

    private fun loopRemoteIngredients(
        ingredientsLocal: LinkedList<IngredientLocal>,
        ingredientsRemote: LinkedList<IngredientRemote>,
        onDone: () -> Unit
    ) {
        val syncTaskCounter = SyncTaskCounter(onDone)
        if (ingredientsRemote.size > 0) {
            ingredientsRemote.forEach { ingredientRemote ->
                if (ingredientsLocal.find { ingredientLocal ->
                        ingredientLocal.remoteId == ingredientRemote.id
                    } == null) {
                    syncTaskCounter.taskStarted()
                    compareIngredient(
                        null,
                        ingredientRemote) {
                        syncTaskCounter.taskEnded()
                    }
                } else {
                    onDone()
                }
            }
        } else {
            onDone()
        }
    }

    private fun compareIngredient(
        ingredientLocal: IngredientLocal?,
        ingredientRemote: IngredientRemote?,
        onDone: () -> Unit
    ) {
        when {
            // CASE 1: doesn't make sense to call this method with both local and remote null
            ingredientLocal == null && ingredientRemote == null -> onDone()

            // CASE 2: we have a new remote ingredient, that we want to insert into local
            ingredientLocal == null -> newLocalFromRemote(ingredientRemote!!, onDone)

            // CASE 3: we have a new local ingredient, that we want to insert into remote
            ingredientRemote == null && ingredientLocal.remoteId == null -> newRemoteFromLocal(ingredientLocal, onDone)

            // CASE 4: remote deleted, i.e. existence of local.remoteid indicates, that there was a remote ingredient
            ingredientRemote == null && ingredientLocal.remoteId != null -> deleteLocal(ingredientLocal, onDone)

            // CASE 5: local and remote do not refer to the same ingredient => BUG!!!
            ingredientLocal.remoteId != ingredientRemote!!.id -> onDone()

            // CASE 6: remote has more recent updates, so we update local
            ingredientLocal.lastModified < ingredientRemote.lastModified -> updateLocal(ingredientLocal, ingredientRemote, onDone)

            // CASE 7: local has deleted flag, delete remotely
            ingredientLocal.flagAsDeleted -> deleteRemote(ingredientLocal, onDone)

            // CASE 8: local has more recent updates, so we update remote
            ingredientLocal.lastModified > ingredientRemote.lastModified -> updateRemote(ingredientLocal, ingredientRemote, onDone)

            // There shouldn't be anything left
            else -> onDone()
        }
    }

    private fun newLocalFromRemote(
        ingredientRemote: IngredientRemote,
        onDone: () -> Unit
    ) {
        localRecipeIdForRemoteIngredient(ingredientRemote) { recipeLocalId ->
            if (recipeLocalId != null) {
                ingredientSourceLocal.new(
                    IngredientLocal.newFromRemote(
                        ingredientRemote,
                        recipeLocalId
                    )
                ) { _, _ ->
                    onDone()
                }
            } else {
                onDone()
            }
        }
    }

    private val lockedNewIngredients = ConcurrentHashMap<Int, Long>()

    @Synchronized
    private fun toggleLockForNewIngredient(ingredientLocalId: Int, lock: Boolean): Boolean {
        return if (lockedNewIngredients.containsKey(ingredientLocalId)) {
            if (!lock) {
                lockedNewIngredients.remove(ingredientLocalId)
            }
            false
        } else {
            lockedNewIngredients[ingredientLocalId] = Date().time
            true
        }
    }

    private fun newRemoteFromLocal(
        ingredientLocal: IngredientLocal,
        onDone: () -> Unit
    ) {
        if (!toggleLockForNewIngredient(ingredientLocal.id, true)) {
            onDone()
            return
        }

        remoteRecipeIdForLocalIngredient(ingredientLocal) { recipeId ->
            if (recipeId == null) {
                toggleLockForNewIngredient(ingredientLocal.id, false)
                onDone()
                return@remoteRecipeIdForLocalIngredient
            }

            ingredientSourceLocal.get(ingredientLocal.id) { status, checkedIngredientLocal ->
                if (status == Source.Status.SUCCESS && checkedIngredientLocal?.remoteId == null) {
                    ingredientSourceRemote.new(
                        IngredientRemote.newFromLocal(
                            ingredientLocal,
                            recipeId
                        )
                    ) { statusRemote, newIngredientRemote ->
                        if (statusRemote == Source.Status.SUCCESS && newIngredientRemote != null) {
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
                                toggleLockForNewIngredient(ingredientLocal.id, false)
                                onDone()
                            }
                        } else {
                            toggleLockForNewIngredient(ingredientLocal.id, false)
                            onDone()
                        }
                    }
                } else {
                    toggleLockForNewIngredient(ingredientLocal.id, false)
                    onDone()
                }
            }
        }
    }

    private fun deleteLocal(
        ingredientLocal: IngredientLocal,
        onDone: () -> Unit
    ) {
        ingredientSourceLocal.delete(ingredientLocal.id) {
            onDone()
        }
    }

    private fun deleteRemote(
        ingredientLocal: IngredientLocal,
        onDone: () -> Unit
    ) {
        ingredientLocal.remoteId?.let {
            ingredientSourceRemote.delete(it) {
                onDone()
            }
        }
    }

    private fun updateLocal(
        ingredientLocal: IngredientLocal,
        ingredientRemote: IngredientRemote,
        onDone: () -> Unit
    ) {
        ingredientSourceLocal.update(
            ingredientLocal.getUpdated(
                ingredientRemote
            )
        ) { _, _ ->
            onDone()
        }
    }

    private fun updateRemote(
        ingredientLocal: IngredientLocal,
        ingredientRemote: IngredientRemote,
        onDone: () -> Unit
    ) {
        ingredientSourceRemote.update(
            ingredientRemote.getUpdated(
                ingredientLocal
            )
        ) { _, _ ->
            onDone()
        }
    }

    /*
    The callback returns the local recipeId - or null, if the ingredientRemote ingredient has no id
     */
    private fun localRecipeIdForRemoteIngredient(
        ingredientRemote: IngredientRemote,
        callback: (Int?) -> Unit
    ) {
        recipeSourceLocal.toLocalId(ingredientRemote.recipeId, callback)
    }

    /*
    The callback returns the remote recipeId - or null, if the ingredientLocal recipe has not yet a remoteId, i.e. has not been synced with remote
     */
    private fun remoteRecipeIdForLocalIngredient(
        ingredientLocal: IngredientLocal,
        callback: (Int?) -> Unit
    ) {
        recipeSourceLocal.toRemoteId(ingredientLocal.recipeId, callback)
    }

    companion object : SingletonHolder<SyncManager, Application>(::SyncManager)
}