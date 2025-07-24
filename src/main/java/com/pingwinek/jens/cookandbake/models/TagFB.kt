package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class TagFB(
    override val id: String = "",
    override val label: String,
    override val color: String = "",
    override var lastModified: Long = Date().time
): Tag() {

    @Keep
    data class DocumentData(
        val label: String,
        val color: String
    )

    constructor(
        label: String,
        color: String = ""
    ) : this(
        "",
        label,
        color
    )

    constructor(
        tag: Tag
    ) : this(
        "",
        tag.label,
        tag.color
    )

    constructor(document: DocumentSnapshot) : this(
        document.id,
        document.getString("label") ?: "",
        document.getString("color") ?: "",
        0
    )

    val documentData = DocumentData(label, color)
}