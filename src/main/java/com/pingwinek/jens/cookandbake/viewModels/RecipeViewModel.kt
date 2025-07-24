package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.ShareableRecipe
import com.pingwinek.jens.cookandbake.lib.TypedQueue
import com.pingwinek.jens.cookandbake.models.FileInfo
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.models.Tag4Recipe
import com.pingwinek.jens.cookandbake.repos.IngredientRepository
import com.pingwinek.jens.cookandbake.repos.RecipeRepository
import com.pingwinek.jens.cookandbake.repos.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class RecipeViewModel(application: Application) : AndroidViewModel(application), TypedQueue.QueueListener {

    data class TagHelper(
        val tagID: String,
        val label: String,
        val color: String,
        var sort: Int,
        var isNew: Boolean = false,
        var isUpdated: Boolean = false,
        var isDeleted: Boolean = false
    )

    private val getString: (id: Int) -> String = { id ->
        application.getString(id)
    }

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)
    private val ingredientRepository = IngredientRepository.getInstance(application as PingwinekCooksApplication)
    private val tagRepository = TagRepository.getInstance(application as PingwinekCooksApplication)

    private val privateRecipeData = MutableLiveData<Recipe>()
    private val privateRecipeAttachment = MutableLiveData<FileInfo?>()
    private val privateIngredientListData = MutableLiveData<LinkedList<Ingredient>>().apply {
        value = LinkedList()
    }
    private val privateAvailableTagListData = MutableLiveData<LinkedList<Tag>>().apply {
        value = LinkedList()
    }
    private val privateAttachedTagListData = MutableLiveData<LinkedList<TagHelper>>().apply {
        value = LinkedList()
    }

    private val privateMessage = MutableLiveData<String?>()
    private val privateIsUpOrDownloading = MutableLiveData(false)

    val recipeData: LiveData<Recipe> = privateRecipeData
    val recipeAttachment: LiveData<FileInfo?> = privateRecipeAttachment
    val ingredientListData: LiveData<LinkedList<Ingredient>> = privateIngredientListData
    val availableTagListData: LiveData<LinkedList<Tag>> = privateAvailableTagListData
    val attachedTagListData: LiveData<LinkedList<TagHelper>> = privateAttachedTagListData

    val message: LiveData<String?> = privateMessage
    val isUpOrDownLoading: LiveData<Boolean> = privateIsUpOrDownloading

    var recipeId: String? = null

    init {
        recipeRepository.registerQueueListener(this)
    }
/*
    private fun addTag(tag4Recipe: Tag4Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            tagRepository.new(tag4Recipe)
        }
    }
*/
    fun attachDocument(uri: Uri) {
        privateIsUpOrDownloading.postValue(true)
        recipeData.value?.let { recipe ->
            viewModelScope.launch(Dispatchers.IO) {
                privateRecipeData.postValue(
                    recipeRepository.saveAttachment(recipe, uri)
                )
                privateIsUpOrDownloading.postValue(false)
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

    fun deleteAttachment() {
        recipeData.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                privateRecipeData.postValue(recipeRepository.deleteAttachment(it))
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
/*
    fun deleteTag4Recipe(tag4Recipe: Tag4Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            tagRepository.deleteForRecipe(tag4Recipe)
            loadTags4Recipe()
        }
    }
*/
    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch(Dispatchers.IO) {
            ingredientRepository.delete(ingredient)
            loadIngredients()
        }
    }
/*
    private fun deleteTag(tag4Recipe: Tag4Recipe) {
        viewModelScope.launch(Dispatchers.IO) {
            tagRepository.deleteForRecipe(tag4Recipe)
        }
    }
*/
    fun generateTag4Recipe(tag: TagHelper, sort: Int): Tag4Recipe? {
        return recipeId?.let { tagRepository.generateTag4Recipe(tag.tagID, it, sort) }
    }

    fun getShareableRecipe(): ShareableRecipe? {
        return recipeData.value?.let { recipe ->
            ingredientListData.value?.let { ingredients ->
                ShareableRecipe(recipe, ingredients)
            }
        }
    }

    fun loadAttachment() {
        privateIsUpOrDownloading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            recipeData.value?.let {  recipe: Recipe ->
                val fileInfo = recipeRepository.getAttachment(recipe)
                fileInfo?.let {
                    privateRecipeAttachment.postValue(it)
                }
            }
            privateIsUpOrDownloading.postValue(false)
        }
    }

    fun loadData() {
        recipeId?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                privateRecipeData.postValue(recipeRepository.get(id))
                loadIngredients()
                loadTags()
//                loadTags4Recipe()
                //fileRepository.getFilesForEntityId("recipe", id)
            }
        }
    }

    private suspend fun loadIngredients() {
        recipeId?.let { id ->
            privateIngredientListData.postValue(ingredientRepository.getAll(id))
        }
    }

    private suspend fun loadTags() {
        val tags = tagRepository.getAll()

        recipeId?.let { id ->
            val tagHelperList = mutableListOf<TagHelper>()

            tagRepository.getAllForRecipe(id).forEach { tag4Recipe ->
                val nullableTag = tags.find { t -> t.id == tag4Recipe.id }

                if (nullableTag == null) {
                    // if no corresponding tags are found, tag4Recipe is no valid tag anymore and will be deleted
                    tagRepository.deleteForRecipe(tag4Recipe)
                } else {
                    tagHelperList.add(
                        TagHelper(
                            tag4Recipe.id,
                            nullableTag.label,
                            nullableTag.color,
                            tag4Recipe.sort
                        )
                    )
                }

                /*
                tags.find { t -> t.id == tag4Recipe.id }?.let { nonNullTag ->
                    tagHelperList.add(
                        TagHelper(
                            tag4Recipe.id,
                            nonNullTag.label,
                            nonNullTag.color,
                            tag4Recipe.sort
                        )
                    )
                }
                */
            }

            privateAttachedTagListData.postValue(LinkedList(tagHelperList))
        }

        privateAvailableTagListData.postValue(tagRepository.getAll())
    }
