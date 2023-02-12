package com.jet.feature.search.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.core.ui.theme.WasimTheme
import com.wasim.feature.search.R.string
import com.jet.search.presentation.model.PhotoUiModel

@Composable
fun PhotoList(
    photos: List<PhotoUiModel>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = getPadding())
            .testTag(stringResource(string.search_list)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(items = photos) { item ->
            PhotoListItem(photo = item, onClick = onItemClick)
        }
    }
}

@Composable
private fun getPadding(): Dp {
    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }

    val configuration = LocalConfiguration.current

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }

    return when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            WasimTheme.spacing.spacing80
        }
        else -> {
            WasimTheme.spacing.spacing0
        }
    }
}
