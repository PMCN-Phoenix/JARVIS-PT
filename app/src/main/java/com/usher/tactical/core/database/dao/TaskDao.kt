package com.usher.tactical.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.usher.tactical.core.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM task WHERE hostId = 'usher' AND status != 'archived' ORDER BY orderIndex")
    fun observeActive(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE hostId = 'usher' AND type = :type AND status != 'archived' ORDER BY orderIndex")
    fun observeByType(type: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE hostId = 'usher' AND type = 'daily' AND status = 'active'")
    suspend fun getActiveDailyTasks(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE hostId = 'usher' AND id = :id")
    suspend fun getById(id: String): TaskEntity?

    @Query("SELECT * FROM task WHERE hostId = 'usher' AND parentId = :parentId ORDER BY orderIndex")
    fun observeByParent(parentId: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("UPDATE task SET status = 'archived' WHERE hostId = 'usher' AND status = 'completed' AND completedAt < :before")
    suspend fun archiveCompleted(before: Long)

    @Query("DELETE FROM task WHERE hostId = 'usher' AND id = :id")
    suspend fun deleteById(id: String)
}
