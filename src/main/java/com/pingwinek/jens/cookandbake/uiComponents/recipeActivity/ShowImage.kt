package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.models.ImageInfo
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun ShowImage(
    paddingValues: PaddingValues,
    imageInfo: ImageInfo?,
    updateImageName: (String) -> Unit
) {
    var isEditImageName by remember { mutableStateOf(false) }

    val bgColor = MaterialTheme.colorScheme.primaryContainer

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {

        if (imageInfo != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor),
                horizontalArrangement = Arrangement.Center,
            ) {
                AsyncImage(
                    model = imageInfo.downloadUri,
                    contentDescription = imageInfo.imageName,
                    placeholder = painterResource(R.drawable.modern_flat_icon_landscape_203633_11062),
                    error = painterResource(R.drawable.modern_flat_icon_landscape_203633_11062),
                    modifier = Modifier
                )
            }

            SpacerSmall()

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditImageName) {
                    EditImageName(
                        imageInfo = imageInfo,
                        saveImageName = { imageName ->
                            updateImageName(imageName)
                            isEditImageName = false
                        }
                    )
                } else {
                    ShowImageName(
                        imageInfo = imageInfo,
                        toggleIsEditImageName = { isEditImageName = true },
                    )
                }
            }
        }
    }
}

@Composable
fun ShowImageName(
    imageInfo: ImageInfo,
    toggleIsEditImageName: () -> Unit
) {
    Text(text = imageInfo.imageName)

    IconButton(
        onClick = toggleIsEditImageName
    ) {
        Icon(Icons.Filled.Edit, stringResource(R.string.edit_image_name))
    }
}

@Composable
fun EditImageName(
    imageInfo: ImageInfo,
    saveImageName: (String) -> Unit
) {
    var imageName by remember { mutableStateOf(imageInfo.imageName) }

    val onSave = {
        saveImageName(imageName)
    }

    TextField(
        value = imageName,
        onValueChange = { changedString ->
            imageName = changedString
        }
    )

    IconButton(
        onClick = onSave
    ) {
        Icon(Icons.Filled.Check, stringResource(R.string.save_image_name))
    }
}

@Preview(showBackground = true, showSystemUi = false, device = "id:pixel_9")
@Composable
fun ShowImagePreview() {
    ShowImage(
        paddingValues = PaddingValues.Absolute(10.dp),
        imageInfo = ImageInfo(
            imageId = "imageId",
            downloadUri = "https://example.com/image.jpg".toUri(),
            imageName = "Delicious Cake"
        ),
        updateImageName = {}
    )
}

