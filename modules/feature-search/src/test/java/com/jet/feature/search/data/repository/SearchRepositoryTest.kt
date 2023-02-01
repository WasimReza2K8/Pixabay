package com.jet.feature.search.data.repository

import app.cash.turbine.test
import com.jet.feature.search.data.datasource.api.PixaBayApi
import com.jet.feature.search.data.datasource.db.dao.PhotoDao
import com.jet.feature.search.data.dto.ResponseDto
import com.jet.feature.search.utils.photo
import com.jet.feature.search.utils.photoDto
import com.jet.feature.search.utils.photoEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.IOException

class SearchRepositoryTest {
    private val api: PixaBayApi = mockk()
    private val dao: PhotoDao = mockk()
    private val repository: SearchRepositoryImpl = SearchRepositoryImpl(
        api = api,
        photoDao = dao,
    )

    @Test
    fun `Given success api response, When searchPhoto called, Then valid photos are emitted`() =
        runTest {
            val given = ResponseDto(listOf(photoDto))
            val expected = listOf(photo)
            coEvery {
                api.searchPhotos(any())
            } returns given

            coEvery {
                dao.insertAll(any())
            } returns listOf(1)

            repository.searchPhoto("test").test {
                val result = awaitItem()
                assertThat(expected == result).isTrue
                awaitComplete()
            }
        }

    @Test
    fun `Given IoException api response, When searchPhoto called, Then valid photos are emitted from db`() =
        runTest {
            val given = IOException()
            val expected = listOf(photo)
            coEvery {
                api.searchPhotos(any())
            } throws given

            coEvery {
                dao.insertAll(any())
            } returns listOf(1)

            coEvery {
                dao.queryPhotos(any())
            } returns listOf(photoEntity)

            repository.searchPhoto("test").test {
                val result = awaitItem()
                assertThat(expected == result).isTrue
                awaitComplete()
            }
        }

    @Test
    fun `Given IoException api response and db has no match, When searchPhoto called, Then throws exception`() =
        runTest {
            val given = IOException()
            coEvery {
                api.searchPhotos(any())
            } throws given

            coEvery {
                dao.queryPhotos(any())
            } returns emptyList()

            repository.searchPhoto("test").test {
                val result = awaitError()
                assertThat(given == result).isTrue
            }
        }

    @Test
    fun `Given RuntimeException api response, When searchPhoto called, Then throws exception`() =
        runTest {
            val given = RuntimeException()
            coEvery {
                api.searchPhotos(any())
            } throws given

            repository.searchPhoto("test").test {
                val result = awaitError()
                assertThat(given == result).isTrue
            }
        }

    @Test
    fun `Given db contains query result, When getPhotoById called, Then returns valid photo`() =
        runTest {
            val given = photoEntity
            val expected = photo
            coEvery {
                dao.getPhoto(any())
            } returns flow { emit(given) }

            repository.getPhotoById("test").test {
                assertThat(expected == awaitItem()).isTrue
                awaitComplete()
            }
        }

    @Test
    fun `Given db provide exception, When getPhotoById called, Then throws the exception`() =
        runTest {
            val given = RuntimeException()
            coEvery {
                dao.getPhoto(any())
            } returns flow { throw given }

            repository.getPhotoById("test").test {
                assertThat(given == awaitError()).isTrue
            }
        }
}
