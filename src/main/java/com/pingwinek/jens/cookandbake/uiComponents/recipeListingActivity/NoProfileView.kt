package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import com.pingwinek.jens.cookandbake.R
import com.pingwinek.jens.cookandbake.uiComponents.pingwinekCooks.SpacerSmall

@Composable
fun NoProfileView(
    onLogIn: () -> Unit,
    onCheckDataProtection: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.noSignedInUser))

        Button(
            onClick = onLogIn
        ) {
            Row() {
                Icon(Icons.Outlined.Person, stringResource(R.string.registerOrLoginNow))
                Text(stringResource(R.string.registerOrLoginNow))
            }
        }

        SpacerSmall()

        Text(stringResource(R.string.WantToCheckDataProtection))

        Text(modifier = Modifier
            .clickable { onCheckDataProtection() },
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.secondary,
            text = stringResource(R.string.checkNow))
    }
}