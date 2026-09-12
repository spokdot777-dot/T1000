package com.aura.ai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A learned, reusable procedure. Skills are versionable: an improved version of
 * a failed procedure is stored as a new row with an incremented [version] and a
 * back-reference in [parentId], so history is preserved and rollbacks are possible.
 */
@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    /** Ordered, serialized plan steps (JSON). The reusable "how". */
    val procedureJson: String,
    val version: Int = 1,
    val parentId: Long? = null,
    val status: SkillStatus = SkillStatus.DRAFT,
    /** Rolling success rate 0.0 .. 1.0 from real executions. */
    val successRate: Float = 0f,
    val timesUsed: Int = 0,
    val timesSucceeded: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

enum class SkillStatus {
    /** Proposed, not yet verified. */
    DRAFT,

    /** Passed verification at least once, safe to use. */
    VERIFIED,

    /** Repeatedly failed; kept for diagnosis but not auto-used. */
    DEPRECATED,
}
