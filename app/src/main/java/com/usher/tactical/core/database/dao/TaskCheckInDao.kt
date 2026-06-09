package com.usher.tactical.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usher.tactical.core.database.entity.TaskCheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCheckInDao {
    @Query("SELECT * FROM task_check_in WHERE taskId = :taskId ORDER BY checkinTime DESC")
    fun observeByTask(taskId: String): Flow<List<TaskCheckInEntity>>

    @Query("SELECT * FROM task_check_in WHERE taskId IN (SELECT id FROM task WHERE hostId = 'usher' AND type = 'daily' AND status = 'active') AND date(checkinTime/1000, 'unixepoch') = date(:today/1000, 'unixepoch')")
    suspend fun getTodayDailyCheckIns(today: Long = System.currentTimeMillis()): List<TaskCheckInEntity>

    @Query("SELECT * FROM task_check_in WHERE taskId = :taskId ORDER BY checkinTime DESC LIMIT 1")
    suspend fun getLatestByTask(taskId: String): TaskCheckInEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: TaskCheckInEntity)
}
