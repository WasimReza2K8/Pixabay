/*
 * Copyright 2021 Wasim Reza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jet.feature.detail.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.ui.theme.WasimTheme
import com.example.core.ui.views.ErrorSnakeBar
import com.example.core.ui.views.PhotoWithInfoView
import com.jet.feature.detail.presentation.viewmodel.DetailContract.Event
import com.jet.feature.detail.presentation.viewmodel.DetailContract.Event.OnBackButtonClicked
import com.jet.feature.detail.presentation.viewmodel.DetailContract.Event.OnErrorSnakeBarDismissed
import com.jet.feature.detail.presentation.viewmodel.DetailContract.State
import com.jet.feature.detail.presentation.viewmodel.DetailViewModel
import com.wasim.feature.detail.R

@Composable
fun DetailScreen(viewModel: DetailViewModel = hiltViewModel()) {
    val state: State by viewModel.viewState.collectAsStateWithLifecycle()
    DetailScreenImpl(
        state = state,
        sendEvent = { viewModel.onUiEvent(it) },
    )
}

@Composable
private fun DetailScreenImpl(
    state: State,
    sendEvent: (event: Event) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    ErrorSnakeBar(
        errorEvent = state.errorEvent,
        snackBarHostState = snackBarHostState,
        sendEvent = sendEvent,
        snakeBarDismissedEvent = OnErrorSnakeBarDismissed,
    )

    Scaffold(scaffoldState = rememberScaffoldState(snackbarHostState = snackBarHostState)) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .testTag(stringResource(id = R.string.detail_title))
        ) {
            IconButton(onClick = { sendEvent(OnBackButtonClicked) }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                )
            }
            val photo = state.photo
            if (photo != null) {
                PhotoWithInfoView(
                    userName = photo.userName,
                    tags = photo.tags,
                    imageUrl = photo.largeImageURL
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        PhotoActivityItem(
                            text = photo.likes.toString(),
                            painter = painterResource(id = R.drawable.detail_ic_like)
                        )
                        PhotoActivityItem(
                            text = photo.comments.toString(),
                            painter = painterResource(id = R.drawable.detail_ic_comment)
                        )
                        PhotoActivityItem(
                            text = photo.downloads.toString(),
                            painter = painterResource(id = R.drawable.detail_ic_download)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoActivityItem(
    text: String,
    painter: Painter,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier
        .wrapContentSize()
        .padding(horizontal = WasimTheme.spacing.spacing4)

    ) {
        Image(painter = painter, contentDescription = "image_activity")
        Text(text = text, color = Color.White)
    }
}

@Preview
@Composable
private fun DetailPreview() {
    DetailScreenImpl(State()) { }
}
