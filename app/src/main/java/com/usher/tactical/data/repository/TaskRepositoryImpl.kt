package com.usher.tactical.data.repository

import com.usher.tactical.core.database.dao.TaskCheckInDao
import com.usher.tactical.core.database.dao.TaskDao
import com.usher.tactical.core.database.entity.TaskCheckInEntity
import com.usher.tactical.core.database.entity.TaskEntity
import com.usher.tactical.domain.model.Task
import com.usher.tactical.domain.model.TaskCheckIn
import com.usher.tactical.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val checkInDao: TaskCheckInDao
) : TaskRepository {

    override fun observeActiveTasks(): Flow<List<Task>> =
        taskDao.observeActive().map { list -> list.map { it.toDomain() } }

    override fun observeTasksByType(type: String): Flow<List<Task>> =
        taskDao.observeByType(type).map { list -> list.map { it.toDomain() } }

    override fun observeSubTasks(parentId: String): Flow<List<Task>> =
        taskDao.observeByParent(parentId).map { list -> list.map { it.toDomain() } }

    override suspend fun getActiveDailyTasks(): List<Task> =
        taskDao.getActiveDailyTasks().map { it.toDomain() }

    override suspend fun getTaskById(id: String): Task? =
        taskDao.getById(id)?.toDomain()

    override suspend fun createTask(task: Task) {
        taskDao.upsert(TaskEntity(
            id = task.id.ifEmpty { UUID.randomUUID().toString() },
            parentId = task.parentId,
            type = task.type,
            name = task.name,
            requirementJson = task.requirementJson,
            rewardJson = task.rewardJson,
            orderIndex = task.orderIndex,
            dueDate = task.dueDate,
            createdAt = System.currentTimeMillis()
        ))
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(TaskEntity(
            id = task.id,
            parentId = task.parentId,
            type = task.type,
            name = task.name,
            requirementJson = task.requirementJson,
            rewardJson = task.rewardJson,
            status = task.status,
            orderIndex = task.orderIndex,
            dueDate = task.dueDate,
            completedAt = task.completedAt,
            updatedAt = System.currentTimeMillis()
        ))
    }

    override suspend fun completeTask(taskId: String) {
        val now = System.currentTimeMillis()
        taskDao.getById(taskId)?.let {
            taskDao.update(it.copy(status = Task.STATUS_COMPLETED, completedAt = now, updatedAt = now))
        }
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteById(taskId)
    }

    override suspend fun checkIn(taskId: String, evidence: String?) {
        checkInDao.insert(TaskCheckInEntity(
            id = UUID.randomUUID().toString(),
            taskId = taskId,
            evidence = evidence
        ))
    }

    override suspend fun getTodayCheckIns(): List<TaskCheckIn> =
        checkInDao.getTodayDailyCheckIns().map { it.toDomain() }

    override suspend fun getLatestCheckIn(taskId: String): TaskCheckIn? =
        checkInDao.getLatestByTask(taskId)?.toDomain()

    // --- Mappers ---

    private fun TaskEntity.toDomain() = Task(
        id = id, parentId = parentId, type = type, name = name,
        requirementJson = requirementJson, rewardJson = rewardJson,
        status = status, orderIndex = orderIndex, dueDate = dueDate,
        completedAt = completedAt
    )

    private fun TaskCheckInEntity.toDomain() = TaskCheckIn(id, taskId, checkinTime, evidence)
}
