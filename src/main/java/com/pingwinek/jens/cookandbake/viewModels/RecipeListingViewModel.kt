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
}