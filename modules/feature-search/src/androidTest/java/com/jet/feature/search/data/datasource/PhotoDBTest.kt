package com.jet.feature.search.data.datasource

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jet.feature.search.data.datasource.db.PhotoDB
import com.jet.feature.search.data.datasource.db.dao.PhotoDao
import com.jet.feature.search.data.dto.PhotoDto
import com.jet.feature.search.data.dto.ResponseDto
import com.jet.feature.search.data.mapper.toPhotoEntity
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStream

@RunWith(AndroidJUnit4::class)
class PhotoDBTest {
    private lateinit var db: PhotoDB
    private lateinit var context: Context
    private lateinit var photoDao: PhotoDao
    private lateinit var photos: List<PhotoDto>

    private val json = Json { ignoreUnknownKeys = true }

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, PhotoDB::class.java).build()
        val jsonStream: InputStream = context.resources.assets.open("response.json")
        val jsonBytes: ByteArray = jsonStream.readBytes()

        photos = json.decodeFromString<ResponseDto>(String(jsonBytes)).images

        photoDao = db.photoDao()


        runTest {
            photoDao.insertAll(photos.map { it.toPhotoEntity("fruits") })
        }
    }

    @After
    fun clear() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun test_queryPhotos_with_matched_query() = runTest {
        assertThat(photos.size == photoDao.queryPhotos("fruits").size).isTrue
    }

    @Test
    fun test_queryPhotos_with_unmatched_query() = runTest {
        assertThat(photoDao.queryPhotos("av")).isEmpty()
    }

    @Test
    fun test_getPhotoById_with_matched_id(): Unit = runTest {
        photoDao.getPhoto("fruits_2310212").take(1).collect {
            assertThat(it).isNotNull
        }
    }

    @Test
    fun test_getPhotoById_with_unmatched_id(): Unit = runTest {
        photoDao.getPhoto("av_2310212").take(1).collect {
            assertThat(it).isNull()
        }
    }
}