package com.usher.tactical.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usher.tactical.core.database.entity.SystemLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemLogDao {
    @Query("SELECT * FROM system_log WHERE hostId = 'usher' ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<SystemLogEntity>>

    @Query("SELECT * FROM system_log WHERE hostId = 'usher' AND category = :category ORDER BY timestamp DESC")
    fun observeByCategory(category: String): Flow<List<SystemLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SystemLogEntity)

    @Query("DELETE FROM system_log WHERE hostId = 'usher' AND timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}
