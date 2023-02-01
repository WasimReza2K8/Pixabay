package com.jet.feature.search.presentation.viewmodel
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
import com.example.core.R
import com.example.core.navigation.Navigator
import com.example.core.resProvider.ResourceProvider
import com.example.core.state.Output
import com.example.core.state.Output.NetworkError
import com.example.core.state.Output.UnknownError
import com.example.core.ui.R.string
import com.example.core.ui.viewmodel.ErrorEvent
import com.jet.detail.presentation.DetailLauncher
import com.jet.feature.search.domain.usecase.SearchUseCase
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnErrorSnakeBarDismissed
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnPhotoClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnQueryClearClicked
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnSearch
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnSelectConfirmed
import com.jet.feature.search.presentation.viewmodel.SearchContract.Event.OnSelectDecline
import com.jet.feature.search.utils.photo
import com.jet.feature.search.utils.photoUi
import com.jet.search.domain.model.Photo
import com.jet.search.presentation.model.PhotoUiModel
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
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Before
import org.junit.Test

class SearchViewModelTest {
    private val searchUseCase: SearchUseCase = mockk()
    private val networkError = "network error"
    private val unknownError = "unknown error"
    private val searNotStarted = "search not started"
    private val resourceProvider: ResourceProvider = mockk {
        every {
            getString(R.string.network_error)
        } returns networkError
        every {
            getString(R.string.unknown_error)
        } returns unknownError
        every {
            getString(string.search_not_started)
        } returns searNotStarted
        every {
            getString(string.no_photo)
        } returns "mock"
    }
    private val navigator: Navigator = mockk()
    private val detailLauncher: DetailLauncher = mockk()
    private lateinit var viewModel: SearchViewModel