/*
    private suspend fun loadTags4Recipe() {
        recipeId?.let { id ->
            privateTag4RecipeListData.postValue(tagRepository.getAllForRecipe(id))
        }
    }
*/
    override fun onCleared() {
        super.onCleared()
        recipeRepository.unregisterQueueListener(this)
    }

    override fun onNewItem() {
        val exceptionMessage = recipeRepository.getRecipeExceptionMessageQueue().getLatest()
        exceptionMessage?.let { privateMessage.postValue(mapActionMessage(it)) }
    }

    fun resetMessage() {
        privateMessage.postValue(null)
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

    fun saveTags(tags: List<TagHelper>) {
        viewModelScope.launch(Dispatchers.IO) {
            recipeId?.let { nonNullRecipeId ->

                tags.forEach { tag ->
                    val tag4Recipe =
                        tagRepository.generateTag4Recipe(tag.tagID, nonNullRecipeId, tag.sort)

                    if (tag.isNew) {
                        tagRepository.new(tag4Recipe)
                    } else if (tag.isUpdated) {
                        tagRepository.update(tag4Recipe)
                    } else if (tag.isDeleted) {
                        tagRepository.deleteForRecipe(tag4Recipe)
                    }
                }
                /*
            attachedTagListData.value!!.forEach { tagOld ->
                if (tags.find { tagNew -> tagOld.label == tagNew.label } == null) {
                    deleteTag(tagOld)
                }
            }

            tags.forEach { tagNew ->
                if(attachedTagListData.value != null &&
                    attachedTagListData.value!!.find { tagOld -> tagNew.label == tagOld.label  } == null) {
                    addTag(tagNew, nonNullRecipeId, recipeData.value?.title ?: "")
                }
            }
*/
                loadTags()
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

    private fun mapActionMessage(actionMessage: RecipeRepository.RecipeExceptionMessage): String {
        return when (actionMessage) {
            RecipeRepository.RecipeExceptionMessage.ATTACHMENT_DELETE_FAILED -> { getString(R.string.attachmentDeleteFailed) }
            RecipeRepository.RecipeExceptionMessage.ATTACHMENT_DOWNLOAD_FAILED -> { getString(R.string.attachmentDownloadFailed) }
            RecipeRepository.RecipeExceptionMessage.ATTACHMENT_UPLOAD_FAILED -> { getString(R.string.attachmentUploadFailed) }
            RecipeRepository.RecipeExceptionMessage.ATTACHMENT_WITHOUT_NAME_OR_SIZE -> { getString(R.string.attachmentWithoutNameOrSize) }
            RecipeRepository.RecipeExceptionMessage.ATTACHMENT_WITHOUT_TYPE_INFORMATION -> { getString(R.string.attachmentWithoutTypeInformation) }
            RecipeRepository.RecipeExceptionMessage.ATTACHMENT_WITH_UNSUPPORTED_SIZE -> { getString(R.string.attachmentWithUnsupportedSize) }
            RecipeRepository.RecipeExceptionMessage.RECIPE_LIST_LOAD_FAILED -> { getString(R.string.recipeListLoadingFailed) }
            RecipeRepository.RecipeExceptionMessage.RECIPE_UPDATE_FAILED -> { getString(R.string.recipeUpdateFailed) }
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