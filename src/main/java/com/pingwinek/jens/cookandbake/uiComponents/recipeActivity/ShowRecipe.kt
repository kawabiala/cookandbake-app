package com.pingwinek.jens.cookandbake.uiComponents.recipeActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun ShowRecipe(
    paddingValues: PaddingValues,
    recipeTitle: String,
    recipeDescription: String,
    labels: List<String>,
    hasAttachment: Boolean,
    isAttachmentLoading: Boolean,
    onAttachmentClicked: () -> Unit,
) {

    Column(
        modifier = Modifier
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(70f)
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = recipeTitle
                )

                if (recipeDescription.isNotEmpty()) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        text = recipeDescription
                    )
                }

                if (isAttachmentLoading) {
                    CircularProgressIndicator()
                } else if (hasAttachment) {
                    IconButton(
                        onClick = onAttachmentClicked
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Attachment,
                            contentDescription = stringResource(R.string.show_attachment)
                        )
                    }
                }
            }
        }

        SpacerSmall()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (labels.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    labels.forEach { label ->
                        InputChip(
                            selected = true,
                            onClick = {},
                            label = {
                                Text(
                                    text = label,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier
                                .requiredWidthIn(60.dp, 90.dp),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }
    }
}
