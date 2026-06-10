package com.usher.tactical.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usher.tactical.core.engine.RewardEngine
import com.usher.tactical.domain.model.Task
import com.usher.tactical.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskListState(
    val dailyTasks: List<Task> = emptyList(),
    val mainTasks: List<Task> = emptyList(),
    val sideTasks: List<Task> = emptyList(),
    val completedTaskIds: Set<String> = emptySet(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val rewardEngine: RewardEngine
) : ViewModel() {

    private val _state = MutableStateFlow(TaskListState())
    val state: StateFlow<TaskListState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                taskRepository.observeTasksByType(Task.TYPE_DAILY),
                taskRepository.observeTasksByType(Task.TYPE_MAIN),
                taskRepository.observeTasksByType(Task.TYPE_SIDE)
            ) { daily, main, side ->
                _state.value.copy(
                    dailyTasks = daily,
                    mainTasks = main,
                    sideTasks = side,
                    completedTaskIds = daily.filter { it.status == Task.STATUS_COMPLETED }
                        .map { it.id }.toSet(),
                    isLoading = false
                )
            }.collect { _state.value = it }
        }
    }

    fun checkIn(taskId: String, evidence: String) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId) ?: return@launch

            // 记录签到
            taskRepository.checkIn(taskId, evidence)

            // 标记任务完成
            taskRepository.completeTask(taskId)

            // 发放奖励
            rewardEngine.grantReward(task.name, task.rewardJson)

            // 更新本地状态
            _state.value = _state.value.copy(
                completedTaskIds = _state.value.completedTaskIds + taskId
            )
        }
    }
}
