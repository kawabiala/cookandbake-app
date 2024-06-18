package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane

@Composable
fun EditInstruction(
    paddingValues: PaddingValues,
    instruction: String,
    onInstructionChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    EditPane(
        paddingValues = paddingValues,
        onCancel = onCancel,
        onSave = onSave
    ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                value = instruction,
                minLines = 2,
                onValueChange = { changedString ->
                    onInstructionChange(changedString)
                }
            )
    }
}
