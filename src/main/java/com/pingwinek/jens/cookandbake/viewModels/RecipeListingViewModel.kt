package com.pingwinek.jens.cookandbake.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pingwinek.jens.cookandbake.AuthService
import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.repos.RecipeRepository

class RecipeListingViewModel(application: Application) : AndroidViewModel(application) {

    private val recipeRepository = RecipeRepository.getInstance(application as PingwinekCooksApplication)
    val recipeListData = recipeRepository.recipeListData

    val authService = (application as PingwinekCooksApplication).getServiceLocator().getService(AuthService::class.java)

    fun loadData() {
        recipeRepository.getAll()
    }
}