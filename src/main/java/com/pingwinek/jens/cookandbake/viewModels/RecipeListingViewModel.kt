package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.repos.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class RecipeListingViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)

    private val privateRecipeListData = MutableLiveData<LinkedList<Recipe>>().apply {
        value = LinkedList()
    }
    val recipeListData: LiveData<LinkedList<Recipe>> = privateRecipeListData.map { mutableList ->
        LinkedList(mutableList)
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            privateRecipeListData.postValue(recipeRepository.getAll())
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