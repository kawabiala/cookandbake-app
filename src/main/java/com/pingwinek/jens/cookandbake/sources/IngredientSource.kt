package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.models.Ingredient
import java.util.*

interface IngredientSource<T : Ingredient> : Source<T> {

    override fun getAll(callback: (Source.Status, LinkedList<T>) -> Unit)
    override fun get(id: Int, callback: (Source.Status, T?) -> Unit)
    override fun new(item: T, callback: (Source.Status, T?) -> Unit)
    override fun update(item: T, callback: (Source.Status, T?) -> Unit)
    override fun delete(id: Int, callback: (Source.Status) -> Unit)

    fun getAllForRecipeId(recipeId: Int, callback: (Source.Status, LinkedList<T>) -> Unit)
}