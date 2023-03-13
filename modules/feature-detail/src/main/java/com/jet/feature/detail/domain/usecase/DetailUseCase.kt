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

import com.example.core.dispatcher.BaseDispatcherProvider
import com.example.core.exception.Exceptions.PhotoDetailNotFoundException
import com.example.core.state.Output
import com.jet.search.domain.model.Photo
import com.jet.search.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class DetailUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val dispatcherProvider: BaseDispatcherProvider,
) {
    operator fun invoke(id: String): Flow<Output<Photo>> =
        repository.getPhotoById(id).map {
            getOutput(it)
        }.catch {
            if (it is PhotoDetailNotFoundException) {
                Timber.e(it)
            }
            emit(Output.UnknownError)
        }.flowOn(dispatcherProvider.io())

    private fun getOutput(photo: Photo?): Output<Photo> = photo?.let {
        Output.Success(it)
    } ?: throw PhotoDetailNotFoundException
}
