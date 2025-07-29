package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R

@Composable
fun DeleteDialog(
    dialogMode: Delete,
    onClose: () -> Unit,
    onDelete: (delete: Delete) -> Unit
) {
    com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.DeleteDialog(
        message = when (dialogMode) {
            Delete.RECIPE -> stringResource(R.string.delete_recipe)
            Delete.INGREDIENT -> stringResource(R.string.delete_ingredient)
            Delete.NONE -> ""
        },
        onClose = { onClose() },
        onDelete = { onDelete(dialogMode) }
    )
}
