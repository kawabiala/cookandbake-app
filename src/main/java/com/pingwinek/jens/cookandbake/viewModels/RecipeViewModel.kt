package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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
    //private val fileRepository = FileRepository.getInstance(application as PingwinekCooksApplication)
    private val contentResolver = application.contentResolver

    var recipeId: String? = null

    val recipeData: LiveData<Recipe?> = recipeRepository.recipeListData.map() { recipeList ->
        recipeId?.let {
            recipeList.find { recipe ->
                recipe.id == recipeId
            }
        }
    }

    val ingredientListData: LiveData<LinkedList<Ingredient>> = ingredientRepository.ingredientListData.map() { ingredientList ->
        LinkedList(ingredientList.filter { ingredient ->
            ingredient.recipeId == recipeId
        })
    }
/*
    val fileListData: LiveData<LinkedList<File>> = fileRepository.fileListData.map() { fileList ->
        LinkedList(fileList.filter { file ->
            file.entityId == recipeId && file.entity == "recipe"
        })
    }

    val file = MutableLiveData<ParcelFileDescriptor>()
*/
    fun delete() {
        recipeId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.deleteRecipe(it)
            }
        }
    }

    fun deleteIngredient(ingredientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ingredientRepository.deleteIngredient(ingredientId)
            loadData()
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
                recipeRepository.getRecipe(id)
                ingredientRepository.getAll(id)
                //fileRepository.getFilesForEntityId("recipe", id)
            }
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

    fun saveRecipe(title: String, description: String, instruction: String) {
        if (title.isEmpty()) {
            return
        }

        recipeId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.updateRecipe(it, title, description, instruction)
            }
        } ?: run {
            viewModelScope.launch(Dispatchers.IO) {
                recipeId = recipeRepository.newRecipe(title, description, instruction)?.id
            }
        }
    }

    fun saveIngredient(id: String?, name: String, quantity: Double?, quantityVerbal:String?, unity: String?) {
        if (name.isEmpty()) {
            return
        }

        if (recipeId == null) {
            return
        }

        if (id == null) {
            recipeId?.let {
                viewModelScope.launch(Dispatchers.IO) {
                    ingredientRepository.newIngredient(it, quantity, quantityVerbal, unity, name)
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                ingredientRepository.updateIngredient(id, quantity, quantityVerbal, unity, name)
            }
        }
    }
}