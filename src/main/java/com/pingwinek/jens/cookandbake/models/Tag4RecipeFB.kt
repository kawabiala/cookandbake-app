package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class Tag4RecipeFB(
    override val id: String = "",
    override val recipeID: String,
    override val sort: Int,
    override var lastModified: Long = Date().time
) : Tag4Recipe() {

    @Keep
    data class DocumentData(
        val sort: Int
    )

    constructor(
        recipeID: String,
        sort: Int
    ) : this(
        "",
        recipeID,
        sort
    )
/*
    constructor(
        tag: Tag,
        recipeID: String,
        sort: Int
    ) : this (
        tag.id,
        recipeID,
        sort
    )
*/
    constructor(document: DocumentSnapshot, recipeID: String) : this(
        document.id,
        recipeID,
        document.getLong("sort")?.toInt() ?: -1,
        0
    )

    val documentData = DocumentData(sort)

}