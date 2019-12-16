package com.pingwinek.jens.cookandbake.db

import androidx.room.*
import com.pingwinek.jens.cookandbake.models.IngredientLocal

@Dao
interface IngredientDAO {

    @Insert
    fun insertIngredient(ingredientLocal: IngredientLocal) : Long

    @Update
    fun updateIngredient(ingredientLocal: IngredientLocal)

    @Delete
    fun deleteIngredient(ingredientLocal: IngredientLocal)

    @Query("SELECT * FROM IngredientLocal")
    fun getAll() : Array<IngredientLocal>

    @Query("SELECT * FROM IngredientLocal WHERE recipeId = :recipeId")
    fun getAllForRecipeId(recipeId: Int) : Array<IngredientLocal>

    @Query("SELECT * FROM IngredientLocal WHERE remoteId = :remoteId")
    fun getIngredientForRemoteId(remoteId: Int) : IngredientLocal?

    @Query("SELECT * FROM IngredientLocal WHERE rowid = :id")
    fun getIngredient(id: Int) : IngredientLocal?
}