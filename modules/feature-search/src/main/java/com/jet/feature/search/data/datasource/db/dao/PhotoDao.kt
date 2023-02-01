package com.jet.feature.search.data.datasource.db.dao

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
    suspend fun queryPhotos(query: String): List<PhotoEntity>

    @Query("SELECT * FROM photo_table WHERE local_id = :id")
    fun getPhoto(id: String): Flow<PhotoEntity>
}
