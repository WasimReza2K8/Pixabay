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

import com.example.core.dispatcher.BaseDispatcherProvider
import com.example.core.ext.isNetworkException
import com.example.core.state.Output
import com.example.core.state.Output.NetworkError
import com.example.core.state.Output.UnknownError
import com.jet.search.domain.model.Photo
import com.jet.search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class SearchUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val dispatcherProvider: BaseDispatcherProvider,
) {
    operator fun invoke(query: String): Flow<Output<List<Photo>>> =
        repository.searchPhoto(processQueryString(query)).map { photos ->
            getOutput(photos)
        }.catch { error ->
            if (error.isNetworkException()) {
                emit(NetworkError)
            } else {
                emit(UnknownError)
            }
        }.flowOn(dispatcherProvider.io())

    private fun getOutput(photos: List<Photo>): Output<List<Photo>> = Output.Success(photos)

    private fun processQueryString(query: String): String = query.trim().replace("\\s+".toRegex(), "+")
}
