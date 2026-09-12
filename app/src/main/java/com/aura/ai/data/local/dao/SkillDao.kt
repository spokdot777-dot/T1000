package com.aura.ai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aura.ai.data.local.entity.SkillEntity
import com.aura.ai.data.local.entity.SkillStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(skill: SkillEntity): Long

    @Update
    suspend fun update(skill: SkillEntity)

    @Query("SELECT * FROM skills ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<SkillEntity>>

    @Query("SELECT * FROM skills WHERE id = :id")
    suspend fun byId(id: Long): SkillEntity?

    @Query("SELECT * FROM skills WHERE status = :status ORDER BY successRate DESC")
    suspend fun byStatus(status: SkillStatus): List<SkillEntity>

    @Query(
        """
        SELECT * FROM skills
        WHERE status != 'DEPRECATED'
          AND (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        ORDER BY successRate DESC, timesUsed DESC
        LIMIT :limit
        """,
    )
    suspend fun findRelevant(query: String, limit: Int = 5): List<SkillEntity>

    @Query("SELECT MAX(version) FROM skills WHERE parentId = :parentId OR id = :parentId")
    suspend fun latestVersionOf(parentId: Long): Int?

    @Query(
        """
        UPDATE skills
        SET timesUsed = timesUsed + 1,
            timesSucceeded = timesSucceeded + :succeededDelta,
            successRate = CAST(timesSucceeded + :succeededDelta AS REAL) / (timesUsed + 1),
            updatedAt = :now
        WHERE id = :id
        """,
    )
    suspend fun recordUse(id: Long, succeededDelta: Int, now: Long = System.currentTimeMillis())
}
