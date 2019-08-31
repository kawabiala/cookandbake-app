package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.pingwinek.jens.cookandbake.Recipe
import com.pingwinek.jens.cookandbake.RecipeRepository
import java.util.*

class RecipeListingViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application)
    val recipeListData = MutableLiveData<LinkedList<Recipe>>()

    init {
        recipeListData.value = LinkedList()
    }

    fun loadData() {
        recipeRepository.getAll { recipeList ->
            recipeListData.postValue(recipeList)
        }
    }
}