/*
 * Copyright 2021 Wasim Reza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jet.feature.search.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.jet.feature.search.data.datasource.api.PixaBayApi
import com.jet.feature.search.data.datasource.db.PhotoDB
import com.jet.feature.search.data.repository.SearchRepositoryImpl
import com.jet.feature.search.presentation.launcher.SearchLauncherImpl
import com.jet.search.domain.repository.SearchRepository
import com.jet.search.presentation.SearchLauncher
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SearchPresentationModule {
    @Singleton
    @Binds
    fun bindLauncher(launcher: SearchLauncherImpl): SearchLauncher
}

@Module
@InstallIn(SingletonComponent::class)
interface SearchDomainModule {
    @Singleton
    @Binds
    fun bindSearchRepository(searchRepository: SearchRepositoryImpl): SearchRepository
}

@Module
@InstallIn(SingletonComponent::class)
object SearchDataModule {
    private const val DATABASE_NAME = "Photo_db"

    @Provides
    @Singleton
    fun providesApi(retrofit: Retrofit): PixaBayApi = retrofit.create(PixaBayApi::class.java)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): PhotoDB =
        databaseBuilder(
            context.applicationContext,
            PhotoDB::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
}
