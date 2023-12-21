package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.repos.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeListingViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)

    val recipeListData = recipeRepository.recipeListData

/*
    val searchIndex = recipeRepository.recipeListData.map() { recipeList ->
        HashMap<String, Recipe>().apply {
            recipeList.forEach { recipe ->
                recipe.title.split(" ").forEach{ subString ->
                    put(subString, recipe)
                }
            }
        }
    }
*/

    fun loadData() {
        loadData(false)
    }

    fun loadData(force: Boolean) {
        //Log.i(this::class.java.name, "loading Data")
        viewModelScope.launch(Dispatchers.IO) {
            recipeRepository.checkForUpdates(force)
        }
    }
}