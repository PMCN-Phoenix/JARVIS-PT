package com.usher.tactical.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resource",
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
data class ResourceEntity(
    @PrimaryKey
    val id: String,
    val hostId: String = "usher",
    val type: String,           // "potential_point" | "specialty_point" | "universal_exp" | "hidden_exp"
    val amount: Float,
    val updatedAt: Long = System.currentTimeMillis()
)
