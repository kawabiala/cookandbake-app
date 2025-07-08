package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.models.Tag4Recipe
import java.util.LinkedList

interface Tag4RecipeSource<T: Tag4Recipe> : Source<T> {

    suspend fun getAllForRecipeId(recipeId: String): LinkedList<T>

}