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
    override val sort: Int,
    override val isGroupHeader: Boolean = false,
    override var lastModified: Long = Date().time
) : Ingredient() {

    @Keep
    data class DocumentData(
        val quantity: Double?,
        val quantityVerbal: String?,
        val unity: String?,
        val name: String,
        val sort: Int,
        @field:JvmField val isGroupHeader: Boolean
    )

    constructor(
        recipeId: String,
        quantity: Double?,
        quantityVerbal: String?,
        unity: String?,
        name: String,
        sort: Int,
        isGroupHeader: Boolean
    ) : this(
        "",
        recipeId,
        quantity,
        quantityVerbal,
        unity,
        name,
        sort,
        isGroupHeader
    )

    constructor(document: DocumentSnapshot, recipeId: String) : this(
        document.id,
        recipeId,
        document.getDouble("quantity"),
        document.getString("quantityVerbal"),
        document.getString("unity"),
        document.getString("name") ?: "",
        document.getLong("sort")?.toInt() ?: -1,
        document.getBoolean("isGroupHeader") ?: false
    )

    val documentData = DocumentData(quantity, quantityVerbal, unity, name, sort, isGroupHeader)
}