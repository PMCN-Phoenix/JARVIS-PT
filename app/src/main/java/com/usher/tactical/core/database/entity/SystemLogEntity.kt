package com.usher.tactical.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "system_log",
    foreignKeys = [
        ForeignKey(
            entity = HostEntity::class,
            parentColumns = ["id"],
            childColumns = ["hostId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hostId"), Index("timestamp")]
)
data class SystemLogEntity(
    @PrimaryKey
    val id: String,
    val hostId: String = "usher",
    val timestamp: Long = System.currentTimeMillis(),
    val level: String,           // "info" | "warning" | "error"
    val category: String,        // "checkin" | "reward" | "penalty" | "system"
    val message: String
)
