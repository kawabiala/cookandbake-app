package com.pingwinek.jens.cookandbake.sources

import android.app.Application
import androidx.room.Room
import com.pingwinek.jens.cookandbake.*
import com.pingwinek.jens.cookandbake.db.PingwinekCooksDB
import com.pingwinek.jens.cookandbake.models.IngredientLocal
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

    override fun new(item: IngredientLocal, callback: (Source.Status, IngredientLocal?) -> Unit) {
        Taskifier<Long> { newIngredientId ->
            if (newIngredientId != null) {
                get(newIngredientId.toInt(), callback)
            } else {
                callback(Source.Status.FAILURE, null)
            }
        }
    }

    override fun update(item: IngredientLocal, callback: (Source.Status, IngredientLocal?) -> Unit) {
        Taskifier<Unit> {
            if (item.id != null) {
                get(item.id, callback)
            } else {
                callback(Source.Status.FAILURE, null)
            }
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