    private fun createViewModel() {
        viewModel = SearchViewModel(
            searchUseCase,
            resourceProvider = resourceProvider,
            navigator = navigator,
            detailLauncher = detailLauncher,
        )
    }

    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        createViewModel()
    }

    @Test
    fun `Given valid photos as output, When viewModel init, Then viewState contains valid photos`() =
        runTest {
            val expected = listOf(photoUi)
            val given = Output.Success(listOf(photo))
            mockUseCase(given)

            createViewModel()
            advanceTimeBy(1000)

            assertThat(expected == viewModel.viewState.value.photos).isTrue
        }

    @Test
    fun `Given valid query but empty output, When onSearch starts, Then viewState contains no photos`() =
        runTest {
            val expected = emptyList<PhotoUiModel>()
            val given = Output.Success(emptyList<Photo>())
            mockUseCase(given)

            viewModel.onUiEvent(OnSearch("de"))
            advanceTimeBy(1000)
            assertThat(expected == viewModel.viewState.value.photos).isTrue
        }

    @Test
    fun `Given valid query but empty output, When onSearch starts, Then viewState contains infoText not empty`() =
        runTest {
            val given = Output.Success(emptyList<Photo>())
            mockUseCase(given)

            viewModel.onUiEvent(OnSearch("de"))
            advanceTimeBy(1000)
            assertThat(viewModel.viewState.value.infoText).isNotEmpty()
        }

    @Test
    fun `Given valid query with success output having photos, When onSearch starts, Then viewState contains list of photos`() =
        runTest {
            val expected = listOf(photoUi)
            val given = Output.Success(listOf(photo))
            mockUseCase(given)

            viewModel.onUiEvent(OnSearch("de"))
            advanceTimeBy(1000)

            assertThat(expected == viewModel.viewState.value.photos).isTrue
        }

    @Test
    fun `Given valid query with success output having photos, When onSearch starts, Then viewState contains empty infoText`() =
        runTest {
            val given = Output.Success(listOf(photo))
            mockUseCase(given)

            viewModel.onUiEvent(OnSearch("de"))
            advanceTimeBy(1000)

            assertThat(viewModel.viewState.value.infoText).isEmpty()
        }

    @Test
    fun `Given changing query with success output having photos, When onSearch use case called, Then returns single result`() =
        runTest {
            val given = Output.Success(listOf(photo))
            coEvery {
                searchUseCase("dev")
            } returns flow { emit(given) }

            viewModel.onUiEvent(OnSearch("d"))
            viewModel.onUiEvent(OnSearch("de"))
            viewModel.onUiEvent(OnSearch("dev"))
            advanceTimeBy(1000)
            verify(exactly = 1) { searchUseCase(any()) }
        }

    @Test
    fun `Given empty query, When onSearch starts, Then use case is not called`() =
        runTest {
            initGettingEmptyQuery()
            verify(exactly = 0) { searchUseCase("") }
        }

    @Test
    fun `Given empty query, When onSearch starts, Then viewState photos is empty`() =
        runTest {
            initGettingEmptyQuery()
            verify(exactly = 0) { searchUseCase("") }
            assertThat(viewModel.viewState.value.photos.isEmpty()).isTrue
        }

    @Test
    fun `Given empty query, When onSearch starts, Then viewState infoText having information`() =
        runTest {
            initGettingEmptyQuery()
            verify(exactly = 0) { searchUseCase("") }
            assertThat(viewModel.viewState.value.infoText == searNotStarted).isTrue
        }

    @Test
    fun `Given empty query, When onSearch starts, Then view state emit with empty list`() =
        runTest {
            initGettingEmptyQuery()
            assertThat(viewModel.viewState.value.photos.isEmpty()).isTrue
        }

    @Test
    fun `When onQueryClearClicked, Then view state emit with empty photo list`() =
        runTest {
            viewModel.onUiEvent(OnQueryClearClicked)
            assertThat(viewModel.viewState.value.photos.isEmpty()).isTrue
        }

    @Test
    fun `When onQueryClearClicked, Then view state emit empty query`() =
        runTest {
            viewModel.onUiEvent(OnQueryClearClicked)
            assertThat(viewModel.viewState.value.query.isEmpty()).isTrue
        }

    @Test
    fun `Given network error in UseCase, When onSearch starts, Then viewState contains network error event`() =
        runTest {
            val given = NetworkError
            mockUseCase(given)

            viewModel.onUiEvent(OnSearch("de"))
            advanceTimeBy(1000)
            viewModel.viewState.take(1).collectLatest {
                assertThat(networkError == (it.errorEvent as ErrorEvent.NetworkError).message).isTrue
            }
        }

    @Test
    fun `Given unknown error in UseCase, When onSearch starts, Then viewState contains unknown error event`() =
        runTest {
            val given = UnknownError
            mockUseCase(given)

            viewModel.onUiEvent(OnSearch("de"))
            advanceTimeBy(1000)

            viewModel.viewState.take(1).collectLatest {
                assertThat(unknownError == (it.errorEvent as ErrorEvent.UnknownError).message).isTrue
            }
        }

    @Test
    fun `When photo clicked, Then viewState isDialogShowing become true`() =
        runTest {
            viewModel.onUiEvent(OnPhotoClicked("photoId"))
            viewModel.viewState.take(1).collectLatest {
                assertThat(it.isDialogShowing).isTrue
            }
        }

    @Test
    fun `When photo selection confirmed, Then viewState contains isDialogShowing become false`() =
        runTest {
            viewModel.onUiEvent(OnPhotoClicked("photoId"))
            viewModel.onUiEvent(OnSelectConfirmed)
            viewModel.viewState.take(1).collectLatest {
                assertThat(it.isDialogShowing).isFalse
            }
        }

    @Test
    fun `When photo selection is confirmed, Then navigate to detail`() {
        every {
            detailLauncher.route(any())
        } returns "detail/photoId"

        viewModel.onUiEvent(OnPhotoClicked("photoId"))
        viewModel.onUiEvent(OnSelectConfirmed)

        verify(exactly = 1) { navigator.navigate("detail/photoId") }
    }

    @Test
    fun `When onSelectionDecline is clicked, Then viewState contains isDialogShowing become false`() =
        runTest {
            viewModel.onUiEvent(OnSelectDecline)
            viewModel.viewState.take(1).collectLatest {
                assertThat(it.isDialogShowing).isFalse
            }
        }

    @Test
    fun `When OnErrorSnakeBarDismissed is happened, Then in viewState errorEvent become null`() =
        runTest {
            viewModel.onUiEvent(OnErrorSnakeBarDismissed)
            viewModel.viewState.take(1).collectLatest {
                assertThat(it.errorEvent == null).isTrue
            }
        }

    private fun mockUseCase(given: Output<List<Photo>>) =
        runTest {
            coEvery {
                searchUseCase(any())
            } returns flow { emit(given) }
        }

    private fun initGettingEmptyQuery() =
        runTest {
            val given = Output.Success(listOf(photo))
            coEvery {
                searchUseCase("")
            } returns flow { emit(given) }

            viewModel.onUiEvent(OnSearch(""))
            advanceTimeBy(1000)
        }

    @org.junit.After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
