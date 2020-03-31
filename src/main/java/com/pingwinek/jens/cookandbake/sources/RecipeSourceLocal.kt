package com.pingwinek.jens.cookandbake.sources

import android.app.Application
import com.pingwinek.jens.cookandbake.db.DatabaseService
import com.pingwinek.jens.cookandbake.SingletonHolder
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.utils.Taskifier
import java.util.*

/**
 * Source to retrieve and manipulate local recipes
 *
 * @property application
 */
class RecipeSourceLocal private constructor(val application: Application):
    RecipeSource<RecipeLocal> {

    private val db = DatabaseService.getDatabase(application)

    override fun getAll() : Promise<LinkedList<RecipeLocal>> {
        val promise = Promise<LinkedList<RecipeLocal>>()
        Taskifier<Array<RecipeLocal>> { recipes ->
            promise.setResult(Promise.Status.SUCCESS, LinkedList(recipes?.asList()))
        }.execute({db.recipeDAO().selectAll()})
        return promise
    }

    override fun get(id: Int) : Promise<RecipeLocal> {
        val promise = Promise<RecipeLocal>()
        Taskifier<RecipeLocal> { recipe ->
            val status = when (recipe) {
                null -> Promise.Status.FAILURE
                else -> Promise.Status.SUCCESS
            }
            promise.setResult(status, recipe)
        }.execute({db.recipeDAO().select(id)})
        return promise
    }

    override fun new(item: RecipeLocal) : Promise<RecipeLocal> {
        var promise = Promise<RecipeLocal>()
        Taskifier<Long> { newRecipeId ->
            if (newRecipeId != null) {
                promise = get(newRecipeId.toInt())
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }.execute({db.recipeDAO().insert(item)})
        return promise
    }

    override fun update(item: RecipeLocal) : Promise<RecipeLocal> {
        var promise = Promise<RecipeLocal>()
        Taskifier<Unit> {
            promise = get(item.id)
        }.execute({db.recipeDAO().update(item)})
        return promise
    }

    override fun delete(id: Int) : Promise<Unit> {
        var promise = Promise<Unit>()
        get(id).setResultHandler { getResult ->
            val recipeLocal = getResult.value
            if (getResult.status == Promise.Status.SUCCESS && recipeLocal != null) {
                promise = delete(recipeLocal)
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    fun flagAsDeleted(id: Int) : Promise<RecipeLocal> {
        var promise = Promise<RecipeLocal>()
        get(id).setResultHandler { getResult ->
            val recipeLocal = getResult.value
            if (getResult.status == Promise.Status.SUCCESS && recipeLocal != null) {
                promise =update(recipeLocal.getDeleted())
            } else {
                promise.setResult(Promise.Status.FAILURE, recipeLocal)
            }
        }
        return promise
    }

    private fun delete(item: RecipeLocal) : Promise<Unit> {
        val promise = Promise<Unit>()
        Taskifier<Unit> {
            promise.setResult(Promise.Status.SUCCESS, null)
        }.execute({db.recipeDAO().delete(item)})
        return promise
    }

    @Suppress("Unused")
    fun getForRemoteId(remoteId: Int) : Promise<RecipeLocal> {
        val promise = Promise<RecipeLocal>()
        Taskifier<RecipeLocal> { recipe ->
            val status = when (recipe) {
                null -> Promise.Status.FAILURE
                else -> Promise.Status.SUCCESS
            }
            promise.setResult(status, recipe)
        }.execute({db.recipeDAO().selectForRemoteId(remoteId)})
        return promise
    }

    fun toRemoteId(localId: Int) : Promise<Int> {
        val promise = Promise<Int>()
        get(localId).setResultHandler { getResult ->
            val recipeLocal = getResult.value
            if (getResult.status == Promise.Status.SUCCESS && recipeLocal != null) {
                promise.setResult(Promise.Status.SUCCESS, recipeLocal.remoteId)
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    fun toLocalId(remoteId: Int) : Promise<Int> {
        val promise = Promise<Int>()
        getForRemoteId(remoteId).setResultHandler { getResult ->
            val recipeLocal = getResult.value
            if (getResult.status == Promise.Status.SUCCESS && recipeLocal != null) {
                promise.setResult(Promise.Status.SUCCESS, recipeLocal.id)
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    companion object : SingletonHolder<RecipeSourceLocal, Application>(::RecipeSourceLocal)
}