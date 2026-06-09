package com.usher.tactical.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lock_status")
data class LockStatusEntity(
    @PrimaryKey
    val hostId: String = "usher",
    val disqualificationCounter: Int = 0,
    val isLocked: Boolean = false,
    val lastSettlement: Long = System.currentTimeMillis()
)
