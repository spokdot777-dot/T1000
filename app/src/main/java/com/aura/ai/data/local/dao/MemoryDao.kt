package com.aura.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aura.ai.data.local.entity.MemoryCategory
import com.aura.ai.data.local.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: MemoryEntity): Long

    @Update
    suspend fun update(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM memories ORDER BY importance DESC, lastAccessedAt DESC")
    fun observeAll(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE category = :category ORDER BY importance DESC")
    suspend fun byCategory(category: MemoryCategory): List<MemoryEntity>

    /**
     * Naive lexical retrieval: matches the query against content, ranked by
     * importance and recency. A production build would layer embeddings on top,
     * but this keeps retrieval fully on-device with no external service.
     */
    @Query(
        """
        SELECT * FROM memories
        WHERE content LIKE '%' || :query || '%'
        ORDER BY importance DESC, accessCount DESC, lastAccessedAt DESC
        LIMIT :limit
        """,
    )
    suspend fun search(query: String, limit: Int = 8): List<MemoryEntity>

    @Query("UPDATE memories SET accessCount = accessCount + 1, lastAccessedAt = :now WHERE id = :id")
    suspend fun touch(id: Long, now: Long = System.currentTimeMillis())

    @Query("SELECT * FROM memories ORDER BY importance DESC, lastAccessedAt DESC LIMIT :limit")
    suspend fun recentImportant(limit: Int = 12): List<MemoryEntity>
}
