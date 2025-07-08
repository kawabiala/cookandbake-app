package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class Tag4RecipeFB(
    override val id: String = "",
    override val recipeID: String,
    override val label: String,
    override var lastModified: Long = Date().time
) : Tag4Recipe() {

    @Keep
    data class DocumentData(
        val label: String
    )

    constructor(
        label: String,
        recipeID: String
    ) : this(
        "",
        label,
        recipeID
    )

    constructor(
        tag: Tag,
        recipeID: String
    ) : this (
        tag.id,
        recipeID,
        tag.label
    )

    constructor(document: DocumentSnapshot, recipeID: String) : this(
        document.id,
        recipeID,
        document.getString("label") ?: "",
        0
    )

    val documentData = DocumentData(label)

}