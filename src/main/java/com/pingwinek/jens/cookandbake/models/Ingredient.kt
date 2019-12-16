package com.pingwinek.jens.cookandbake.models

import java.util.*

abstract class Ingredient {
    abstract val id: Int?
    abstract val recipeId: Int
    abstract val quantity: Double?
    abstract val unity: String?
    abstract val name: String
    var lastModified: Long = Date().time
}