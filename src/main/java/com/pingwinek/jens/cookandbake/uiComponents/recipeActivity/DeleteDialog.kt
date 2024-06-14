package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.Delete

@Composable
fun DeleteDialog(
    dialogMode: Delete,
    onClose: () -> Unit,
    onDelete: (delete: Delete) -> Unit
) {
    AlertDialog(
        text = {
            val msg = when (dialogMode) {
                Delete.RECIPE -> stringResource(R.string.delete_recipe)
                Delete.INGREDIENT -> stringResource(R.string.delete_ingredient)
                Delete.NONE -> ""
            }
            Text(msg)
        },
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
                    .clickable { onDelete(dialogMode) },
                text = stringResource(R.string.delete)
            )
        }
    )
}
