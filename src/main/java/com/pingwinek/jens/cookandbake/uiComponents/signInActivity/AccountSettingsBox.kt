package com.pingwinek.jens.cookandbake.uiComponents.signInActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.Expandable
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.LabelledSwitch

@Composable
fun AccountSettingsBox(
    crashlyticsEnabled: Boolean,
    onCrashlyticsChange: (Boolean) -> Unit,
    onResetPasswordClicked: () -> Unit,
    onLogout: () -> Unit,
    onDelete: () -> Unit
) {
    Expandable(
        headerText = "Account Settings",
        headerTextStyle = MaterialTheme.typography.headlineMedium,
        headerTextColor = MaterialTheme.colorScheme.onSurface,
        contentTextStyle = MaterialTheme.typography.bodyMedium,
        boxColor = MaterialTheme.colorScheme.surfaceContainerLow,
        padding = Dp(20F)
    ) { contentTextStyle ->
        LabelledSwitch(
            label = stringResource(R.string.acceptCrashlytics),
            labelTextStyle = contentTextStyle,
            checked = crashlyticsEnabled,
            onCheckedChange = onCrashlyticsChange
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        )

        Row(
            modifier = Modifier.clickable { onResetPasswordClicked() }
        ) {
            Text(
                text = stringResource(R.string.lostPassword),
                style = contentTextStyle,
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        )


        Row(
            Modifier.clickable(
                onClick = onLogout
            )
        ) {
            Text(
                text = stringResource(R.string.logout),
                style = contentTextStyle,
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        )

        Row(
            Modifier.clickable(
                onClick = onDelete
            )
        ) {
            Text(
                text = stringResource(R.string.delete),
                style = contentTextStyle,
            )
        }
    }
}
