package com.aura.ai.data.repository

import com.aura.ai.data.local.dao.MemoryDao
import com.aura.ai.data.local.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MemoryRepository @Inject constructor(
    private val memoryDao: MemoryDao
) {
    suspend fun rememberFact(type: String, content: String, importance: Int = 5): Long {
        return memoryDao.insertMemory(
            MemoryEntity(
                type = type,
                content = content,
                importance = importance.coerceIn(1, 10)
            )
        )
    }

    suspend fun updateMemory(id: Long, content: String, importance: Int? = null) {
        val memory = memoryDao.getMemoryById(id) ?: return
        memoryDao.updateMemory(
            memory.copy(
                content = content,
                importance = importance?.coerceIn(1, 10) ?: memory.importance,
                updatedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun forget(id: Long) {
        val memory = memoryDao.getMemoryById(id) ?: return
        memoryDao.deleteMemory(memory)
    }

    suspend fun forgetByType(type: String) {
        // Implementation would require a new DAO method
        // For now, this is a placeholder
    }

    fun searchMemories(query: String): Flow<List<MemoryEntity>> {
        return memoryDao.searchMemories(query)
    }

    fun getMemoriesByType(type: String): Flow<List<MemoryEntity>> {
        return memoryDao.getMemoriesByType(type)
    }

    fun getRelevantMemories(limit: Int = 20): Flow<List<MemoryEntity>> {
        return memoryDao.getTopMemories(limit)
    }

    suspend fun getMemoryCount(): Int {
        return memoryDao.getMemoryCount()
    }
}
