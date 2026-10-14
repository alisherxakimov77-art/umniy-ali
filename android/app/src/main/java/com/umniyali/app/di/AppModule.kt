package com.umniyali.app.di

import android.content.Context
import com.umniyali.app.data.local.AppDatabase
import com.umniyali.app.data.remote.ApiService
import com.umniyali.app.data.remote.RetrofitClient
import com.umniyali.app.data.repository.ChatRepository
import com.umniyali.app.data.repository.SettingsRepository
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(database: AppDatabase): SettingsRepository {
        return SettingsRepository(database.settingsDao())
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }

    @Provides
    @Singleton
    fun provideChatRepository(apiService: ApiService, database: AppDatabase): ChatRepository {
        return ChatRepository(apiService, database.messageDao())
    }
}
