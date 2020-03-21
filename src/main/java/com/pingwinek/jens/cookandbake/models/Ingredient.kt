package com.pingwinek.jens.cookandbake.models

import com.pingwinek.jens.cookandbake.lib.sync.Model

abstract class Ingredient: Model {
    abstract val recipeId: Int
    abstract val quantity: Double?
    abstract val unity: String?
    abstract val name: String
    abstract fun getUpdated(ingredient: Ingredient) : Ingredient
}