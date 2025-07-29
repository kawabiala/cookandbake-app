package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class TagFB(
    override val id: String = "",
    override val label: String,
    override val color: String = "",
    override val sort: Int = -1,
    override var lastModified: Long = Date().time
): Tag() {

    @Keep
    data class DocumentData(
        val label: String,
        val color: String,
        val sort: Int
    )

    constructor(
        label: String,
        color: String = "",
        sort: Int = -1
    ) : this(
        "",
        label,
        color,
        sort
    )

    constructor(
        tag: Tag
    ) : this(
        "",
        tag.label,
        tag.color,
        tag.sort
    )

    constructor(document: DocumentSnapshot) : this(
        document.id,
        document.getString("label") ?: "",
        document.getString("color") ?: "",
        document.getLong("sort")?.toInt() ?: -1,
        0
    )

    val documentData = DocumentData(label, color, sort)
}