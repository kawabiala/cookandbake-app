package com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R

@Composable
fun EditPane(
    paddingValues: PaddingValues,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
    ) {
        SpacerSmall()

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .clickable { onCancel() },
                text = stringResource(R.string.close)
            )
            Text(
                modifier = Modifier
                    .clickable { onSave() },
                text = stringResource(R.string.save)
            )
        }

        SpacerMedium()

        content()
    }
}
