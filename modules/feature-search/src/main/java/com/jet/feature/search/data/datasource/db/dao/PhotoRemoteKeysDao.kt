package com.jet.feature.search.data.datasource.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.jet.feature.search.data.datasource.db.entity.PhotoRemoteKeys


@Dao
interface PhotoRemoteKeysDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(remoteKeys: List<PhotoRemoteKeys>)

    @Query("SELECT * FROM photo_remote_keys WHERE photo_id = :photoId")
    suspend fun photoRemoteKeys(photoId: Int): PhotoRemoteKeys?

    @Query("DELETE FROM photo_remote_keys")
    suspend fun deleteAll()
}