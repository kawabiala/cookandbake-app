package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class TagFB(
    override val id: String = "",
    override val label: String,
    override var lastModified: Long = Date().time
): Tag() {

    @Keep
    data class DocumentData(
        val label: String
    )

    constructor(
        label: String
    ) : this(
        "",
        label
    )

    constructor(
        tag: Tag
    ) : this(
        "",
        tag.label
    )

    constructor(document: DocumentSnapshot) : this(
        document.id,
        document.getString("label") ?: "",
        0
    )

    val documentData = DocumentData(label)
}