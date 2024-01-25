package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date


data class IngredientFB(
    override val id: String = "",
    override val recipeId: String,
    override val quantity: Double?,
    override val quantityVerbal: String?,
    override val unity: String?,
    override val name: String,
    override var lastModified: Long = Date().time
) : Ingredient() {

    @Keep
    data class DocumentData(
        val quantity: Double?,
        val quantityVerbal: String?,
        val unity: String?,
        val name: String
    )

    constructor(
        recipeId: String,
        quantity: Double?,
        quantityVerbal: String?,
        unity: String?,
        name: String
    ) : this(
        "",
        recipeId,
        quantity,
        quantityVerbal,
        unity,
        name
    )

    constructor(document: DocumentSnapshot, recipeId: String) : this(
        document.id,
        recipeId,
        document.getDouble("quantity"),
        document.getString("quantityVerbal"),
        document.getString("unity"),
        document.getString("name") ?: "",
        0
    )

    val documentData = DocumentData(quantity, quantityVerbal, unity, name)

    override fun getUpdated(ingredient: Ingredient): IngredientFB {
        return IngredientFB(
            id,
            recipeId,
            ingredient.quantity,
            ingredient.quantityVerbal,
            ingredient.unity,
            ingredient.name)
    }
}