package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.lib.sync.Promise
import com.pingwinek.jens.cookandbake.lib.sync.Source
import com.pingwinek.jens.cookandbake.models.Ingredient
import java.util.*

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
     * @return a [Promise] with a linked list of the given subtype of [Ingredient]
     */
    fun getAllForRecipeId(recipeId: Int) : Promise<LinkedList<T>>
}