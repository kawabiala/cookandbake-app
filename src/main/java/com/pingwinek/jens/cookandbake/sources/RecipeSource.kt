package com.pingwinek.jens.cookandbake.sources

import com.pingwinek.jens.cookandbake.models.Recipe

/**
 * Source for retrieving and manipulating recipes
 *
 * @param T a subtype of [Recipe]
 */
interface RecipeSource<T: Recipe> : Source<T>