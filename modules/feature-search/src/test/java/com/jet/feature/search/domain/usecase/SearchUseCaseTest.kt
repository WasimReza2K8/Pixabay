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

package com.jet.feature.search.domain.usecase

import app.cash.turbine.test
import com.example.core.dispatcher.BaseDispatcherProvider
import com.example.core.state.Output.NetworkError
import com.example.core.state.Output.Success
import com.example.core.state.Output.UnknownError
import com.jet.feature.search.utils.TestDispatcherProvider
import com.jet.feature.search.utils.photo
import com.jet.search.domain.model.Photo
import com.jet.search.domain.repository.SearchRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class SearchUseCaseTest {
    private val repository: SearchRepository = mockk(relaxed = true)
    private val dispatcherProvider: BaseDispatcherProvider = TestDispatcherProvider()
    private val useCase: SearchUseCase = SearchUseCase(
        repository = repository,
        dispatcherProvider = dispatcherProvider
    )

    @Test
    fun `Given valid query with valid response, When invoked, Then returns output with valid list of photos`() =
        runTest {
            // Given
            val given = listOf(photo)
            val expected = Success(given)
            val query = "De"
            mockRepository(given)

            useCase.invoke(query).test {
                assertEquals(expected, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `Given valid query with empty photo response, When invoked, Then returns output with empty list of photos`() =
        runTest {
            // Given
            val expected = Success(emptyList<Photo>())
            val query = "fa"
            mockRepository(emptyList())

            useCase.invoke(query).test {
                assertEquals(expected, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `Given IOException from repository, When invoked, Then returns network error output`() =
        runTest {
            // Given
            val expected = NetworkError
            val query = "fa"
            coEvery {
                repository.searchPhoto(any())
            } returns flow { throw IOException() }

            useCase.invoke(query).test {
                assertEquals(expected, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `Given RuntimeException from repository, When invoked, Then return unknown error output`() =
        runTest {
            // Given
            val expected = UnknownError
            val query = "fa"
            coEvery {
                repository.searchPhoto(any())
            } returns flow { throw RuntimeException() }

            useCase.invoke(query).test {
                assertEquals(expected, awaitItem())
                awaitComplete()
            }
        }

    @Test
    fun `Given query string with multiple whitespaces, When invoked, Then called repository having query with a + sign`() =
        runTest {
            val given = "test        query"
            val expected = "test+query"

            useCase.invoke(given).test {
                verify { repository.searchPhoto(expected) }
                awaitComplete()
            }
        }

    @Test
    fun `Given query string with a inside whitespace, When invoked, Then called repository having query with a + sign`() =
        runTest {
            val given = "test query"
            val expected = "test+query"

            useCase.invoke(given).test {
                verify { repository.searchPhoto(expected) }
                awaitComplete()
            }
        }

    @Test
    fun `Given query string with outside whitespace, When invoked, Then called repository having query with a + sign`() =
        runTest {
            val given = " test "
            val expected = "test"

            useCase.invoke(given).test {
                verify { repository.searchPhoto(expected) }
                awaitComplete()
            }
        }

    private fun mockRepository(photos: List<Photo>) = runTest {
        coEvery {
            repository.searchPhoto(any())
        } returns flow { emit(photos) }
    }

}
