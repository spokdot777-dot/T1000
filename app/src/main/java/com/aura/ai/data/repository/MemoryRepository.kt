package com.aura.ai.data.repository

import com.aura.ai.data.local.dao.MemoryDao
import com.aura.ai.data.local.entity.MemoryCategory
import com.aura.ai.data.local.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor(
    private val dao: MemoryDao,
) {
    fun observeAll(): Flow<List<MemoryEntity>> = dao.observeAll()

    suspend fun remember(
        content: String,
        category: MemoryCategory = MemoryCategory.GENERAL,
        importance: Float = 0.5f,
        source: String? = null,
        skillId: Long? = null,
    ): Long = dao.insert(
        MemoryEntity(
            content = content,
            category = category,
            importance = importance,
            source = source,
            skillId = skillId,
        ),
    )

    /** Retrieve context relevant to a request and mark it accessed. */
    suspend fun recall(query: String, limit: Int = 8): List<MemoryEntity> {
        val hits = dao.search(query, limit).ifEmpty { dao.recentImportant(limit) }
        hits.forEach { dao.touch(it.id) }
        return hits
    }

    suspend fun update(memory: MemoryEntity) = dao.update(memory)
    suspend fun forget(id: Long) = dao.delete(id)
}
