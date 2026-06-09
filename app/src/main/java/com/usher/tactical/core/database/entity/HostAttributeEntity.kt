package com.usher.tactical.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "host_attribute",
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
data class HostAttributeEntity(
    @PrimaryKey
    val id: String,
    val hostId: String = "usher",
    val category: String,       // "体能" | "排球技术" | "枪械战斗"
    val attrName: String,       // "力量" | "速度" | "体力" | "弹跳" 等
    val value: Float,
    val detailsJson: String? = null,  // 分项详情JSON，如 {"core":83.8,"arm":83.8}
    val updatedAt: Long = System.currentTimeMillis()
)
