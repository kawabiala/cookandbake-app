package com.pingwinek.jens.cookandbake.sources

import android.app.Application
import androidx.room.Room
import com.pingwinek.jens.cookandbake.SingletonHolder
import com.pingwinek.jens.cookandbake.db.PingwinekCooksDB
import com.pingwinek.jens.cookandbake.models.RecipeLocal
import java.util.*

class RecipeSourceLocal private constructor(val application: Application):
    RecipeSource<RecipeLocal> {

    private val db = Room.databaseBuilder(application, PingwinekCooksDB::class.java, "PingwinekCooks")
        .fallbackToDestructiveMigration()
        .build()

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
            if (item.rowid != null) {
                get(item.rowid, callback)
            } else {
                callback(Source.Status.FAILURE, null)
            }
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

    private fun delete(item: RecipeLocal, callback: (Source.Status) -> Unit) {
        Taskifier<Unit> {
            callback(Source.Status.SUCCESS)
        }.execute({db.recipeDAO().delete(item)})
    }

    fun getRecipeForRemoteId(remoteId: Int, callback: (Source.Status, RecipeLocal?) -> Unit) {
        Taskifier<RecipeLocal> { recipe ->
            val status = when (recipe) {
                null -> Source.Status.FAILURE
                else -> Source.Status.SUCCESS
            }
            callback(status, recipe)
        }.execute({db.recipeDAO().selectForRemoteId(remoteId)})
    }

    companion object : SingletonHolder<RecipeSourceLocal, Application>(::RecipeSourceLocal)
}