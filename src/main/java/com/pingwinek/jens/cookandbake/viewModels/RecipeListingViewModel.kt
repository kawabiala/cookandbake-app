package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.Tag4Recipe
import com.pingwinek.jens.cookandbake.repos.RecipeRepository
import com.pingwinek.jens.cookandbake.repos.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class RecipeListingViewModel(application: Application) : AndroidViewModel(application) {

    data class RecipesForLabel(
        val label: String,
        val recipes: LinkedList<Recipe>
    )

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)
    private val tagRepository = TagRepository.getInstance(application as PingwinekCooksApplication)

    private val privateRecipeListData = MutableLiveData<LinkedList<Recipe>>().apply {
        value = LinkedList()
    }
    private val privateRecipesByLabel = MutableLiveData<LinkedList<RecipesForLabel>>().apply {
        value = LinkedList()
    }

    val recipeListData: LiveData<LinkedList<Recipe>> = privateRecipeListData.map { mutableList ->
        LinkedList(mutableList)
    }
    val recipesByLabelListData: LiveData<LinkedList<Pair<String, LinkedList<Recipe>>>> = privateRecipesByLabel.map { mutableList ->
        LinkedList<Pair<String, LinkedList<Recipe>>>().apply {
            mutableList.forEach { recipe4Label ->
                this.add(
                    Pair(
                        recipe4Label.label,
                        recipe4Label.recipes
                    )
                )
            }
        }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val recipes = recipeRepository.getAll()
            privateRecipeListData.postValue(recipes)

            val recipeTagList: MutableList<Pair<Recipe, Tag4Recipe>> = mutableListOf()
            recipes.forEach { recipe ->
                tagRepository.getAllForRecipe(recipe.id).forEach { tag ->
                    recipeTagList.add(Pair(recipe, tag))
                }
            }

            privateRecipesByLabel.postValue(getAllRecipes4Tags(recipeTagList))
        }
    }

    private suspend fun getAllRecipes4Tags(recipeTagList: List<Pair<Recipe, Tag4Recipe>>) : LinkedList<RecipesForLabel> {
        val returnValue = LinkedList<RecipesForLabel>()

        tagRepository.getAll().forEach { tag ->
            returnValue.add(
                RecipesForLabel(
                    tag.label,
                    LinkedList(recipeTagList.mapNotNull { entry ->
                        if(entry.second.id == tag.id) entry.first else null
                    })
                )
            )
        }

        return returnValue
    }
/*
    private suspend fun getRecipes4Tag(tag: Tag) : LinkedList<Recipe> {
        return LinkedList<Recipe>().apply {
            tagRepository.getAllRecipeIdsForTag(tag).forEach { recipeId ->
                getRecipe(recipeId)?.let { nonNullRecipe ->
                    this.add(nonNullRecipe)
                }
            }
        }
    }
*/
    private fun getRecipe(recipeID: String) : Recipe? {
        return recipeListData.value?.find { recipe ->
            recipe.id == recipeID
        }
    }

    /**
     * Used one time for migrating data Jan. 2024
    fun migrateData() {
        val oldUserId = FirebaseAuth.getInstance().uid?.let { DataMigration.getOldUserId(it) }

        viewModelScope.launch(Dispatchers.IO) {
            DataMigration.recipes.iterate <JSONObject> { recipe, _ ->
                if (recipe.getString("user_id") == oldUserId) {
                    val insertedRecipe = recipeRepository.newRecipe(
                        recipe.getString("title"),
                        recipe.getString("description"),
                        recipe.getString("instruction")
                    )
                    val oldRecipeId = recipe.getString("id")
                    val newRecipeId = insertedRecipe.id
                    DataMigration.ingredients.iterate <JSONObject> { ingredient, _ ->
                        if (ingredient.getString("recipe_id") == oldRecipeId) {
                            ingredientRepository.new(
                                newRecipeId,
                                ingredient.getDouble("quantity"),
                                ingredient.getString("quantity_verbal"),
                                ingredient.getString("unity"),
                                ingredient.getString("name")
                            )
                        }
                    }
                }
            }
        }
    }
     */
}