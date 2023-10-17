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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.core.ui.theme.WasimTheme
import com.example.core.ui.views.ErrorSnakeBar
import com.example.core.ui.views.SearchBar
import com.jet.feature.search.presentation.viewmodel.SearchContract.State
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnActivityStarted
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnPhotoClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnQueryClearClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnSearch
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnSelectConfirmed
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnSelectDecline
import com.jet.feature.search.presentation.viewmodel.SearchViewModel
import com.wasim.feature.search.R

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    val state: State by viewModel.viewState.collectAsStateWithLifecycle()
    OnStart(sendEvent = { viewModel.onUiEvent(it) })
    SearchScreenImpl(
        state = state,
        sendEvent = { viewModel.onUiEvent(it) },
    )
}

@Composable
private fun OnStart(sendEvent: (uiEvent: UiEvent) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for sending analytics events
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                sendEvent(OnActivityStarted)
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun SearchScreenImpl(
    state: State,
    sendEvent: (uiEvent: UiEvent) -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val photos = state.photos.collectAsLazyPagingItems()
    ErrorSnakeBar(
        errorEvent = state.errorUiEvent?.getContentIfNotHandled(),
        snackBarHostState = snackBarHostState,
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
            if (photos.itemCount == 0 && state.infoText.isNotEmpty()) {
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
                    photos = photos,
                    onItemClick = { localId -> sendEvent(OnPhotoClicked(localId)) }
                )
            }
        }

        if (state.isLoading) {
            LoadingScreen()
        }

        if (state.isDialogShowing) {
            Dialog(sendEvent = sendEvent)
        }
    }
}

@Preview
@Composable
fun Preview(){
    SearchScreenImpl(
        state = State(),
        sendEvent = {}
    )
}

@Composable
fun Dialog(sendEvent: (uiEvent: UiEvent) -> Unit) {
    SelectionDialog(
        title = stringResource(id = R.string.search_confirmation),
        text = stringResource(id = R.string.search_confirmation_detail),
        yesButtonText = stringResource(id = R.string.search_confirmation_yes),
        noButtonText = stringResource(id = R.string.search_confirmation_no),
        onConfirm = { sendEvent(OnSelectConfirmed) },
        onDecline = { sendEvent(OnSelectDecline) }
    )
}
