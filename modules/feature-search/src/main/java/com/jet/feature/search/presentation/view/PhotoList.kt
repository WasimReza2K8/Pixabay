package com.jet.feature.search.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.core.ui.theme.WasimTheme
import com.jet.search.presentation.model.PhotoUiModel
import com.wasim.feature.search.R.string

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoList(
    photos: LazyPagingItems<PhotoUiModel>,
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
        items(count = photos.itemCount) { index ->
            photos[index]?.let {
                PhotoListItem(
                    photo = it,
                    onClick = onItemClick,
                    modifier = modifier.animateItemPlacement()
                )
            }
        }
        handlePagination(photos = photos)
    }
}

private fun LazyListScope.handlePagination(
    photos: LazyPagingItems<PhotoUiModel>,
) {
    photos.apply {
        when {
            loadState.refresh is LoadState.Loading -> {
                item { LoadingScreen() }
            }

            loadState.refresh is LoadState.Error -> {
                val error = loadState.refresh as LoadState.Error
                item {
                    ErrorMessage(
                        modifier = Modifier.fillMaxSize(),
                        message = error.error.localizedMessage!!,
                        onClickRetry = { retry() })
                }
            }

            loadState.append is LoadState.Loading -> {
                item {
                    LoadingNextPageItem(modifier = Modifier)
                }
            }

            loadState.append is LoadState.Error -> {
                val error = loadState.append as LoadState.Error
                item {
                    ErrorMessage(
                        modifier = Modifier,
                        message = error.error.localizedMessage!!,
                        onClickRetry = { retry() })
                }
            }
        }
    }
}

@Composable
fun LoadingNextPageItem(modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
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
