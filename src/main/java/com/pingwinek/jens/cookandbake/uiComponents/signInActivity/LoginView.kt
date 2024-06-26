package com.pingwinek.jens.cookandbake.uiComponents.signInActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PasswordField
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerMedium
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun LoginView(
    email: String = "",
    onResetPassword: () -> Unit,
    onLogin: (email: String, password: String) -> Unit,
    onClose: () -> Unit
) {
    val emailLabel = stringResource(R.string.email)
    val passwordLabel = stringResource(R.string.password)
    val resetPasswordText = stringResource(R.string.lostPassword)
    val buttonCloseText = stringResource(R.string.close)
    val buttonLoginText = stringResource(R.string.login)

    val buttonCloseColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer,
    )

    var emailTmp by remember(email) {
        mutableStateOf(email)
    }

    var passwordTmp by remember {
        mutableStateOf("")
    }

    val onEmailChange: (String) -> Unit = { emailTmp = it }
    val onPasswordChange: (String) -> Unit = { passwordTmp = it }

    val onLoginClicked: () -> Unit = {
        onLogin(emailTmp.trim(), passwordTmp.trim())
    }

    Column {
        TextField(
            label = { Text(emailLabel) },
            value = emailTmp,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = onEmailChange
        )

        SpacerSmall()

        PasswordField(
            label = passwordLabel,
            password = passwordTmp,
            showResetPassword = true,
            resetPasswordText = resetPasswordText,
            onResetPassword = onResetPassword,
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
                onClick = onLoginClicked
            ) {
                Text(buttonLoginText)
            }
        }
    }
}