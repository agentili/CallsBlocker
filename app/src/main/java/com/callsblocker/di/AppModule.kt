package com.callsblocker.di

import android.content.Context
import androidx.room.TypeConverters
import com.callsblocker.data.AppDatabase
import com.callsblocker.data.BlockedCallLogDao
import com.callsblocker.data.BlockedEntryDao
import com.callsblocker.data.CallActionConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = AppDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideBlockedEntryDao(db: AppDatabase): BlockedEntryDao = db.blockedEntryDao()

    @Singleton
    @Provides
    fun provideBlockedCallLogDao(db: AppDatabase): BlockedCallLogDao = db.blockedCallLogDao()
}
