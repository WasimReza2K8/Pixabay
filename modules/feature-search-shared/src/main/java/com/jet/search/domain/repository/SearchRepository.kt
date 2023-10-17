package com.jet.search.domain.repository

import androidx.paging.PagingData
import com.jet.search.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchPhoto(query: String): Flow<PagingData<Photo>>
    fun getPhotoById(id: String): Flow<Photo?>
}
