package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.models.Ingredient
import java.util.LinkedList

/**
 * Interface for retrieving and manipulating ingredients
 *
 * @param T a subtype of [Ingredient]
 */
interface IngredientSource<T : Ingredient> :
    Source<T> {

    /**
     * Retrieves all ingredients for a given recipe
     *
     * @param recipeId the id of the recipe, to which the ingredients belong
     * @return a linked list of the given subtype of [Ingredient]
     */
    suspend fun getAllForRecipeId(recipeId: String) : LinkedList<T>
}