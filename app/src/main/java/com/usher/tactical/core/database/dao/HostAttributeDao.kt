package com.usher.tactical.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usher.tactical.core.database.entity.HostAttributeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HostAttributeDao {
    @Query("SELECT * FROM host_attribute WHERE hostId = 'usher' ORDER BY category, attrName")
    fun observeAll(): Flow<List<HostAttributeEntity>>

    @Query("SELECT * FROM host_attribute WHERE hostId = 'usher' AND category = :category")
    fun observeByCategory(category: String): Flow<List<HostAttributeEntity>>

    @Query("SELECT * FROM host_attribute WHERE hostId = 'usher' AND category = :category")
    suspend fun getByCategory(category: String): List<HostAttributeEntity>

    @Query("UPDATE host_attribute SET value = value + :delta, updatedAt = :now WHERE hostId = 'usher' AND attrName = :attrName")
    suspend fun addValue(attrName: String, delta: Float, now: Long = System.currentTimeMillis())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(attr: HostAttributeEntity)

    @Query("DELETE FROM host_attribute WHERE hostId = 'usher'")
    suspend fun deleteAll()
}
