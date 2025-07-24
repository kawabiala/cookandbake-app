package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.models.Tag

interface TagSource<T: Tag> : Source<T> {

//    suspend fun getRecipeIDs(tag: Tag): LinkedList<String>
//    suspend fun newRecipeID(tag4Recipe: Tag4Recipe, recipeTitle: String): String
//    suspend fun deleteRecipeID(tagID: String, recipeID: String): Boolean

}