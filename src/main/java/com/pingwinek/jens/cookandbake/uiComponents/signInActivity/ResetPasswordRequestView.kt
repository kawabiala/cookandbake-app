package com.pingwinek.jens.cookandbake.uiComponents.signInActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerMedium
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun ResetPasswordRequestView(
    email: String = "",
    onResetRequest: (email: String) -> Unit,
    onClose: () -> Unit
) {
    val emailLabel = stringResource(R.string.email)
    val caption = stringResource(R.string.sendPasswordRequest)
    val buttonCloseText = stringResource(R.string.close)
    val buttonResetText = stringResource(R.string.changePassword)

    val buttonCloseColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer,
    )

    var emailTmp by remember(email) {
        mutableStateOf(email)
    }

    val onEmailChange: (String) -> Unit = { emailTmp = it }

    val onResetClicked: () -> Unit = {
        onResetRequest(emailTmp)
    }

    Column {
        ProfileHeader(text = caption)

        SpacerSmall()

        TextField(
            label = { Text(emailLabel) },
            value = "",
            onValueChange = onEmailChange
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
                onClick = onResetClicked
            ) {
                Text(buttonResetText)
            }
        }
    }
}