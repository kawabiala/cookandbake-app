package com.pingwinek.jens.cookandbake.models

import com.pingwinek.jens.cookandbake.lib.sync.Model

abstract class Recipe: Model {
    abstract val title: String
    abstract val description: String?
    abstract val instruction: String?
    abstract fun getUpdated(recipe: Recipe) : Recipe
}