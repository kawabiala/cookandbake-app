package com.pingwinek.jens.cookandbake.db

import androidx.room.*
import com.pingwinek.jens.cookandbake.models.RecipeLocal

@Dao
interface RecipeDAO {

    @Insert
    fun insert(recipe: RecipeLocal) : Long

    @Update
    fun update(recipe: RecipeLocal)

    @Delete
    fun delete(recipe: RecipeLocal)

    @Query("SELECT * FROM recipeLocal")
    fun selectAll() : Array<RecipeLocal>

    @Query("SELECT * FROM recipeLocal WHERE rowid = :id")
    fun select(id: Int) : RecipeLocal

    @Query("SELECT * FROM recipeLocal WHERE remoteId = :remoteId")
    fun selectForRemoteId(remoteId: Int) : RecipeLocal
}