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

    fun generateTag4Recipe(tagID: String, recipeId: String, sort: Int): Tag4Recipe {
        return Tag4RecipeFB(tagID, recipeId, sort)
    }

    suspend fun getAll(): LinkedList<Tag> {
        return LinkedList(tagSourceFB.getAll())
    }

    suspend fun getAllForRecipe(recipeId: String): LinkedList<Tag4Recipe> {
        return LinkedList(tag4RecipeSourceFB.getAllForRecipeId(recipeId))
    }
/*
    suspend fun getAllRecipeIdsForTag(tag: Tag): LinkedList<String> {
        return LinkedList(tagSourceFB.getRecipeIDs(tag))
    }
*/
    suspend fun delete(tag: Tag): Boolean {
        return tagSourceFB.delete(tag as TagFB)
    }

    suspend fun deleteForRecipe(tag4Recipe: Tag4Recipe): Boolean {
        return tag4RecipeSourceFB.delete(Tag4RecipeFB(tag4Recipe.id, tag4Recipe.recipeID, tag4Recipe.sort))
    }

    suspend fun new(label: String, color: String = ""): Tag {
        return tagSourceFB.new(TagFB(label, color))
    }

    suspend fun new(tag4Recipe: Tag4Recipe): Tag4Recipe {
        val tag4RecipeFB = Tag4RecipeFB(tag4Recipe.id, tag4Recipe.recipeID, tag4Recipe.sort)
        return tag4RecipeSourceFB.new(tag4RecipeFB)
    }

    suspend fun update(tag: Tag, label: String, color: String = ""): Tag? {
        return tagSourceFB.update(TagFB(tag.id, label, color))
    }

    suspend fun update(tag4Recipe: Tag4Recipe): Tag4Recipe? {
        val tag4RecipeFB = Tag4RecipeFB(tag4Recipe.id, tag4Recipe.recipeID, tag4Recipe.sort)
        return tag4RecipeSourceFB.update(tag4RecipeFB)
    }

    companion object : SingletonHolder<TagRepository, PingwinekCooksApplication>(::TagRepository)

}