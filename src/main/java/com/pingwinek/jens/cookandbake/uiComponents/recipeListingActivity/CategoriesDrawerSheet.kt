package com.pingwinek.jens.cookandbake.uiComponents.recipeListingActivity

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pingwinek.jens.cookandbake.R

@Composable
fun CategoriesDrawerSheet(
    categories: List<Pair<String, () -> Unit>>
) {

    ModalDrawerSheet{

        val lazyListState = rememberLazyListState()

        NavigationDrawerItem(
            icon = {},
            label = { Text(
                text = stringResource(R.string.labels),
                style = MaterialTheme.typography.headlineMedium
            )},
            selected = false,
            onClick = {}
        )

        HorizontalDivider()

        LazyColumn(
            state = lazyListState
        ) {
            items(
                items = categories
            ) { category ->
                NavigationDrawerItem(
                    icon = {},
                    label = { Text(category.first) },
                    selected = false,
                    onClick = category.second
                )
            }
        }
    }
}