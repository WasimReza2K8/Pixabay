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

package com.jet.feature.detail.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.core.R
import com.example.core.navigation.Navigator
import com.example.core.resProvider.ResourceProvider
import com.example.core.state.Output.Success
import com.example.core.state.Output.UnknownError
import com.example.core.viewmodel.BaseViewModel
import com.example.core.viewmodel.ErrorEvent
import com.jet.feature.detail.domain.usecase.DetailUseCase
import com.jet.feature.detail.presentation.launcher.DetailLauncherImpl.Companion.LOCAL_ID
import com.jet.feature.detail.presentation.viewmodel.DetailContract.Event
import com.jet.feature.detail.presentation.viewmodel.DetailContract.Event.OnBackButtonClicked
import com.jet.feature.detail.presentation.viewmodel.DetailContract.Event.OnErrorSnakeBarDismissed
import com.jet.feature.detail.presentation.viewmodel.DetailContract.Event.OnViewModelInit
import com.jet.feature.detail.presentation.viewmodel.DetailContract.State
import com.jet.search.presentation.mapper.toPhotoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val useCase: DetailUseCase,
    private val navigator: Navigator,
    private val resourceProvider: ResourceProvider,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<Event, State>() {

    override fun provideInitialState() = State()
    private val id: String by lazy {
        savedStateHandle.get<String>(LOCAL_ID).orEmpty()
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is OnViewModelInit -> getSelectedPhoto(event.id)
            is OnBackButtonClicked -> navigator.navigateUp()
            is OnErrorSnakeBarDismissed -> updateState { copy(errorEvent = null) }
        }
    }

    private fun getSelectedPhoto(id: String) {
        if (id.isEmpty()) {
            return
        }
        viewModelScope.launch {
            useCase(id).collect { output ->
                when (output) {
                    is Success -> updateState {
                        copy(
                            photo = output.result?.toPhotoUiModel(),
                        )
                    }
                    is UnknownError -> updateState {
                        copy(
                            errorEvent = ErrorEvent.UnknownError(
                                resourceProvider.getString(R.string.unknown_error_detail)
                            )
                        )
                    }
                    else -> {
                        // no other error possible here
                    }
                }
            }
        }
    }

    init {
        onUiEvent(OnViewModelInit(id))
    }
}
