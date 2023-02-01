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

package com.jet.feature.search.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.core.navigation.Navigator
import com.example.core.resProvider.ResourceProvider
import com.example.core.state.Output.NetworkError
import com.example.core.state.Output.Success
import com.example.core.state.Output.UnknownError
import com.example.core.ui.R.string
import com.example.core.ui.viewmodel.BaseViewModel
import com.example.core.ui.viewmodel.ErrorEvent
import com.jet.detail.presentation.DetailLauncher
import com.jet.feature.search.BuildConfig.DEBOUNCE_TIME
import com.jet.feature.search.domain.usecase.SearchUseCase
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnErrorSnakeBarDismissed
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnInitViewModel
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnPhotoClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnQueryClearClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnSearch
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnSelectConfirmed
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnSelectDecline
import com.jet.feature.search.presentation.viewmodel.SearchContract.FRUITS
import com.jet.feature.search.presentation.viewmodel.SearchContract.State
import com.jet.search.domain.model.Photo
import com.jet.search.presentation.mapper.toPhotoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val resourceProvider: ResourceProvider,
    private val navigator: Navigator,
    private val detailLauncher: DetailLauncher,
) : BaseViewModel<Event, State>() {

    override fun provideInitialState() = State()

    private val searchQuery: Channel<String> = Channel()
    private var selectedId: String? = null

    init {
        onUiEvent(OnInitViewModel)
    }

    override fun handleEvent(event: Event) {
        when (event) {
            is OnInitViewModel -> {
                startQuery()
                viewModelScope.launch {
                    onUiEvent(OnSearch(FRUITS))
                }
            }
            is OnSearch -> {
                updateState { copy(query = event.query) }
                searchQuery.trySend(event.query)
            }
            is OnQueryClearClicked -> updateState { copy(query = "", photos = emptyList()) }
            is OnPhotoClicked -> {
                selectedId = event.selectedId
                updateState { copy(isDialogShowing = true) }
            }
            is OnSelectConfirmed -> {
                updateState { copy(isDialogShowing = false) }
                selectedId?.let {
                    navigator.navigate(detailLauncher.route(it))
                }
                selectedId = null
            }
            is OnSelectDecline -> {
                updateState { copy(isDialogShowing = false) }
            }
            is OnErrorSnakeBarDismissed -> {
                updateState { copy(errorEvent = null) }
            }
        }
    }

    private fun startQuery() {
        viewModelScope.launch {
            searchQuery
                .receiveAsFlow()
                .debounce(DEBOUNCE_TIME)
                .distinctUntilChanged()
                .filter { query ->
                    if (query.isEmpty()) {
                        updateState {
                            copy(
                                infoText = resourceProvider.getString(string.search_not_started),
                                photos = emptyList(),
                            )
                        }
                        return@filter false
                    } else {
                        return@filter true
                    }
                }
                .flatMapLatest { query ->
                    updateState { copy(isLoading = true) }
                    searchUseCase(query)
                }.collect { output ->
                    updateState { copy(isLoading = false) }
                    when (output) {
                        is Success -> {
                            handleSuccess(output)
                        }
                        NetworkError -> {
                            updateState {
                                copy(
                                    errorEvent = ErrorEvent.NetworkError(
                                        resourceProvider.getString(string.network_error)
                                    )
                                )
                            }
                        }
                        UnknownError -> {
                            updateState {
                                copy(
                                    errorEvent = ErrorEvent.UnknownError(
                                        resourceProvider.getString(string.unknown_error)
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun handleSuccess(output: Success<List<Photo>>) {
        if (output.result.isEmpty()) {
            updateState {
                copy(
                    infoText = resourceProvider.getString(string.no_photo),
                    photos = emptyList()
                )
            }
        } else {
            updateState {
                copy(
                    infoText = "",
                    photos = output.result.map {
                        it.toPhotoUiModel()
                    }
                )
            }
        }
    }
}
