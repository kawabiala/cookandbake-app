package com.pingwinek.jens.cookandbake.repos

import com.pingwinek.jens.cookandbake.PingwinekCooksApplication
import com.pingwinek.jens.cookandbake.models.Ingredient
import com.pingwinek.jens.cookandbake.models.IngredientFB
import com.pingwinek.jens.cookandbake.sources.IngredientSourceFB
import com.pingwinek.jens.cookandbake.utils.SingletonHolder
import java.util.LinkedList

class IngredientRepository private constructor(val application: PingwinekCooksApplication) {

    private val ingredientSourceFB = application.getServiceLocator().getService(IngredientSourceFB::class.java)

    suspend fun getAll(recipeId: String): LinkedList<Ingredient> {
        return LinkedList(ingredientSourceFB.getAllForRecipeId(recipeId))
    }

    suspend fun delete(ingredient: Ingredient) {
        ingredientSourceFB.delete(ingredient as IngredientFB)
    }

    suspend fun new(
        recipeId: String,
        quantity: Double?,
        quantityVerbal: String?,
        unity: String?,
        name: String
    ) : IngredientFB {
        return ingredientSourceFB.new(IngredientFB(recipeId, quantity, quantityVerbal, unity, name))
    }

    suspend fun update(
        ingredient: Ingredient,
        quantity: Double?,
        quantityVerbal: String?,
        unity: String?,
        name: String
    ): Ingredient {
        return ingredientSourceFB.update(
            IngredientFB(
                ingredient.id,
                ingredient.recipeId,
                quantity,
                quantityVerbal,
                unity,
                name
            )
        )
    }

    companion object : SingletonHolder<IngredientRepository, PingwinekCooksApplication>(::IngredientRepository)

}