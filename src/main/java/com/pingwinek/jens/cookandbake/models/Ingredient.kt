package com.pingwinek.jens.cookandbake.models

import com.pingwinek.jens.cookandbake.lib.sync.Model

abstract class Ingredient: Model {
    abstract val recipeId: String
    abstract val quantity: Double?
    abstract val quantityVerbal: String?
    abstract val unity: String?
    abstract val name: String
    abstract val sort: Int
//    abstract fun getUpdated(ingredient: Ingredient) : Ingredient
}