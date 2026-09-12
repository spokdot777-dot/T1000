package com.aura.ai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aura.ai.data.local.dao.MemoryDao
import com.aura.ai.data.local.dao.MessageDao
import com.aura.ai.data.local.dao.SkillDao
import com.aura.ai.data.local.dao.TaskHistoryDao
import com.aura.ai.data.local.entity.MemoryEntity
import com.aura.ai.data.local.entity.MessageEntity
import com.aura.ai.data.local.entity.SkillEntity
import com.aura.ai.data.local.entity.TaskHistoryEntity

@Database(
    entities = [
        MemoryEntity::class,
        SkillEntity::class,
        TaskHistoryEntity::class,
        MessageEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class T1000Database : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun skillDao(): SkillDao
    abstract fun taskHistoryDao(): TaskHistoryDao
    abstract fun messageDao(): MessageDao

    companion object {
        const val NAME = "t1000.db"
    }
}
