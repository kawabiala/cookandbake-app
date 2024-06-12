package com.pingwinek.jens.cookandbake.models

abstract class Recipe: Model {
    abstract val title: String
    abstract val description: String?
    abstract val instruction: String?
    abstract val hasAttachment: Boolean
    abstract fun getUpdated(recipe: Recipe) : Recipe
}