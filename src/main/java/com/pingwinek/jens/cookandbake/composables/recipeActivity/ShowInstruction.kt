package com.pingwinek.jens.cookandbake.composables.recipeActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.composables.spacing

@Composable
fun ShowInstruction(
    paddingValues: PaddingValues,
    instruction: String,
    onEditInstruction: () -> Unit
) {
    var showButtons by remember(instruction) { mutableStateOf(instruction.isBlank()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                top = MaterialTheme.spacing.spacerSmall
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .clickable { showButtons = !showButtons },
            text = instruction
        )

        if (showButtons) {
            IconButton(onClick = onEditInstruction) {
                Icon(Icons.Filled.Edit, stringResource(R.string.write_instruction))
            }
        }
    }
}
