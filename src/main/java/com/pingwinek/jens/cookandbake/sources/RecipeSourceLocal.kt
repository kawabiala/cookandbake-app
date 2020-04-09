package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.db.PingwinekCooksDB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.lib.sync.SourceLocal
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.utils.Taskifier
import java.util.*

/**
 * Source to retrieve and manipulate local recipes
 *
 * @property db
 */
class RecipeSourceLocal private constructor(private val db: PingwinekCooksDB):
    RecipeSource<RecipeLocal>, SourceLocal<RecipeLocal> {

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
        val promise = Promise<RecipeLocal>()
        Taskifier<Long> { newRecipeId ->
            if (newRecipeId != null) {
                get(newRecipeId.toInt()).setResultHandler {
                    promise.setResult(it.status, it.value)
                }
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }.execute({db.recipeDAO().insert(item)})
        return promise
    }

    override fun update(item: RecipeLocal) : Promise<RecipeLocal> {
        val promise = Promise<RecipeLocal>()
        Taskifier<Unit> {
            get(item.id).setResultHandler {
                promise.setResult(it.status, it.value)
            }
        }.execute({db.recipeDAO().update(item)})
        return promise
    }

    override fun delete(id: Int) : Promise<Unit> {
        val promise = Promise<Unit>()
        get(id).setResultHandler { getResult ->
            val recipeLocal = getResult.value
            if (getResult.status == Promise.Status.SUCCESS && recipeLocal != null) {
                delete(recipeLocal).setResultHandler {
                    promise.setResult(it.status, it.value)
                }
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    fun flagAsDeleted(id: Int) : Promise<RecipeLocal> {
        val promise = Promise<RecipeLocal>()
        get(id).setResultHandler { getResult ->
            val recipeLocal = getResult.value
            if (getResult.status == Promise.Status.SUCCESS && recipeLocal != null) {
                update(recipeLocal.getDeleted()).setResultHandler {
                    promise.setResult(it.status, it.value)
                }
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
    override fun getForRemoteId(remoteId: Int) : Promise<RecipeLocal> {
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

    companion object : SingletonHolder<RecipeSourceLocal, PingwinekCooksDB>(::RecipeSourceLocal)
}