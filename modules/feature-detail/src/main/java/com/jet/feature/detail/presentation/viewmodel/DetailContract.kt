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

import com.example.core.state.Event
import com.example.core.viewmodel.ErrorEvent
import com.example.core.viewmodel.ViewEvent
import com.example.core.viewmodel.ViewState
import com.jet.search.presentation.model.PhotoUiModel

object DetailContract {
    data class State(
        val photo: PhotoUiModel? = null,
        val errorUiEvent: Event<ErrorEvent>? = null,
    ) : ViewState

    sealed interface UiEvent : ViewEvent {
        data class OnViewModelInit(val id: String) : UiEvent
        object OnBackButtonClicked : UiEvent
    }
}
