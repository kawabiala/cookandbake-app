package com.pingwinek.jens.cookandbake.models

import com.google.firebase.firestore.DocumentSnapshot

data class RecipeFB(
    override val id: String,
    override val title: String,
    override val description: String?,
    override val instruction: String?,
    override var lastModified: Long // keep for consistency with abstract base class Recipe and the interface Model
) : Recipe() {

    inner class DocumentData(
        val title: String,
        val description: String?,
        val instruction: String?
    )

    constructor(
        title: String,
        description: String?,
        instruction: String?
    ) : this(
        "",
        title,
        description,
        instruction,
        0
    )

    constructor(
        id: String,
        title: String,
        description: String?,
        instruction: String?
    ) : this(
        id,
        title,
        description,
        instruction,
        0
    )

    constructor(document: DocumentSnapshot) : this(
        document.id,
        document.getString("title") ?: "",
        document.getString("description") ?: "",
        document.getString("instruction") ?: "",
        0
    )

    val documentData = DocumentData(title, description, instruction)

    override fun getUpdated(recipe: Recipe): RecipeFB {
        return RecipeFB(recipe.id, recipe.title, recipe.description, recipe.instruction, recipe.lastModified)
    }
}