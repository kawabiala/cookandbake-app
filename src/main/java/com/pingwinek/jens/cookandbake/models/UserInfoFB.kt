package com.pingwinek.jens.cookandbake.models

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentSnapshot

data class UserInfoFB(
    override val id: String,
    override val crashlyticsEnabled: Boolean,
    override var lastModified: Long // keep for consistency with abstract base class Recipe and the interface Model
) : UserInfo() {

    @Keep
    data class DocumentData(
        val crashlyticsEnabled: Boolean
    )

    constructor(
        crashlyticsEnabled: Boolean
    ) : this(
        "",
        crashlyticsEnabled,
        0
    )

    constructor(
        id: String,
        crashlyticsEnabled: Boolean
    ) : this(
        id,
        crashlyticsEnabled,
        0
    )

    constructor(document: DocumentSnapshot) : this(
        document.id,
        document.getBoolean("crashlyticsEnabled") ?: false,
        0
    )

    val documentData = DocumentData(crashlyticsEnabled)
}