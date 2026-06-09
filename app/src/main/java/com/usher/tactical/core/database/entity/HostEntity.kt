package com.usher.tactical.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "host")
data class HostEntity(
    @PrimaryKey
    val id: String = "usher",
    val displayName: String = "白厄",
    val overallScore: Float = 70f,
    val createdAt: Long = System.currentTimeMillis()
)
