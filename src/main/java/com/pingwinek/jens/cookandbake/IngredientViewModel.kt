package com.pingwinek.jens.cookandbake

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class IngredientViewModel(application: Application) : AndroidViewModel(application) {

    private val ingredientRepository = IngredientRepository.getInstance(application)

    val ingredientData = MutableLiveData<Ingredient>()

    val isEditable = MutableLiveData<Boolean>()

    init {
        isEditable.value = false
    }

    fun newIngredient(recipeId: Int) {
        ingredientData.value = Ingredient(null, recipeId, null, null, "")
    }

    fun loadData(ingredientId: Int) {
        ingredientRepository.getIngredient(ingredientId) { ingredient ->
            ingredientData.postValue(ingredient)
        }
    }

    fun save(name: String, quantity: Double?, unity: String?) {

        // input validation could be moved to something separate
        if (name == null || name.length == 0) {
            return
        }

        var ingredient = ingredientData.value

        /*
        / if the viewmodel does not contain a recipe, we didn't get any from the server
         */
        ingredient?.let {

            // create a new recipe, when id is null
            if (it.id == null) {
                val _ingredient = Ingredient(null, it.recipeId, quantity, unity, name)
                ingredientRepository.putIngredient(_ingredient) { ingredientFromResponse ->
                    ingredientData.postValue(ingredientFromResponse)
                }

            // otherwise update existing recipe
            } else {
                val _ingredient = Ingredient(it.id, it.recipeId, quantity, unity, name)
                ingredientRepository.postIngredient(_ingredient) { ingredientFromResponse ->
                    ingredientData.postValue(ingredientFromResponse)
                }
            }
        }
    }

}