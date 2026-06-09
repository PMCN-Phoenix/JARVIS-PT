package com.usher.tactical.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_template",
    foreignKeys = [
        ForeignKey(
            entity = HostEntity::class,
            parentColumns = ["id"],
            childColumns = ["hostId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hostId")]
)
data class TaskTemplateEntity(
    @PrimaryKey
    val id: String,
    val hostId: String = "usher",
    val type: String,                // "daily" | "main" | "side"
    val name: String,
    val configJson: String,          // 完整任务配置JSON
    val createdAt: Long = System.currentTimeMillis()
)
