package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Recipe
import com.pingwinek.jens.cookandbake.repos.RecipeRepository

class RecipeListingViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)
    val authService = (application as PingwinekCooksApplication).getServiceLocator().getService(AuthService::class.java)

    val recipeListData = recipeRepository.recipeListData

/*
    val searchIndex = Transformations.map(recipeRepository.recipeListData) { recipeList ->
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
        recipeRepository.checkForUpdates(force)
    }
}