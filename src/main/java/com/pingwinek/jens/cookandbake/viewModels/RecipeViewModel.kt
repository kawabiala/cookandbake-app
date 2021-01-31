package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.ShareableRecipe
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.repos.IngredientRepository
import com.pingwinek.jens.cookandbake.repos.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.*

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)
    private val ingredientRepository = IngredientRepository.getInstance(application as PingwinekCooksApplication)

    var recipeId: Int? = null
    var recipeFileInputStream = MutableLiveData<InputStream>()

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
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.getRecipe(id)
                ingredientRepository.getAll(id)
            }
        }
    }

    fun savePdf(inputStream: InputStream, type: String) {
        recipeFileInputStream.value = inputStream

        recipeId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.saveFile(it, inputStream, type)
            }
/*
            FileManagerRemote(getApplication())
                .saveFile(inputStream, type)
                .setResultHandler { result ->
                    if (result.status == Promise.Status.SUCCESS) {
                        Log.i(this::class.java.name, "Uri: ${result.value}")
                    } else {
                        Log.i(this::class.java.name, "Saving file, but no inputstream provided")
                    }
                }

 */
        }
    }

    fun saveRecipe(title: String, description: String, instruction: String) {

        if (title.isEmpty()) {
            return
        }

        recipeId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.updateRecipe(it, title, description, instruction)
            }
            return
        }

        if (recipeId == null) {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.newRecipe(title, description, instruction) { newRecipeId ->
                    recipeId = newRecipeId
                    true
                }
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
                viewModelScope.launch(Dispatchers.IO) {
                    ingredientRepository.newIngredient(it, quantity, quantityVerbal, unity, name) {
                        true
                    }
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                ingredientRepository.updateIngredient(id, quantity, quantityVerbal, unity, name)
            }
        }
    }

    fun deleteIngredient(ingredientId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            ingredientRepository.deleteIngredient(ingredientId)
            loadData()
        }
    }

    fun delete() {
        recipeId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.deleteRecipe(it)
            }
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