package com.aura.ai.data.repository

import com.aura.ai.data.local.dao.SkillDao
import com.aura.ai.data.local.entity.SkillEntity
import com.aura.ai.data.local.entity.SkillStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillRepository @Inject constructor(
    private val dao: SkillDao,
) {
    fun observeAll(): Flow<List<SkillEntity>> = dao.observeAll()

    suspend fun findRelevant(query: String, limit: Int = 5): List<SkillEntity> = dao.findRelevant(query, limit)

    suspend fun byId(id: Long): SkillEntity? = dao.byId(id)

    suspend fun create(name: String, description: String, procedureJson: String): Long =
        dao.insert(
            SkillEntity(
                name = name,
                description = description,
                procedureJson = procedureJson,
                status = SkillStatus.DRAFT,
            ),
        )

    /**
     * Store an improved version of an existing skill instead of overwriting it,
     * preserving lineage via [parentId] so a regression can be rolled back.
     */
    suspend fun evolve(parent: SkillEntity, newProcedureJson: String, newDescription: String? = null): Long {
        val nextVersion = (dao.latestVersionOf(parent.parentId ?: parent.id) ?: parent.version) + 1
        return dao.insert(
            parent.copy(
                id = 0,
                parentId = parent.parentId ?: parent.id,
                version = nextVersion,
                description = newDescription ?: parent.description,
                procedureJson = newProcedureJson,
                status = SkillStatus.DRAFT,
                successRate = 0f,
                timesUsed = 0,
                timesSucceeded = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun markVerified(id: Long) {
        dao.byId(id)?.let { dao.update(it.copy(status = SkillStatus.VERIFIED, updatedAt = System.currentTimeMillis())) }
    }

    suspend fun recordUse(id: Long, succeeded: Boolean) = dao.recordUse(id, if (succeeded) 1 else 0)
}
