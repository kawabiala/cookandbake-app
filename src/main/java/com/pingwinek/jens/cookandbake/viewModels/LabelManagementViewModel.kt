package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.repos.RecipeRepository
import com.pingwinek.jens.cookandbake.repos.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class LabelManagementViewModel(application: Application) : AndroidViewModel(application) {

    data class TagWithCount (
        val tag: Tag,
        var count: Int = 0
    )

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)
    private val tagRepository = TagRepository.getInstance(application as PingwinekCooksApplication)

    private val privateAvailableTagsListData = MutableLiveData<LinkedList<TagWithCount>>().apply {
        value = LinkedList()
    }

    val availableTagListData: LiveData<LinkedList<TagWithCount>> = privateAvailableTagsListData

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val tagsWithCount = tagRepository.getAll().map { entry -> TagWithCount(entry) }
            recipeRepository.getAll().forEach { recipe ->
                tagRepository.getAllForRecipe(recipe.id).forEach { tag ->
                    tagsWithCount.find { tc -> tc.tag.id == tag.id }?.let { tcFound -> tcFound.count += 1 }
                }
            }

            privateAvailableTagsListData.postValue(LinkedList(tagsWithCount))
        }
    }

    fun addLabel(label: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tagRepository.new(label)
            loadData()
        }
    }

    fun deleteLabel(tag: Tag) {
        viewModelScope.launch(Dispatchers.IO) {
            tagRepository.delete(tag)
            loadData()
        }
    }

    fun updateLabel(tag: Tag, label: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tagRepository.update(tag, label)
            loadData()
        }
    }
}