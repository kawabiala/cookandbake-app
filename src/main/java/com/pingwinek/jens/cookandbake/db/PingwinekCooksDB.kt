package com.pingwinek.jens.cookandbake.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.RecipeLocal

@Database(entities = [RecipeLocal::class, IngredientLocal::class], version = 13, exportSchema = false)
@TypeConverters(com.pingwinek.jens.cookandbake.db.TypeConverters::class)
abstract class PingwinekCooksDB : RoomDatabase() {

    abstract fun recipeDAO(): RecipeDAO
    abstract fun ingredientDAO() : IngredientDAO
}