package com.jet.feature.search.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.core.dispatcher.BaseDispatcherProvider
import com.jet.feature.search.data.datasource.api.PixaBayApi
import com.jet.feature.search.data.datasource.db.PhotoDB
import com.jet.feature.search.data.datasource.db.entity.PhotoEntity
import com.jet.feature.search.data.datasource.db.entity.PhotoRemoteKeys
import com.jet.feature.search.data.mapper.toPhotoEntity
import com.jet.feature.search.data.repository.SearchRepositoryImpl
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val FIRST_PAGE = 1

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator @Inject constructor(
    private val query: String,
    private val networkService: PixaBayApi,
    private val photoDB: PhotoDB,
    private val dispatcherProvider: BaseDispatcherProvider
) : RemoteMediator<Int, PhotoEntity>() {
    private val photoDao = photoDB.photoDao()
    private val remoteKeysDao = photoDB.remoteKeysDao()

    //when uncommented this function will prevent the remote mediator from refreshing in every intial launch of the app
    //though this brings a bug such that the on launch the app will do things at once one,fetch data from room and secondly perform a network request so as to update
    //data in room.When one is not connected to the internet,the error view and the loaded data from room will overlay each other
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextPage?.minus(1) ?: FIRST_PAGE
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevPage
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextPage
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            return withContext(dispatcherProvider.io()) {
                val imagesResponse = networkService.searchPhotos(
                    searchString = query,
                    page = page,
                    perPage = SearchRepositoryImpl.NETWORK_PAGE_SIZE
                )
                val images = imagesResponse.images
                val endOfPaginationReached = images.isEmpty()
                photoDB.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        remoteKeysDao.deleteAll()
                        photoDao.deleteAll()
                    }
                    val prevPage = if (page == FIRST_PAGE) null else page - 1
                    val nextPage = if (endOfPaginationReached) null else page + 1
                    val keys = images.map {
                        PhotoRemoteKeys(photoId = it.id, prevPage = prevPage, nextPage = nextPage)
                    }
                    remoteKeysDao.insertAll(keys)
                    photoDao
                        .insertAll(images.map { it.toPhotoEntity(searchTerm = query) })
                }
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PhotoEntity>): PhotoRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { photoEntity ->
                remoteKeysDao.photoRemoteKeys(photoEntity.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PhotoEntity>): PhotoRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { photoEntity ->
                remoteKeysDao.photoRemoteKeys(photoEntity.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, PhotoEntity>
    ): PhotoRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { photoId ->
                remoteKeysDao.photoRemoteKeys(photoId)
            }
        }
    }
}