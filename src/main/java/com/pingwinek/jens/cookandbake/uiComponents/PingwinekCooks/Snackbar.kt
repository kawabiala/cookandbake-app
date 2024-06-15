package com.pingwinek.jens.cookandbake.uiComponents.PingwinekCooks

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun Snackbar(
    snackbarMessage: String?,
    onHasShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    SnackbarHost(hostState = snackbarHostState)

    if (snackbarMessage != null) {
        LaunchedEffect(key1 = snackbarHostState) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onHasShown()
        }
    }
}
