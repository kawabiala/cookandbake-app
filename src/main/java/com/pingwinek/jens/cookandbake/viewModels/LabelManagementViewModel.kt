package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Tag
import com.pingwinek.jens.cookandbake.repos.TagRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class LabelManagementViewModel(application: Application) : AndroidViewModel(application) {

    private val tagRepository = TagRepository.getInstance(application as PingwinekCooksApplication)

    private val privateLabelListData = MutableLiveData<LinkedList<Tag>>().apply {
        value = LinkedList()
    }

    val labelListData: LiveData<LinkedList<Tag>> = privateLabelListData

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            privateLabelListData.postValue(tagRepository.getAll())
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

    fun updateLabel(tag: Tag) {
        TODO("update all Tag4Recipes as well")
    }
}