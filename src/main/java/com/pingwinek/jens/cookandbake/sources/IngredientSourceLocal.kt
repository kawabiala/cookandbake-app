package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.db.PingwinekCooksDB
import com.pingwinek.jens.cookandbake.lib.sync.SourceLocal
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.*

class IngredientSourceLocal private constructor(private val db: PingwinekCooksDB) : IngredientSource<IngredientLocal>, SourceLocal<IngredientLocal> {

    override suspend fun getAll() : LinkedList<IngredientLocal> {
        return LinkedList(db.ingredientDAO().selectAll().asList())
    }

    override suspend fun getAllForRecipeId(recipeId: Int) : LinkedList<IngredientLocal> {
        return LinkedList(db.ingredientDAO().selectAllForRecipeId(recipeId).asList())
    }

    override suspend fun get(id: Int) : IngredientLocal? {
        return db.ingredientDAO().selectIngredient(id)
    }

    @Suppress("Unused")
    override suspend fun getForRemoteId(remoteId: Int) : IngredientLocal? {
        return db.ingredientDAO().selectIngredientForRemoteId(remoteId)
    }

    override suspend fun new(item: IngredientLocal) : IngredientLocal {
        /*
        remoteId is unique -> if we already have an ingredient with the same remoteId,
        we delete it, before we insert the new one
         */
        item.remoteId?.let { remoteId ->
            getForRemoteId(remoteId)?.let { delete(it.id) }
        }

        val newId = db.ingredientDAO().insertIngredient(item)
        return get(newId.toInt())!!
    }

    override suspend fun update(item: IngredientLocal) : IngredientLocal? {
        val updated = db.ingredientDAO().updateIngredient(item)
        return if (updated > 0) {
            get(item.id)!!
        } else {
            null
        }
    }

    override suspend fun delete(id: Int) : Boolean {
        val toDelete = get(id) ?: return false
        return delete(toDelete)
    }

    suspend fun flagAsDeleted(id: Int) : IngredientLocal? {
        val toDelete = get(id)?.getDeleted() ?: return null
        return update(toDelete)
    }

    private suspend fun delete(item: IngredientLocal) : Boolean {
        return db.ingredientDAO().deleteIngredient(item) > 0
    }

    companion object : SingletonHolder<IngredientSourceLocal, PingwinekCooksDB>(::IngredientSourceLocal)

}