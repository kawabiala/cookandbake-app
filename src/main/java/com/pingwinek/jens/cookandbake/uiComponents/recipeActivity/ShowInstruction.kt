package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PingwinekCooksAppTheme
import com.pingwinek.jens.cookandbake.uiComponents.spacing

@Composable
fun ShowInstruction(
    paddingValues: PaddingValues,
    instruction: String,
) {
    val layoutDirection = LocalLayoutDirection.current

    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection),
                top = MaterialTheme.spacing.spacerSmall
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .verticalScroll(scrollState),
            text = instruction
        )
    }
}

@Preview(showBackground = true, name = "Normal Instruction")
@Composable
private fun PreviewShowInstruction() {
    PingwinekCooksAppTheme { // Replace with CookAndBakeTheme if you have one
        ShowInstruction(
            paddingValues = PaddingValues(16.dp),
            instruction = "Preheat the oven to 200°C. Mix flour, eggs, and milk until smooth.",
        )
    }
}

@Preview(showBackground = true, name = "Long Instruction (Scrollable)")
@Composable
private fun PreviewLongShowInstruction() {
    PingwinekCooksAppTheme {
        ShowInstruction(
            paddingValues = PaddingValues(16.dp),
            instruction = "Step 1: Start by gathering all ingredients.\n\n" +
                    "Step 2: Chop the onions and garlic finely.\n\n" +
                    "Step 3: Heat oil in a large pan over medium heat.\n\n" +
                    "Step 4: Sauté the onions until translucent.\n\n" +
                    "Step 5: Add the meat and brown it thoroughly on all sides.\n\n" +
                    "Step 6: Pour in the tomato sauce and simmer for 45 minutes.\n\n" +
                    "Step 7: Boil water in a separate pot for the pasta.\n\n" +
                    "Step 8: Serve hot with parmesan cheese and fresh basil leaves.",
        )
    }
}

@Preview(showBackground = true, name = "Empty Instruction")
@Composable
private fun PreviewEmptyShowInstruction() {
    PingwinekCooksAppTheme {
        ShowInstruction(
            paddingValues = PaddingValues(16.dp),
            instruction = "",
        )
    }
}