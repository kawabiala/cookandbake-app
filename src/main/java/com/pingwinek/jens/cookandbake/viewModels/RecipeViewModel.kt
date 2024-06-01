package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.ShareableRecipe
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.repos.IngredientRepository
import com.pingwinek.jens.cookandbake.repos.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)
    private val ingredientRepository = IngredientRepository.getInstance(application as PingwinekCooksApplication)

    private val privateRecipeData = MutableLiveData<Recipe>()
    private val privateIngredientListData = MutableLiveData<LinkedList<Ingredient>>().apply {
        value = LinkedList()
    }

    val recipeData: LiveData<Recipe> = privateRecipeData
    val ingredientListData: LiveData<LinkedList<Ingredient>> = privateIngredientListData

    var recipeId: String? = null

    fun attachDocument(uri: Uri) {
        recipeData.value?.let { recipe ->
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.attachDocument(recipe, uri)
            }
        }
    }

    //TODO update recipeData and ingredientListData
    fun deleteRecipe() {
        recipeData.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.delete(it)
                ingredientListData.value?.let { ingredients ->
                    ingredients.forEach {
                        ingredientRepository.delete(it)
                    }
                }
            }
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            ingredientRepository.delete(ingredient)
            loadIngredients()
        }
    }
/*
    fun deletePdf() {
        recipeData.value?.id?.let {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.deletePdf(it)
            }
        }
    }
*/
    fun getShareableRecipe(): ShareableRecipe? {
        return recipeData.value?.let { recipe ->
            ingredientListData.value?.let { ingredients ->
                ShareableRecipe(recipe, ingredients)
            }
        }
    }
/*
    private fun getFile() : File? {
        return try {
            fileListData.value?.first
        } catch (exception: NoSuchElementException) {
            null
        }
    }

    fun hasRecipeImage() : Boolean {
        return getFile() != null
    }
*/
    fun loadData() {
        recipeId?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                privateRecipeData.postValue(recipeRepository.get(id))
                loadIngredients()
                //fileRepository.getFilesForEntityId("recipe", id)
            }
        }
    }

    private suspend fun loadIngredients() {
        recipeId?.let { id ->
            privateIngredientListData.postValue(ingredientRepository.getAll(id))
        }
    }

/*
    fun loadFile(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val parcelFileDescriptor = fileRepository.loadFile(name)
            parcelFileDescriptor?.let { file.postValue(it) }
        }
    }

 */
/*
    fun savePdf(pdfUri: Uri) {
        val pfd = contentResolver.openFileDescriptor(pdfUri, "rw") ?: return
        val type = contentResolver.getType(pdfUri) ?: return
        val contentType = NetworkRequest.ContentType.find(type) ?: return

        val fileName = getFile()?.fileName

        recipeId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                if (fileName == null) {
                    fileRepository.newFile("recipe", it, pfd, contentType)
                } else {
                    fileRepository.updateFile(fileName, pfd)
                }
            }
        }
    }

 */
    fun saveRecipe(title: String, description: String) {
        saveRecipe(title, description, recipeData.value?.instruction ?: "")
    }

    fun saveRecipe(title: String, description: String, instruction: String) {
        if (title.isEmpty()) {
            return
        }

        recipeData.value?.let {recipe ->
            viewModelScope.launch(Dispatchers.IO) {
                privateRecipeData.postValue(recipeRepository.updateRecipe(recipe, title, description, instruction))
            }
        } ?: run {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.newRecipe(title, description, instruction).let { recipe ->
                    recipeId = recipe.id
                    privateRecipeData.postValue(recipe)
                }
            }
        }
    }

    fun saveIngredient(id: String?, name: String, quantity: Double?, quantityVerbal:String?, unity: String?, sort: Int) {
        if (name.isEmpty()) {
            return
        }

        recipeData.value?.let { recipe ->
            viewModelScope.launch(Dispatchers.IO) {
                if (id.isNullOrEmpty()) {
                    ingredientRepository.new(recipe.id, quantity, quantityVerbal, unity, name, sort)
                } else {
                    ingredientListData.value?.find { ingredient ->
                        ingredient.id == id
                    }?.let { ingredient ->
                        ingredientRepository.update(ingredient, quantity, quantityVerbal, unity, name, sort)
                    }
                }
                loadIngredients()
            }
        }
    }

    fun bulkUpdateIngredients(updateMap: Map<Ingredient, Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            updateMap.forEach { (ingredient, sort) ->
                updateIngredient(ingredient, sort)
            }
            loadIngredients()
        }
    }

    private suspend fun updateIngredient(ingredient: Ingredient, sort: Int) {
        ingredientRepository.update(
            ingredient,
            ingredient.quantity,
            ingredient.quantityVerbal,
            ingredient.unity,
            ingredient.name,
            sort
        )
    }
}