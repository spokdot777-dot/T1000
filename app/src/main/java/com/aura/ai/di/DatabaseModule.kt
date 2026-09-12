package com.aura.ai.di

import android.content.Context
import androidx.room.Room
import com.aura.ai.data.local.T1000Database
import com.aura.ai.data.local.dao.MemoryDao
import com.aura.ai.data.local.dao.MessageDao
import com.aura.ai.data.local.dao.SkillDao
import com.aura.ai.data.local.dao.TaskHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): T1000Database =
        Room.databaseBuilder(context, T1000Database::class.java, T1000Database.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideMemoryDao(db: T1000Database): MemoryDao = db.memoryDao()
    @Provides fun provideSkillDao(db: T1000Database): SkillDao = db.skillDao()
    @Provides fun provideTaskHistoryDao(db: T1000Database): TaskHistoryDao = db.taskHistoryDao()
    @Provides fun provideMessageDao(db: T1000Database): MessageDao = db.messageDao()
}
