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

package com.jet.feature.detail.domain.usecase

import app.cash.turbine.test
import com.example.core.dispatcher.BaseDispatcherProvider
import com.example.core.state.Output.Success
import com.example.core.state.Output.UnknownError
import com.jet.feature.detail.utils.TestDispatcherProvider
import com.jet.feature.detail.utils.photo
import com.jet.search.domain.model.Photo
import com.jet.search.domain.repository.SearchRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DetailUseCaseTest {
    private val repository: SearchRepository = mockk(relaxed = true)
    private val dispatcherProvider: BaseDispatcherProvider = TestDispatcherProvider()
    private val useCase: DetailUseCase = DetailUseCase(
        repository = repository,
        dispatcherProvider = dispatcherProvider
    )

    @Test
    fun `Given valid localId with valid response, When invoked, Then return success output having photo`() =
        runTest {
            // Given
            val given = photo
            val expected = Success(given)
            val id = "localId"
            mockRepository(given)

            useCase.invoke(id).test {
                assertThat(expected == awaitItem()).isTrue
                awaitComplete()
            }
        }

    @Test
    fun `Given valid localId with null response, When invoked, Then returns output containing null`() =
        runTest {
            // Given
            val expected = UnknownError
            val id = "localId"
            mockRepository(null)

            useCase.invoke(id).test {
                assertThat(expected == awaitItem()).isTrue
                awaitComplete()
            }
        }

    @Test
    fun `Given RuntimeException from repository, When invoked, Then returns unknown error output`() =
        runTest {
            // Given
            val expected = UnknownError
            val id = "localId"
            coEvery {
                repository.getPhotoById(any())
            } returns flow { throw RuntimeException() }

            useCase.invoke(id).test {
                assertThat(expected == awaitItem()).isTrue
                awaitComplete()
            }
        }

    private fun mockRepository(photo: Photo?) = runTest {
        coEvery {
            repository.getPhotoById(any())
        } returns flow { emit(photo) }
    }
}
