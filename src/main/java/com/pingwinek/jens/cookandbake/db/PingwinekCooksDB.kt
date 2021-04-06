package com.pingwinek.jens.cookandbake.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pingwinek.jens.cookandbake.models.FileLocal
import com.pingwinek.jens.cookandbake.models.IngredientLocal
import com.pingwinek.jens.cookandbake.models.RecipeLocal

@Database(entities = [RecipeLocal::class, IngredientLocal::class, FileLocal::class], version = 15, exportSchema = false)
@TypeConverters(com.pingwinek.jens.cookandbake.db.TypeConverters::class)
abstract class PingwinekCooksDB : RoomDatabase() {

    abstract fun recipeDAO(): RecipeDAO
    abstract fun ingredientDAO() : IngredientDAO
    abstract fun fileDAO() : FileDAO
}