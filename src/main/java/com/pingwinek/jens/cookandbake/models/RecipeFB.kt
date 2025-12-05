package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot

data class RecipeFB(
    override val id: String,
    override val title: String,
    override val description: String?,
    override val instruction: String?,
    override val tags: List<String> = listOf(),
    override val hasAttachment: Boolean = false,
    override var lastModified: Long = 0// keep for consistency with abstract base class Recipe and the interface Model
) : Recipe() {

    @Keep
    data class DocumentData(
        val title: String,
        val description: String?,
        val instruction: String?,
        val tags: List<String>,
        val hasAttachment: Boolean
    )

    constructor(
        title: String,
        description: String?,
        instruction: String?,
        tags: List<String>
    ) : this(
        "",
        title,
        description,
        instruction,
        tags
    )

    constructor(document: DocumentSnapshot) : this(
        document.id,
        document.getString("title") ?: "",
        document.getString("description") ?: "",
        document.getString("instruction") ?: "",
        document.get("tags")?.let { any ->
            val returnValue = mutableListOf<String>()
            if (any is List<*>) {
                any.forEach { item ->
                    returnValue.add(item as? String ?: "")
                }
                returnValue
            } else {
                returnValue
            }
        } ?: listOf(),
        document.getBoolean("hasAttachment") ?: false
    )

    val documentData = DocumentData(title, description, instruction, tags, hasAttachment)

    override fun getUpdated(recipe: Recipe): RecipeFB {
        return RecipeFB(recipe.id, recipe.title, recipe.description, recipe.instruction, recipe.tags, recipe.hasAttachment, recipe.lastModified)
    }
}