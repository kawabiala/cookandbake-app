package com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun PasswordField(
    password: String,
    label: String? = null,
    showResetPassword: Boolean = false,
    resetPasswordText: String = "",
    onValueChange: (String) -> Unit,
    onResetPassword: () -> Unit = {}
) {
    var passwordHidden: Boolean by remember { mutableStateOf(true) }
    TextField(
        value = password,
        textStyle = MaterialTheme.typography.bodyMedium,
        label = {
            if (!label.isNullOrEmpty()) {
                Text(text = label)
            }
        },
        onValueChange = onValueChange,
        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val icon = if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordHidden) "show password" else "hide password"
                Icon(imageVector = icon, contentDescription = description)
            }
        },
        supportingText = {
            if (showResetPassword) {
                Text(
                    modifier = Modifier
                        .clickable { onResetPassword() },
                    text = resetPasswordText
                )
            }
        }
    )
}
