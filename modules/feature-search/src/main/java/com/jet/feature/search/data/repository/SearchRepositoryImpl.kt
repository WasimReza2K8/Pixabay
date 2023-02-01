package com.jet.feature.search.data.repository

import com.example.core.ext.isNetworkException
import com.jet.feature.search.data.datasource.api.PixaBayApi
import com.jet.feature.search.data.datasource.db.dao.PhotoDao
import com.jet.feature.search.data.mapper.toDomainPhoto
import com.jet.feature.search.data.mapper.toPhotoEntity
import com.jet.search.domain.model.Photo
import com.jet.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: PixaBayApi,
    private val photoDao: PhotoDao,
) : SearchRepository {

    override fun searchPhoto(query: String): Flow<List<Photo>> =
        flow {
            val photoEntities = api.searchPhotos(query).images.map {
                it.toPhotoEntity(query)
            }
            photoDao.insertAll(photoEntities)
            emit(photoEntities.map { it.toDomainPhoto() })
        }.catch { error ->
            if (error.isNetworkException()) {
                emit(handleNetworkException(query, error))
            } else {
                throw error
            }
        }

    private suspend fun handleNetworkException(
        query: String,
        error: Throwable,
    ): List<Photo> = getLocalPhotos(query).ifEmpty {
        throw error
    }

    override fun getPhotoById(id: String): Flow<Photo?> =
        photoDao.getPhoto(id).map {
            it.toDomainPhoto()
        }

    private suspend fun getLocalPhotos(query: String): List<Photo> =
        photoDao.queryPhotos(query).map { it.toDomainPhoto() }
}
