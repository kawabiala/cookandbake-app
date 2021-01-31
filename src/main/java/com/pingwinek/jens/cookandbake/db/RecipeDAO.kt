package com.pingwinek.jens.cookandbake.db

import androidx.room.*
import com.pingwinek.jens.cookandbake.models.RecipeLocal

@Dao
interface RecipeDAO {

    /**
     * returns the rowId of the new entry
     */
    @Insert
    fun insert(recipe: RecipeLocal) : Long

    /**
     * returns the number of updated rows
     */
    @Update
    fun update(recipe: RecipeLocal) : Int

    /**
     * returns the number of deleted rows
     */
    @Delete
    fun delete(recipe: RecipeLocal) : Int

    /**
     * results empty array if no results
     */
    @Query("SELECT * FROM recipeLocal")
    fun selectAll() : Array<RecipeLocal>

    /**
     * results null if no result
     */
    @Query("SELECT * FROM recipeLocal WHERE rowid = :id")
    fun select(id: Int) : RecipeLocal?

    /**
     * results null if no result
     */
    @Query("SELECT * FROM recipeLocal WHERE remoteId = :remoteId")
    fun selectForRemoteId(remoteId: Int) : RecipeLocal?
}