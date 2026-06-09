package com.usher.tactical.domain.repository

import com.usher.tactical.domain.model.Task
import com.usher.tactical.domain.model.TaskCheckIn
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeActiveTasks(): Flow<List<Task>>
    fun observeTasksByType(type: String): Flow<List<Task>>
    fun observeSubTasks(parentId: String): Flow<List<Task>>

    suspend fun getActiveDailyTasks(): List<Task>
    suspend fun getTaskById(id: String): Task?
    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun completeTask(taskId: String)
    suspend fun deleteTask(taskId: String)

    suspend fun checkIn(taskId: String, evidence: String? = null)
    suspend fun getTodayCheckIns(): List<TaskCheckIn>
    suspend fun getLatestCheckIn(taskId: String): TaskCheckIn?
}
