package com.pingwinek.jens.cookandbake.sources

import android.app.Application
import com.pingwinek.jens.cookandbake.db.DatabaseService
import com.pingwinek.jens.cookandbake.SingletonHolder
import com.pingwinek.jens.cookandbake.lib.sync.Source
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import com.pingwinek.jens.cookandbake.utils.Taskifier
import java.util.*

class RecipeSourceLocal private constructor(val application: Application):
    RecipeSource<RecipeLocal> {

    private val db = DatabaseService.getDatabase(application)

    override fun getAll(callback: (Source.Status, LinkedList<RecipeLocal>) -> Unit) {
        Taskifier<Array<RecipeLocal>> { recipes ->
            callback(Source.Status.SUCCESS, LinkedList(recipes?.asList()))
        }.execute({db.recipeDAO().selectAll()})
    }

    override fun get(id: Int, callback: (Source.Status, RecipeLocal?) -> Unit) {
        Taskifier<RecipeLocal> { recipe ->
            val status = when (recipe) {
                null -> Source.Status.FAILURE
                else -> Source.Status.SUCCESS
            }
            callback(status, recipe)
        }.execute({db.recipeDAO().select(id)})
    }

    override fun new(item: RecipeLocal, callback: (Source.Status, RecipeLocal?) -> Unit) {
        Taskifier<Long> { newRecipeId ->
            if (newRecipeId != null) {
                get(newRecipeId.toInt(), callback)
            } else {
                callback(Source.Status.FAILURE, null)
            }
        }.execute({db.recipeDAO().insert(item)})
    }

    override fun update(item: RecipeLocal, callback: (Source.Status, RecipeLocal?) -> Unit) {
        Taskifier<Unit> {
            get(item.id, callback)
        }.execute({db.recipeDAO().update(item)})
    }

    override fun delete(id: Int, callback: (Source.Status) -> Unit) {
        get(id) { status, recipeLocal ->
            if (status == Source.Status.SUCCESS && recipeLocal != null) {
                delete(recipeLocal, callback)
            } else {
                callback(Source.Status.FAILURE)
            }
        }
    }

    fun flagAsDeleted(id: Int, callback: (Source.Status, RecipeLocal?) -> Unit) {
        get(id) { status, recipeLocal ->
            if (status == Source.Status.SUCCESS && recipeLocal != null) {
                update(recipeLocal.getDeleted(), callback)
            } else {
                callback(Source.Status.FAILURE, recipeLocal)
            }
        }
    }

    private fun delete(item: RecipeLocal, callback: (Source.Status) -> Unit) {
        Taskifier<Unit> {
            callback(Source.Status.SUCCESS)
        }.execute({db.recipeDAO().delete(item)})
    }

    fun getForRemoteId(remoteId: Int, callback: (Source.Status, RecipeLocal?) -> Unit) {
        Taskifier<RecipeLocal> { recipe ->
            val status = when (recipe) {
                null -> Source.Status.FAILURE
                else -> Source.Status.SUCCESS
            }
            callback(status, recipe)
        }.execute({db.recipeDAO().selectForRemoteId(remoteId)})
    }

    fun toRemoteId(localId: Int, callback: (Int?) -> Unit) {
        get(localId) { status, recipeLocal ->
            if (status == Source.Status.SUCCESS && recipeLocal != null) {
                callback(recipeLocal.remoteId)
            } else {
                callback(null)
            }
        }
    }

    fun toLocalId(remoteId: Int, callback: (Int?) -> Unit) {
        getForRemoteId(remoteId) { status, recipeLocal ->
            if (status == Source.Status.SUCCESS && recipeLocal != null) {
                callback(recipeLocal.id)
            } else {
                callback(null)
            }
        }
    }

    companion object : SingletonHolder<RecipeSourceLocal, Application>(::RecipeSourceLocal)
}