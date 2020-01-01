package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pingwinek.jens.cookandbake.repos.RecipeRepository

class RecipeListingViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application)
    val recipeListData = recipeRepository.recipeListData

    fun loadData() {
        recipeRepository.getAll()
    }
}