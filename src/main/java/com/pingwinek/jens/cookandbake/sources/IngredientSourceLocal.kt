package com.pingwinek.jens.cookandbake.sources

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.db.PingwinekCooksDB
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.utils.Taskifier
import java.util.*

class IngredientSourceLocal private constructor(val application: Application) : IngredientSource<IngredientLocal> {

    private val db = Room.databaseBuilder(application, PingwinekCooksDB::class.java, "PingwinekCooks")
        .fallbackToDestructiveMigration()
        .build()

    override fun getAll(callback: (Source.Status, LinkedList<IngredientLocal>) -> Unit) {
        Taskifier<Array<IngredientLocal>> { ingredients ->
            callback(Source.Status.SUCCESS, LinkedList(ingredients?.toList()))
        }.execute({db.ingredientDAO().getAll()})
    }

    override fun getAllForRecipeId(recipeId: Int, callback: (Source.Status, LinkedList<IngredientLocal>) -> Unit) {
        Taskifier<Array<IngredientLocal>> { ingredients ->
            callback(Source.Status.SUCCESS, LinkedList(ingredients?.asList()))
        }.execute({db.ingredientDAO().getAllForRecipeId(recipeId)})
    }

    override fun get(id: Int, callback: (Source.Status, IngredientLocal?) -> Unit) {
        Taskifier<IngredientLocal> { ingredient ->
            val status = when (ingredient) {
                null -> Source.Status.FAILURE
                else -> Source.Status.SUCCESS
            }
            callback(status, ingredient)
        }.execute({db.ingredientDAO().getIngredient(id)})
    }

    fun getForRemoteId(remoteId: Int, callback: (Source.Status, IngredientLocal?) -> Unit) {
        Taskifier<IngredientLocal> { ingredient ->
            val status = when (ingredient) {
                null -> Source.Status.FAILURE
                else -> Source.Status.SUCCESS
            }
            callback(status, ingredient)
        }.execute({db.ingredientDAO().getIngredientForRemoteId(remoteId)})
    }

    override fun new(item: IngredientLocal, callback: (Source.Status, IngredientLocal?) -> Unit) {
        if (item.remoteId != null) {
            getForRemoteId(item.remoteId) { _, ingredientLocal ->
                if (ingredientLocal != null) {
                    delete(ingredientLocal.id) {
                        Log.i(
                            this::class.java.name,
                            "Deleted duplicate ingredient: $ingredientLocal"
                        )
                        doNew(item, callback)
                    }
                } else {
                    doNew(item, callback)
                }
            }
        } else {
            doNew(item, callback)
        }
    }

    private fun doNew(item: IngredientLocal, callback: (Source.Status, IngredientLocal?) -> Unit) {
        Taskifier<Long> { newIngredientId ->
            if (newIngredientId != null) {
                get(newIngredientId.toInt(), callback)
            } else {
                callback(Source.Status.FAILURE, null)
            }
        }.execute({db.ingredientDAO().insertIngredient(item)})
    }

    override fun update(item: IngredientLocal, callback: (Source.Status, IngredientLocal?) -> Unit) {
        Taskifier<Unit> {
            get(item.id, callback)
        }.execute({db.ingredientDAO().updateIngredient(item)})
    }

    override fun delete(id: Int, callback: (Source.Status) -> Unit) {
        get(id) { status, ingredientLocal ->
            if (status == Source.Status.SUCCESS && ingredientLocal != null) {
                delete(ingredientLocal, callback)
            } else {
                callback(Source.Status.FAILURE)
            }
        }
    }

    private fun delete(item: IngredientLocal, callback: (Source.Status) -> Unit) {
        Taskifier<Unit> {
            callback(Source.Status.SUCCESS)
        }.execute({db.ingredientDAO().deleteIngredient(item)})
    }

    fun getIngredientForRemoteId(remoteId: Int, callback: (Source.Status, IngredientLocal?) -> Unit) {
        Taskifier<IngredientLocal> { ingredient ->
            val status = when (ingredient) {
                null -> Source.Status.FAILURE
                else -> Source.Status.SUCCESS
            }
            callback(status, ingredient)
        }.execute({db.ingredientDAO().getIngredientForRemoteId(remoteId)})
    }

    companion object : SingletonHolder<IngredientSourceLocal, Application>(::IngredientSourceLocal)

}