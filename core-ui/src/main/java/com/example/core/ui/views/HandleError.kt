package com.example.core.ui.views

import androidx.compose.material.SnackbarDuration.Long
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult.Dismissed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.core.ui.viewmodel.ErrorEvent
import com.example.core.ui.viewmodel.ErrorEvent.NetworkError
import com.example.core.ui.viewmodel.ErrorEvent.UnknownError
import com.example.core.ui.viewmodel.ViewEvent

@Composable
fun <UiEvent : ViewEvent> HandleError(
    errorEvent: ErrorEvent?,
    snackBarHostState: SnackbarHostState,
    sendEvent: (event: UiEvent) -> Unit,
    snakeBarDismissedEvent: UiEvent,
) {
    LaunchedEffect(errorEvent) {
        errorEvent?.let {
            val result = when (errorEvent) {
                is NetworkError -> {
                    snackBarHostState.showSnackbar(
                        message = errorEvent.message,
                        duration = Long,
                    )
                }
                is UnknownError -> {
                    snackBarHostState.showSnackbar(
                        message = errorEvent.message,
                        duration = Long,
                    )
                }
            }

            when (result) {
                Dismissed -> sendEvent(snakeBarDismissedEvent)
                else -> {
                    //do nothing
                }
            }
        }
    }
}