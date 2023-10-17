package com.jet.feature.search.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.core.dispatcher.BaseDispatcherProvider
import com.jet.feature.search.data.remotemediator.PhotoRemoteMediator
import com.jet.feature.search.data.datasource.api.PixaBayApi
import com.jet.feature.search.data.datasource.db.PhotoDB
import com.jet.feature.search.data.mapper.toDomainPhoto
import com.jet.search.domain.model.Photo
import com.jet.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: PixaBayApi,
    private val photoDB: PhotoDB,
    private val dispatcherProvider: BaseDispatcherProvider
) : SearchRepository {

    private val photoDao = photoDB.photoDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun searchPhoto(query: String): Flow<PagingData<Photo>> {
        val pagingSourceFactory = { photoDao.queryPhotos(query) }

        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = NETWORK_PAGE_SIZE + (NETWORK_PAGE_SIZE * 2),
                enablePlaceholders = false
            ),
            remoteMediator = PhotoRemoteMediator(
                query = query,
                api,
                photoDB,
                dispatcherProvider
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { photoEntity ->
                photoEntity.toDomainPhoto()
            }
        }
    }

    override fun getPhotoById(id: String): Flow<Photo?> =
        photoDao.getPhoto(id).map {
            it?.toDomainPhoto()
        }

    companion object {
         const val NETWORK_PAGE_SIZE = 20
    }
}
