package com.pingwinek.jens.cookandbake.composables.recipeActivity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import com.pingwinek.jens.cookandbake.lib.PingwinekCooksComposables

@Composable
fun EditInstruction(
    paddingValues: PaddingValues,
    instruction: String,
    onInstructionChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    PingwinekCooksComposables.EditPane(
        paddingValues = paddingValues,
        onCancel = onCancel,
        onSave = onSave
    ) {
        TextField(
            value = instruction,
            onValueChange = { changedString ->
                onInstructionChange(changedString)
            }
        )
    }
}
