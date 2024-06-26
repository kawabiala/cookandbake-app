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
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PasswordField
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerMedium
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun ResetPasswordView(
    email: String,
    onReset: (password: String) -> Unit,
    onClose: () -> Unit
) {
    val emailLabel = stringResource(R.string.email)
    val passwordLabel = stringResource(R.string.password)
    val caption = stringResource(R.string.lostPassword)
    val buttonCloseText = stringResource(R.string.close)
    val buttonResetText = stringResource(R.string.changePassword)

    val buttonCloseColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer,
    )

    var passwordTmp by remember {
        mutableStateOf("")
    }

    val onPasswordChange: (String) -> Unit = { passwordTmp = it }

    val onResetClicked: () -> Unit = {
        onReset(passwordTmp)
    }

    Column {
        ProfileHeader(text = caption)

        SpacerSmall()

        TextField(
            label = { Text(emailLabel) },
            value = email,
            enabled = false,
            onValueChange = {}
        )

        SpacerSmall()

        PasswordField(
            label = passwordLabel,
            password = passwordTmp,
            onValueChange = onPasswordChange
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