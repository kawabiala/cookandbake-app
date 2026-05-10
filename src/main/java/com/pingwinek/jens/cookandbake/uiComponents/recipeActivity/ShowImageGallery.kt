package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.ImageInfo

@Composable
fun ShowImageGallery(
    images: List<ImageInfo>,
    onSelect: (String) -> Unit
) {

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(120.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(items = images) { imageInfo ->
            Log.i("ShowImageGallery", "Uri: ${imageInfo.downloadUri}, name: ${imageInfo.imageName}")

            AsyncImage(
                model = imageInfo.downloadUri,
                contentDescription = imageInfo.imageName,
//                imageLoader = TODO(),
                modifier = Modifier
                    .clickable() {
                        onSelect(imageInfo.imageId)
                    },
//                placeholder = painterResource(R.drawable.modern_flat_icon_landscape_203633_11062),
                error = painterResource(R.drawable.modern_flat_icon_landscape_203633_11062),
//                fallback = TODO(),
//                onLoading = { Log.i("ShowImageGallery", "AsyncImage starts loading: ${imageInfo.imageId}")},
//                onSuccess = TODO(),
                onError = { error -> Log.i("ShowImageGallery", "AsyncImage ends with error: $error")},
//                alignment = TODO(),
//                contentScale = TODO(),
//                alpha = TODO(),
//                colorFilter = TODO(),
//                filterQuality = TODO(),
//                clipToBounds = TODO()
            )
        }
    }
}