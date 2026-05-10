package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun DeleteDialog(
    deleteTarget: DeleteTarget,
    onClose: () -> Unit,
    onDelete: (DeleteTarget) -> Unit
) {
    com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.DeleteDialog(
        message =  deleteTarget.messageId?.let { stringResource(it) } ?: "",
        onClose = { onClose() },
        onDelete = { onDelete(deleteTarget) }
    )
}
