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

import androidx.paging.map
import com.example.core.navigation.Navigator
import com.example.core.viewmodel.BaseViewModel
import com.jet.detail.presentation.DetailLauncher
import com.jet.feature.search.domain.usecase.SearchUseCase
import com.jet.feature.search.presentation.viewmodel.SearchContract.BERLIN
import com.jet.feature.search.presentation.viewmodel.SearchContract.State
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnActivityStarted
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnInitViewModel
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnPhotoClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnQueryClearClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnSearch
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnSelectConfirmed
import com.jet.feature.search.presentation.viewmodel.SearchContract.UiEvent.OnSelectDecline
import com.jet.search.presentation.mapper.toPhotoUiModel
import com.wasim.feature.search.BuildConfig.DEBOUNCE_TIME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val navigator: Navigator,
    private val detailLauncher: DetailLauncher,
) : BaseViewModel<UiEvent, State>() {

    override fun provideInitialState() = State()

    private val searchQuery = MutableSharedFlow<String>(replay = 1)
    private var selectedId: String? = null


    init {
        onUiEvent(OnInitViewModel)
    }

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is OnInitViewModel -> {
                startQuery()
            }

            is OnActivityStarted -> {
                onUiEvent(OnSearch(BERLIN))
            }

            is OnSearch -> {
                updateState { copy(query = event.query) }
                searchQuery.tryEmit(event.query)
            }

            is OnQueryClearClicked -> updateState { copy(query = "") }
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
        }
    }

    private fun startQuery() {
        updateState {
            copy(
                photos = searchQuery
                    .debounce(DEBOUNCE_TIME)
                    .distinctUntilChanged()
                    .filter { query ->
                        return@filter query.isNotEmpty()
                    }
                    .flatMapLatest { query ->
                        searchUseCase(query)
                            .map { paginatedPhoto ->
                                paginatedPhoto.map { it.toPhotoUiModel() }
                            }
                    }
            )
        }
    }
}
