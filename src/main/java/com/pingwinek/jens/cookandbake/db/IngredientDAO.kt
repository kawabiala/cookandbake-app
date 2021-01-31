package com.pingwinek.jens.cookandbake.db

import androidx.room.*
import com.pingwinek.jens.cookandbake.models.IngredientLocal

@Dao
interface IngredientDAO {

    /**
     * returns the rowId of the new entry
     */
    @Insert
    fun insertIngredient(ingredientLocal: IngredientLocal) : Long

    /**
     * returns the number of updated rows
     */
    @Update
    fun updateIngredient(ingredientLocal: IngredientLocal) : Int

    /**
     * returns the number of deleted rows
     */
    @Delete
    fun deleteIngredient(ingredientLocal: IngredientLocal) : Int

    /**
     * returns empty array if no results
     */
    @Query("SELECT * FROM IngredientLocal")
    fun selectAll() : Array<IngredientLocal>

    /**
     * returns empty array if no results
     */
    @Query("SELECT * FROM IngredientLocal WHERE recipeId = :recipeId")
    fun selectAllForRecipeId(recipeId: Int) : Array<IngredientLocal>

    /**
     * returns null if no result
     */
    @Query("SELECT * FROM IngredientLocal WHERE remoteId = :remoteId")
    fun selectIngredientForRemoteId(remoteId: Int) : IngredientLocal?

    /**
     * returns null if no result
     */
    @Query("SELECT * FROM IngredientLocal WHERE rowid = :id")
    fun selectIngredient(id: Int) : IngredientLocal?
}