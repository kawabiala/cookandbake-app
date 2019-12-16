package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.models.Recipe
import java.util.*

interface RecipeSource<T: Recipe> : Source<T> {

    override fun getAll(callback: (Source.Status, LinkedList<T>) -> Unit)
    override fun get(id: Int, callback: (Source.Status, T?) -> Unit)
    override fun new(item: T, callback: (Source.Status, T?) -> Unit)
    override fun update(item: T, callback: (Source.Status, T?) -> Unit)
    override fun delete(id: Int, callback: (Source.Status) -> Unit)
}