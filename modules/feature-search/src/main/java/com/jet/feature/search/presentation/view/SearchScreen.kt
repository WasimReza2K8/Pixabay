package com.jet.feature.search.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.ui.theme.WasimTheme
import com.example.core.ui.views.ErrorSnakeBar
import com.example.core.ui.views.SearchBar
import com.jet.feature.search.R
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnErrorSnakeBarDismissed
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnPhotoClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnQueryClearClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnSearch
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnSelectConfirmed
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnSelectDecline
import com.jet.feature.search.presentation.viewmodel.SearchContract.State
import com.jet.feature.search.presentation.viewmodel.SearchViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    val state: State by viewModel.viewState.collectAsStateWithLifecycle()
    SearchScreenImpl(
        state = state,
        sendEvent = { viewModel.onUiEvent(it) },
    )
}

@Composable
private fun SearchScreenImpl(
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
    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackBarHostState),
        topBar = {
            SearchBar(
                hint = stringResource(id = R.string.search),
                value = state.query,
                onValueChange = { query ->
                    sendEvent(OnSearch(query))
                },
                onClick = { sendEvent(OnQueryClearClicked) }
            )
        },
    ) { scaffoldPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    start = WasimTheme.spacing.spacing16,
                    top = scaffoldPadding.calculateTopPadding(),
                    end = WasimTheme.spacing.spacing16,
                    bottom = scaffoldPadding.calculateBottomPadding(),
                )
        ) {
            if (state.photos.isEmpty() && state.infoText.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.infoText)
                }
            } else {
                PhotoList(
                    photos = state.photos,
                    onItemClick = { localId -> sendEvent(OnPhotoClicked(localId)) }
                )
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
            )
        }

        if (state.isDialogShowing) {
            Dialog(sendEvent = sendEvent)
        }
    }
}

@Composable
fun Dialog(sendEvent: (event: Event) -> Unit) {
    SelectionDialog(
        title = stringResource(id = R.string.search_confirmation),
        text = stringResource(id = R.string.search_confirmation_detail),
        yesButtonText = stringResource(id = R.string.search_confirmation_yes),
        noButtonText = stringResource(id = R.string.search_confirmation_no),
        onConfirm = { sendEvent(OnSelectConfirmed) },
        onDecline = { sendEvent(OnSelectDecline) }
    )
}
