package com.pingwinek.jens.cookandbake.uiComponents.signInActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerMedium
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun UnverifiedView(
    email: String,
    crashlyticsEnabled: Boolean,
    onCrashlyticsChange: (Boolean) -> Unit,
    onVerify: () -> Unit,
    onResetPasswordRequest: () -> Unit,
    onLogout: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    val buttonCloseText = stringResource(R.string.close)
    val buttonVerifyText = stringResource(R.string.sendVerificationEmail)
    val unverifiedText = stringResource(R.string.verificationNeeded)

    val buttonCloseColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer,
    )

    Column {
        Text(
            text = unverifiedText
        )

        SpacerSmall()

        Text(
            text = email,
        )

        SpacerMedium()

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                colors = buttonCloseColors,
                onClick = onClose
            ) {
                Text(buttonCloseText)
            }

            Button(
                onClick = onVerify
            ) {
                Text(buttonVerifyText)
            }
        }

        SpacerMedium()

        AccountSettingsBox(
            crashlyticsEnabled = crashlyticsEnabled,
            onCrashlyticsChange = onCrashlyticsChange,
            onResetPasswordClicked = onResetPasswordRequest,
            onLogout = onLogout,
            onDelete = onDelete
        )
    }
}