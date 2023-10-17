package com.jet.feature.search.data.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jet.feature.search.data.datasource.db.dao.PhotoDao
import com.jet.feature.search.data.datasource.db.dao.PhotoRemoteKeysDao
import com.jet.feature.search.data.datasource.db.entity.PhotoEntity
import com.jet.feature.search.data.datasource.db.entity.PhotoRemoteKeys

@Database(
    entities = [PhotoEntity::class, PhotoRemoteKeys::class],
    version = 1,
    exportSchema = false,
)

abstract class PhotoDB : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun remoteKeysDao(): PhotoRemoteKeysDao
}
