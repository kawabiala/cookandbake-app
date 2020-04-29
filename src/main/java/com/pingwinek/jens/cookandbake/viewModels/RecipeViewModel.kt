package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.ShareableRecipe
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.repos.IngredientRepository
import com.pingwinek.jens.cookandbake.repos.RecipeRepository
import java.util.*

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)
    private val ingredientRepository = IngredientRepository.getInstance(application as PingwinekCooksApplication)

    var recipeId: Int? = null

    val recipeData: LiveData<Recipe?> = Transformations.map(recipeRepository.recipeListData) { recipeList ->
        recipeId?.let {
            recipeList.find { recipe ->
                recipe.id == recipeId
            }
        }
    }

    val ingredientListData: LiveData<LinkedList<Ingredient>> = Transformations.map(ingredientRepository.ingredientListData) { ingredientList ->
        LinkedList(ingredientList.filter { ingredient ->
            ingredient.recipeId == recipeId
        })
    }

    fun loadData() {
        recipeId?.let { id ->
            recipeRepository.getRecipe(id)
            ingredientRepository.getAll(id)
        }
    }

    fun saveRecipe(title: String, description: String, instruction: String) {

        if (title.isEmpty()) {
            return
        }

        recipeId?.let {
            println("Recipe update")
            recipeRepository.updateRecipe(it, title, description, instruction)
            return
        }

        if (recipeId == null) {
            recipeRepository.newRecipe(title, description, instruction) { newRecipeId ->
                recipeId = newRecipeId
                true
            }
        }
    }

    fun saveIngredient(id: Int?, name: String, quantity: Double?, quantityVerbal:String?, unity: String?) {
        if (name.isEmpty()) {
            return
        }

        if (recipeId == null){
            return
        }

        if (id == null) {
            recipeId?.let {
                ingredientRepository.new(it, quantity, quantityVerbal, unity, name) {
                    true
                }
            }
        } else {
            ingredientRepository.update(id, quantity, quantityVerbal, unity, name)
        }
    }

    fun deleteIngredient(ingredientId: Int) {
        ingredientRepository.deleteIngredient(ingredientId) {
            loadData()
        }
    }

    fun delete() {
        recipeId?.let {
            recipeRepository.deleteRecipe(it) {}
        }
    }

    fun getShareableRecipe(): ShareableRecipe? {
        return recipeData.value?.let { recipe ->
            ingredientListData.value?.let { ingredients ->
                ShareableRecipe(recipe, ingredients)
            }
        }
    }

}