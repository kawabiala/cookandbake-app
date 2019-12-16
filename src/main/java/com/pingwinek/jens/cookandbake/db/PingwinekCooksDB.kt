package com.pingwinek.jens.cookandbake.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.RecipeLocal

@Database(entities = arrayOf(RecipeLocal::class, IngredientLocal::class), version = 5, exportSchema = false)
abstract class PingwinekCooksDB : RoomDatabase() {

    abstract fun recipeDAO(): RecipeDAO
    abstract fun ingredientDAO() : IngredientDAO
}