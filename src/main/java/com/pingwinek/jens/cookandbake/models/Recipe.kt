package com.pingwinek.jens.cookandbake.models

abstract class Recipe {
    abstract var id: Int
    abstract val title: String
    abstract val description: String?
    abstract val instruction: String?
    abstract var lastModified: Long
    //abstract fun getUpdated(title: String, description: String?, instruction: String?, lastModified: Long) : Recipe
    abstract fun getUpdated(recipe: Recipe) : Recipe
}