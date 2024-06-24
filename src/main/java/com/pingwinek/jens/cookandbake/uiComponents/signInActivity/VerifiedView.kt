package com.pingwinek.jens.cookandbake.uiComponents.signInActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerMedium
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun VerifiedView(
    email: String,
    crashlyticsEnabled: Boolean,
    onCrashlyticsChange: (Boolean) -> Unit,
    onResetPasswordRequest: () -> Unit,
    onLogout: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    val buttonCloseText = stringResource(R.string.close)
    val verifiedText = stringResource(R.string.confirmationSucceeded)

    Column {
        Text(
            text = verifiedText
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
                onClick = onClose
            ) {
                Text(buttonCloseText)
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