package com.pingwinek.jens.cookandbake.repos

import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.lib.SingletonHolder
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.models.Tag4Recipe
import com.pingwinek.jens.cookandbake.models.Tag4RecipeFB
import com.pingwinek.jens.cookandbake.models.TagFB
import com.pingwinek.jens.cookandbake.sources.Tag4RecipeSourceFB
import com.pingwinek.jens.cookandbake.sources.TagSourceFB
import java.util.LinkedList

class TagRepository private constructor(val application: PingwinekCooksApplication) {

    private val tagSourceFB = application.getServiceLocator().getService(TagSourceFB::class.java)
    private val tag4RecipeSourceFB = application.getServiceLocator().getService(Tag4RecipeSourceFB::class.java)

    fun generateTag4Recipe(tag: Tag, recipeId: String): Tag4Recipe {
        return Tag4RecipeFB(tag, recipeId)
    }

    suspend fun getAll(): LinkedList<Tag> {
        return LinkedList(tagSourceFB.getAll())
    }

    suspend fun getAllForRecipe(recipeId: String): LinkedList<Tag4Recipe> {
        return LinkedList(tag4RecipeSourceFB.getAllForRecipeId(recipeId))
    }

    suspend fun getAllRecipeIdsForTag(tag: Tag): LinkedList<String> {
        return LinkedList(tagSourceFB.getRecipeIDs(tag))
    }

    suspend fun new(label: String): Tag {
        return tagSourceFB.new(TagFB(label))
    }

    suspend fun new(tag: Tag, recipeId: String, recipeTitle: String): Tag4Recipe {
        val tag4Recipe = Tag4RecipeFB(tag, recipeId)
        tagSourceFB.newRecipeID(tag4Recipe, recipeTitle)
        return tag4RecipeSourceFB.new(tag4Recipe)
    }

    suspend fun delete(tag: Tag): Boolean {
        return tagSourceFB.delete(tag as TagFB)
    }

    suspend fun deleteForRecipe(tag4Recipe: Tag4Recipe): Boolean {
        return tag4RecipeSourceFB.delete(Tag4RecipeFB(tag4Recipe, tag4Recipe.recipeID))
    }

    companion object : SingletonHolder<TagRepository, PingwinekCooksApplication>(::TagRepository)

}