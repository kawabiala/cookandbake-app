package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot

data class RecipeFB(
    override val id: String,
    override val title: String,
    override val description: String?,
    override val instruction: String?,
    override val hasAttachment: Boolean = false,
    override var lastModified: Long = 0// keep for consistency with abstract base class Recipe and the interface Model
) : Recipe() {

    @Keep
    data class DocumentData(
        val title: String,
        val description: String?,
        val instruction: String?,
        val hasAttachment: Boolean
    )

    constructor(
        title: String,
        description: String?,
        instruction: String?
    ) : this(
        "",
        title,
        description,
        instruction
    )
/*
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
        false,
        0
    )*/

    constructor(document: DocumentSnapshot) : this(
        document.id,
        document.getString("title") ?: "",
        document.getString("description") ?: "",
        document.getString("instruction") ?: "",
        document.getBoolean("hasAttachment") ?: false
    )

    val documentData = DocumentData(title, description, instruction, hasAttachment)

    override fun getUpdated(recipe: Recipe): RecipeFB {
        return RecipeFB(recipe.id, recipe.title, recipe.description, recipe.instruction, recipe.hasAttachment, recipe.lastModified)
    }
}