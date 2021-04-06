package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.lifecycle.*
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.ShareableRecipe
import com.pingwinek.jens.cookandbake.lib.networkRequest.NetworkRequest
import com.pingwinek.jens.cookandbake.models.File
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.repos.FileRepository
import com.pingwinek.jens.cookandbake.repos.IngredientRepository
import com.pingwinek.jens.cookandbake.repos.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.NoSuchElementException

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)
    private val ingredientRepository = IngredientRepository.getInstance(application as PingwinekCooksApplication)
    private val fileRepository = FileRepository.getInstance(application as PingwinekCooksApplication)
    private val contentResolver = application.contentResolver

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

    val fileListData: LiveData<LinkedList<File>> = Transformations.map(fileRepository.fileListData) { fileList ->
        LinkedList(fileList.filter { file ->
            file.entityId == recipeId && file.entity == "recipe"
        })
    }

    val file = MutableLiveData<ParcelFileDescriptor>()

    fun delete() {
        recipeId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.deleteRecipe(it)
            }
        }
    }

    fun deleteIngredient(ingredientId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            ingredientRepository.deleteIngredient(ingredientId)
            loadData()
        }
    }

    fun deletePdf() {
        recipeData.value?.id?.let {
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.deletePdf(it)
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

    fun loadData() {
        recipeId?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                recipeRepository.getRecipe(id)
                ingredientRepository.getAll(id)
                fileRepository.getFilesForEntityId("recipe", id)
            }
        }
    }

    fun loadFile(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val parcelFileDescriptor = fileRepository.loadFile(name)
            parcelFileDescriptor?.let { file.postValue(it) }
        }
    }

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

        if (recipeId == null) {
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
}