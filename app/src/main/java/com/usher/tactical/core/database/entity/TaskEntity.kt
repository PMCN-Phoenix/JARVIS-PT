package com.usher.tactical.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task",
    foreignKeys = [
        ForeignKey(
            entity = HostEntity::class,
            parentColumns = ["id"],
            childColumns = ["hostId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hostId"), Index("parentId")]
)
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val hostId: String = "usher",
    val parentId: String? = null,      // 主线子目标挂载
    val type: String,                  // "daily" | "main" | "main_sub" | "side"
    val name: String,
    val requirementJson: String,       // 要求配置JSON
    val rewardJson: String,            // 奖励配置JSON
    val status: String = "active",     // "active" | "completed" | "archived"
    val orderIndex: Float = 0f,
    val dueDate: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
