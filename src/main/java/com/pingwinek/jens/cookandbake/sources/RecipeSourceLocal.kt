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

    override suspend fun getAll() : LinkedList<RecipeLocal> {
        return LinkedList(db.recipeDAO().selectAll().asList())
    }

    override suspend fun get(id: Int) : RecipeLocal? {
        return db.recipeDAO().select(id)
    }

    /**
     * Always returns a non-null result
     */
    override suspend fun new(item: RecipeLocal) : RecipeLocal {
        val newId = db.recipeDAO().insert(item)
        return get(newId.toInt())!!
    }

    /**
     * Returns null if for any reason there is no corresponding item to be updated
     */
    override suspend fun update(item: RecipeLocal) : RecipeLocal? {
        val updated = db.recipeDAO().update(item)
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

    suspend fun flagAsDeleted(id: Int) : RecipeLocal? {
        val toDelete = get(id)?.getDeleted() ?: return null
        return update(toDelete)
    }

    private fun delete(item: RecipeLocal) : Boolean {
        return db.recipeDAO().delete(item) > 0
    }

    @Suppress("Unused")
    override suspend fun getForRemoteId(remoteId: Int) : RecipeLocal? {
        return db.recipeDAO().selectForRemoteId(remoteId)
    }

    suspend fun toRemoteId(localId: Int) : Int? {
        return get(localId)?.remoteId
    }

    suspend fun toLocalId(remoteId: Int) : Int? {
        return getForRemoteId(remoteId)?.id
    }

    companion object : SingletonHolder<RecipeSourceLocal, PingwinekCooksDB>(::RecipeSourceLocal)
}