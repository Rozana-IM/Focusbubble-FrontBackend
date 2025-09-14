package com.focusbubble.di

import android.content.Context
import androidx.room.Room
import com.focusbubble.data.AppDatabase
import com.focusbubble.data.dao.BlockedAppDao
import com.focusbubble.data.repository.BlockedAppsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "blocked_apps_db"
        ).build()
    }

    @Provides
    fun provideBlockedAppDao(database: AppDatabase): BlockedAppDao {
        return database.blockedAppDao()
    }

    @Provides
    @Singleton
    fun provideBlockedAppsRepository(dao: BlockedAppDao): BlockedAppsRepository {
        return BlockedAppsRepository(dao)
    }
}
