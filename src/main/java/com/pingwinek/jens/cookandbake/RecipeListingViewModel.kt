package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
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