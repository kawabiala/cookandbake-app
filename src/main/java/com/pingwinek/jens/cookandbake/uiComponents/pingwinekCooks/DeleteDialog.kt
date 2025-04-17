package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.pingwinek.jens.cookandbake.R

@Composable
fun DeleteDialog(
    message: String,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        text = { Text(message) },
        onDismissRequest = onClose,
        dismissButton = {
            Text(
                modifier = Modifier
                    .clickable { onClose() },
                text = stringResource(R.string.close)
            )
        },
        confirmButton = {
            Text(
                modifier = Modifier
                    .clickable { onDelete() },
                text = stringResource(R.string.delete)
            )
        }
    )
}

@Preview
@Composable
fun PreviewDeleteDialog() {
    DeleteDialog(
        message = "Message",
        onClose = {},
        onDelete = {}
    )
}
