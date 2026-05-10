package com.pingwinek.jens.cookandbake.models

import android.net.Uri

data class ImageInfo(
    val imageId: String,
    val imageName: String,
    val downloadUri: Uri
)
