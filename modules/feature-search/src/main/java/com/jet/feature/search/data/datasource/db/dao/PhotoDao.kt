package com.jet.feature.search.data.datasource.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jet.feature.search.data.datasource.db.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<PhotoEntity>): List<Long>

    @Query("SELECT * FROM photo_table WHERE search_term like :query")
    fun queryPhotos(query: String): PagingSource<Int, PhotoEntity>

    @Query("SELECT * FROM photo_table WHERE local_id = :id")
    fun getPhoto(id: String): Flow<PhotoEntity?>

    @Query("DELETE FROM photo_table")
    suspend fun deleteAll()
}
