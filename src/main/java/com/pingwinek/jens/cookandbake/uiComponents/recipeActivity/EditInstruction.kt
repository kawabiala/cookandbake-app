package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.EditPane

@Composable
fun EditInstruction(
    paddingValues: PaddingValues,
    instruction: String,
    onCancel: () -> Unit,
    onSave: (String) -> Unit
) {
    var instructionTemp by remember {
        mutableStateOf(instruction)
    }

    val onSave: () -> Unit = {
        onSave(instructionTemp)
    }

    EditPane(
        paddingValues = paddingValues,
        onCancel = onCancel,
        onSave = onSave
    ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                value = instructionTemp,
                minLines = 2,
                onValueChange = { changedString ->
                    instructionTemp = changedString
                }
            )
    }
}

@Preview(showBackground = true)
@Composable
fun EditInstructionPreview() {
    EditInstruction(
        paddingValues = PaddingValues(),
        instruction = "Slice the onions and fry them until golden brown.",
        onCancel = {},
        onSave = {}
    )
}

