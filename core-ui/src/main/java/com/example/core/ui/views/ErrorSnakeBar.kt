package com.example.core.ui.views

import androidx.compose.material.SnackbarDuration.Long
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.core.viewmodel.ErrorEvent
import com.example.core.viewmodel.ErrorEvent.NetworkError
import com.example.core.viewmodel.ErrorEvent.UnknownError

@Composable
fun ErrorSnakeBar(
    errorEvent: ErrorEvent?,
    snackBarHostState: SnackbarHostState,
) {
    LaunchedEffect(errorEvent) {
        errorEvent?.let {
            when (errorEvent) {
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
        }
    }
}
