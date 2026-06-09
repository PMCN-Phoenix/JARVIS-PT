package com.usher.tactical.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.usher.tactical.core.database.entity.HostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HostDao {
    @Query("SELECT * FROM host WHERE id = 'usher'")
    fun observe(): Flow<HostEntity?>

    @Query("SELECT * FROM host WHERE id = 'usher'")
    suspend fun get(): HostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(host: HostEntity)

    @Update
    suspend fun update(host: HostEntity)
}
