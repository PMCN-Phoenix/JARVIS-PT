package com.usher.tactical.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.usher.tactical.core.database.entity.TaskTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTemplateDao {
    @Query("SELECT * FROM task_template WHERE hostId = 'usher' ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TaskTemplateEntity>>

    @Query("SELECT * FROM task_template WHERE hostId = 'usher' AND type = :type")
    fun observeByType(type: String): Flow<List<TaskTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(template: TaskTemplateEntity)

    @Query("DELETE FROM task_template WHERE id = :id")
    suspend fun deleteById(id: String)
}
