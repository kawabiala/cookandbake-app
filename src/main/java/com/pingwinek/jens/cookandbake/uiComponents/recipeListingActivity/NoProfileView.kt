package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R

@Composable
fun NoProfileView(
    onLogIn: () -> Unit,
    onCheckDataProtection: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.noSignedInUser))
        IconButton(
            onClick = onLogIn
        ) {
            Row() {
                Icon(Icons.Outlined.Person, stringResource(R.string.registerOrLoginNow))
                Text(stringResource(R.string.registerOrLoginNow))
            }
        }
        Text(stringResource(R.string.WantToCheckDataProtection))
        Text(modifier = Modifier
            .clickable { onCheckDataProtection() },
            text = stringResource(R.string.checkNow))
    }
}