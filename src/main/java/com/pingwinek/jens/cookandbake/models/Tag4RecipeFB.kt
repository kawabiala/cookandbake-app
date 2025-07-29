package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class Tag4RecipeFB(
    override val id: String = "",
    override val recipeID: String,
    override var lastModified: Long = Date().time
) : Tag4Recipe() {

    @Keep
    data class DocumentData(
        val id: String
    )

    constructor(document: DocumentSnapshot, recipeID: String) : this(
        document.id,
        recipeID
    )

    val documentData = DocumentData(id)

}