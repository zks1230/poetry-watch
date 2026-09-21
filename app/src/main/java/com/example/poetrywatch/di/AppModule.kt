package com.example.poetrywatch.di

import android.content.Context
import androidx.room.Room
import com.example.poetrywatch.data.db.PoetryDatabase
import com.example.poetrywatch.data.db.PoetryDatabaseFactory
import com.example.poetrywatch.data.db.dao.PoemDao
import com.example.poetrywatch.data.db.dao.ProgressDao
import com.example.poetrywatch.data.preferences.PreferencesRepository
import com.example.poetrywatch.data.preferences.PreferencesRepositoryImpl
import com.example.poetrywatch.domain.recommend.RecommendationEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(factory: PoetryDatabaseFactory): PoetryDatabase = factory.db

    @Provides
    fun providePoemDao(db: PoetryDatabase): PoemDao = db.poemDao()

    @Provides
    fun provideProgressDao(db: PoetryDatabase): ProgressDao = db.progressDao()

    @Provides
    @Singleton
    fun providePreferencesRepository(
        @ApplicationContext context: Context
    ): PreferencesRepository = PreferencesRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideRecommendationEngine(): RecommendationEngine = RecommendationEngine()
}
