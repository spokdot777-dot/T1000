package com.aura.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persistent long-term memory. This is knowledge/context the agent retains
 * across sessions. It is NOT model weights — storing a memory does not retrain
 * the foundation model, it augments prompts via retrieval.
 */
@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val category: MemoryCategory = MemoryCategory.GENERAL,
    /** 0.0 (trivial) .. 1.0 (critical). Drives retrieval ranking and forgetting. */
    val importance: Float = 0.5f,
    /** Where this memory came from: user message, document, url, task result, etc. */
    val source: String? = null,
    /** Optional link to a skill or knowledge record this memory supports. */
    val skillId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val accessCount: Int = 0,
)

enum class MemoryCategory {
    GENERAL,
    PREFERENCE,
    FACT,
    WORKFLOW,
    CONTACT,
    TASK,
    KNOWLEDGE,
}
