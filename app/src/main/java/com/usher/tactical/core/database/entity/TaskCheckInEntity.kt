package com.usher.tactical.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_check_in",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId")]
)
data class TaskCheckInEntity(
    @PrimaryKey
    val id: String,
    val taskId: String,
    val checkinTime: Long = System.currentTimeMillis(),
    val evidence: String? = null     // 用户输入的值或文本（如 "83.8" 或 "完成1小时"）
)
