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
import com.example.core.R
import com.example.core.navigation.Navigator
import com.example.core.resProvider.ResourceProvider
import com.example.core.state.Output
import com.example.core.ui.viewmodel.ErrorEvent.UnknownError
import com.jet.feature.detail.domain.usecase.DetailUseCase
import com.jet.feature.detail.presentation.launcher.DetailLauncherImpl
import com.jet.feature.detail.presentation.viewmodel.DetailContract.Event.OnBackButtonClicked
import com.jet.feature.detail.presentation.viewmodel.DetailContract.Event.OnErrorSnakeBarDismissed
import com.jet.feature.detail.utils.photo
import com.jet.feature.detail.utils.photoUi
import com.jet.search.domain.model.Photo
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class DetailViewModelTest {
    private val useCase: DetailUseCase = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
    private lateinit var viewModel: DetailViewModel
    private val navigator: Navigator = mockk()
    private val unknownError = "unknown error"
    private val resourceProvider: ResourceProvider = mockk {
        every {
            getString(R.string.unknown_error_detail)
        } returns unknownError
    }

    private lateinit var testDispatcher: TestDispatcher

    private fun createViewModel() {
        viewModel = DetailViewModel(
            useCase = useCase,
            savedStateHandle = savedStateHandle,
            navigator = navigator,
            resourceProvider = resourceProvider
        )
    }

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `Given valid photo, When DetailUseCase invoked, Then view state having photo`() =
        runTest {
            val given = Output.Success(photo)
            mockUseCase(given)
            mockSaveStateHandle("localId")

            createViewModel()

            viewModel.viewState.take(1).collectLatest {
                assertThat(it.photo == photoUi).isTrue
            }
        }

    @Test
    fun `Given unknown error from DetailUseCase, When DetailUseCase invoked, Then view state contains error message`() =
        runTest {
            val given = Output.UnknownError
            mockUseCase(given)
            mockSaveStateHandle("localId")

            createViewModel()

            viewModel.viewState.take(1).collectLatest {
                assertThat((it.errorEvent as UnknownError).message == unknownError).isTrue
            }
        }

    @Test
    fun `When OnBackButtonClicked, Then navigateUp called`() {
        mockSaveStateHandle("localId")

        createViewModel()
        viewModel.onUiEvent(OnBackButtonClicked)

        verify(exactly = 1) { navigator.navigateUp() }
    }

    @Test
    fun `When OnErrorSnakeBarDismissed is happened, Then in viewState errorEvent become null`() =
        runTest {
            mockSaveStateHandle("localId")
            createViewModel()
            viewModel.onUiEvent(OnErrorSnakeBarDismissed)
            viewModel.viewState.take(1).collectLatest {
                assertThat(it.errorEvent).isNull()
            }

        }

    @Test
    fun `Given id is empty, When init viewModel, Then DetailUseCase never called`() {
        mockSaveStateHandle("")
        createViewModel()
        verify(exactly = 0) { useCase.invoke("") }
    }

    private fun mockSaveStateHandle(localId: String) {
        every {
            savedStateHandle.get<String>(DetailLauncherImpl.LOCAL_ID)
        } returns localId
    }

    private fun mockUseCase(given: Output<Photo>) = runTest {
        coEvery {
            useCase.invoke(any())
        } returns flow { emit(given) }
    }

    @org.junit.After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
