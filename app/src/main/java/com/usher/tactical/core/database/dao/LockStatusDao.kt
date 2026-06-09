package com.usher.tactical.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usher.tactical.core.database.entity.LockStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LockStatusDao {
    @Query("SELECT * FROM lock_status WHERE hostId = 'usher'")
    fun observe(): Flow<LockStatusEntity?>

    @Query("SELECT * FROM lock_status WHERE hostId = 'usher'")
    suspend fun get(): LockStatusEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(status: LockStatusEntity)

    @Query("UPDATE lock_status SET disqualificationCounter = :counter, lastSettlement = :now WHERE hostId = 'usher'")
    suspend fun updateCounter(counter: Int, now: Long = System.currentTimeMillis())

    @Query("UPDATE lock_status SET isLocked = :locked WHERE hostId = 'usher'")
    suspend fun setLocked(locked: Boolean)
}
