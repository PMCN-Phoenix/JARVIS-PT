package com.usher.tactical.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usher.tactical.core.database.entity.ResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao {
    @Query("SELECT * FROM resource WHERE hostId = 'usher'")
    fun observeAll(): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resource WHERE hostId = 'usher' AND type = :type")
    suspend fun getByType(type: String): ResourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(resource: ResourceEntity)

    @Query("UPDATE resource SET amount = amount + :delta, updatedAt = :now WHERE hostId = 'usher' AND type = :type")
    suspend fun addAmount(type: String, delta: Float, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM resource WHERE hostId = 'usher' AND type = :type")
    suspend fun deleteByType(type: String)
}
