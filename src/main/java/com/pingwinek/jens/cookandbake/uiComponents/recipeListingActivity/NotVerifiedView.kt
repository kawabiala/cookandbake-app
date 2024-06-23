package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R

@Composable
fun NotVerifiedView(
    onLogIn: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.emailNotVerified))
        IconButton(
            onClick = onLogIn
        ) {
            Row() {
                Icon(Icons.Outlined.VerifiedUser, stringResource(R.string.verifyEmailNow))
                Text(stringResource(R.string.verifyEmailNow))
            }
        }
    }
}