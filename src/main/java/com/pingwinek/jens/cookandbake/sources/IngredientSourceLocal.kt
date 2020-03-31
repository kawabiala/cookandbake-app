package com.pingwinek.jens.cookandbake.sources

import android.app.Application
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.db.DatabaseService
import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.utils.Taskifier
import java.util.*

class IngredientSourceLocal private constructor(val application: Application) : IngredientSource<IngredientLocal> {

    private val db = DatabaseService.getDatabase(application)

    override fun getAll() : Promise<LinkedList<IngredientLocal>> {
        val promise = Promise<LinkedList<IngredientLocal>>()
        Taskifier<Array<IngredientLocal>> { ingredients ->
            promise.setResult(Promise.Status.SUCCESS, LinkedList(ingredients?.toList()))
        }.execute({db.ingredientDAO().getAll()})
        return promise
    }

    override fun getAllForRecipeId(recipeId: Int) : Promise<LinkedList<IngredientLocal>> {
        val promise = Promise<LinkedList<IngredientLocal>>()
        Taskifier<Array<IngredientLocal>> { ingredients ->
            promise.setResult(Promise.Status.SUCCESS, LinkedList(ingredients?.asList()))
        }.execute({db.ingredientDAO().getAllForRecipeId(recipeId)})
        return promise
    }

    override fun get(id: Int) : Promise<IngredientLocal> {
        val promise = Promise<IngredientLocal>()
        Taskifier<IngredientLocal> { ingredient ->
            val status = when (ingredient) {
                null -> Promise.Status.FAILURE
                else -> Promise.Status.SUCCESS
            }
            promise.setResult(status, ingredient)
        }.execute({db.ingredientDAO().getIngredient(id)})
        return promise
    }

    @Suppress("Unused")
    fun getForRemoteId(remoteId: Int) : Promise<IngredientLocal> {
        val promise = Promise<IngredientLocal>()
        Taskifier<IngredientLocal> { ingredient ->
            val status = when (ingredient) {
                null -> Promise.Status.FAILURE
                else -> Promise.Status.SUCCESS
            }
            promise.setResult(status, ingredient)
        }.execute({db.ingredientDAO().getIngredientForRemoteId(remoteId)})
        return promise
    }

    override fun new(item: IngredientLocal) : Promise<IngredientLocal> {
        var promise = Promise<IngredientLocal>()
        /*
        remoteId is unique -> if we already have an ingredient with the same remoteId,
        we delete it, before we insert the new one
         */
        if (item.remoteId != null) {
            getForRemoteId(item.remoteId).setResultHandler { getResult ->
                val ingredientLocal = getResult.value
                if (getResult.status == Promise.Status.SUCCESS && ingredientLocal != null) {
                    delete(ingredientLocal.id).setResultHandler {
                        promise = doNew(item)
                    }
                } else {
                    promise = doNew(item)
                }
            }
        } else {
            promise = doNew(item)
        }
        return promise
    }

    private fun doNew(item: IngredientLocal) : Promise<IngredientLocal> {
        var promise = Promise<IngredientLocal>()
        Taskifier<Long> { newIngredientId ->
            if (newIngredientId != null) {
                promise = get(newIngredientId.toInt())
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }.execute({db.ingredientDAO().insertIngredient(item)})
        return promise
    }

    override fun update(item: IngredientLocal) : Promise<IngredientLocal> {
        var promise = Promise<IngredientLocal>()
        Taskifier<Unit> {
            promise = get(item.id)
        }.execute({db.ingredientDAO().updateIngredient(item)})
        return promise
    }

    override fun delete(id: Int) : Promise<Unit> {
        var promise = Promise<Unit>()
        get(id).setResultHandler{ getResult ->
            val ingredientLocal = getResult.value
            if (getResult.status == Promise.Status.SUCCESS && ingredientLocal != null) {
                promise = delete(ingredientLocal)
            } else {
                promise.setResult(Promise.Status.FAILURE, null)
            }
        }
        return promise
    }

    fun flagAsDeleted(id: Int) : Promise<IngredientLocal> {
        var promise = Promise<IngredientLocal>()
        get(id).setResultHandler { getResult ->
            val ingredientLocal = getResult.value
            if (getResult.status == Promise.Status.SUCCESS && ingredientLocal != null) {
                promise = update(ingredientLocal.getDeleted())
            } else {
                promise.setResult(Promise.Status.FAILURE, ingredientLocal)
            }
        }
        return promise
    }

    private fun delete(item: IngredientLocal) : Promise<Unit> {
        val promise = Promise<Unit>()
        Taskifier<Unit> {
            promise.setResult(Promise.Status.SUCCESS, null)
        }.execute({db.ingredientDAO().deleteIngredient(item)})
        return promise
    }

    @Suppress("Unused")
    fun getIngredientForRemoteId(remoteId: Int) : Promise<IngredientLocal> {
        val promise = Promise<IngredientLocal>()
        Taskifier<IngredientLocal> { ingredient ->
            val status = when (ingredient) {
                null -> Promise.Status.FAILURE
                else -> Promise.Status.SUCCESS
            }
            promise.setResult(status, ingredient)
        }.execute({db.ingredientDAO().getIngredientForRemoteId(remoteId)})
        return promise
    }

    companion object : SingletonHolder<IngredientSourceLocal, Application>(::IngredientSourceLocal)

}