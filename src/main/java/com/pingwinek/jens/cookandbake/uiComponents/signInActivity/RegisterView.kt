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
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.LabelledCheckBox
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.PasswordField
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerMedium
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun RegisterView(
    onRegister: (
        email: String,
        password: String,
        dataProtection: Boolean,
        crashlytics: Boolean
    ) -> Unit,
    onClose: () -> Unit
) {
    val emailLabel = stringResource(R.string.email)
    val passwordLabel = stringResource(R.string.password)
    val dataProtectionLabel = stringResource(R.string.declareAcceptanceOfDataprotection)
    val crashlyticsLabel = stringResource(R.string.acceptCrashlytics)
    val buttonCloseText = stringResource(R.string.close)
    val buttonRegisterText = stringResource(R.string.register)

    val buttonCloseColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.onSecondary,
        disabledContentColor = MaterialTheme.colorScheme.secondaryContainer,
    )

    var emailTmp by remember { mutableStateOf("email") }
    var passwordTmp by remember { mutableStateOf("") }
    var dataProtectionTmp by remember { mutableStateOf(false) }
    var crashlyticsTmp by remember { mutableStateOf(false) }

    val onEmailChange: (String) -> Unit = { emailTmp = it }
    val onPasswordChange: (String) -> Unit = { passwordTmp = it }
    val onDataProtectionChanged: (Boolean) -> Unit = { dataProtectionTmp = it }
    val onCrashlyticsChanged: (Boolean) -> Unit = { crashlyticsTmp = it }

    val onLoginClicked: () -> Unit = {
        onRegister(emailTmp, passwordTmp, dataProtectionTmp, crashlyticsTmp)
    }

    Column {
        TextField(
            label = { Text(emailLabel) },
            value = "",
            onValueChange = onEmailChange
        )

        SpacerSmall()

        PasswordField(
            label = passwordLabel,
            password = "",
            onValueChange = onPasswordChange
        )

        SpacerMedium()

        LabelledCheckBox(
            label = dataProtectionLabel,
            checked = dataProtectionTmp,
            onCheckedChange = onDataProtectionChanged
        )

        SpacerSmall()

        LabelledCheckBox(
            label = crashlyticsLabel,
            checked = crashlyticsTmp,
            onCheckedChange = onCrashlyticsChanged
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
                Text(buttonRegisterText)
            }
        }
    }
}