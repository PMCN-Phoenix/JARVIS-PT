package com.usher.tactical.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usher.tactical.domain.model.Host
import com.usher.tactical.domain.model.HostAttribute
import com.usher.tactical.domain.model.LockStatus
import com.usher.tactical.domain.repository.HostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val host: Host? = null,
    val attributes: List<HostAttribute> = emptyList(),
    val resources: List<com.usher.tactical.domain.model.Resource> = emptyList(),
    val systemLogs: List<com.usher.tactical.domain.model.SystemLog> = emptyList(),
    val lockStatus: LockStatus? = null,
    val isLoading: Boolean = true
) {
    /** 属性按分类分组 */
    val physicalAttrs: List<HostAttribute> get() = attributes.filter { it.category == "体能" }
    val volleyballAttrs: List<HostAttribute> get() = attributes.filter { it.category == "排球技术" }
    val firearmAttrs: List<HostAttribute> get() = attributes.filter { it.category == "枪械战斗" }
}

@HiltViewModel
class HostViewModel @Inject constructor(
    private val hostRepository: HostRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                hostRepository.observeHost(),
                hostRepository.observeAttributes(),
                hostRepository.observeResources(),
                hostRepository.observeLockStatus(),
                hostRepository.observeSystemLogs()
            ) { host, attrs, resources, lock, logs ->
                DashboardState(
                    host = host,
                    attributes = attrs,
                    resources = resources,
                    systemLogs = logs,
                    lockStatus = lock,
                    isLoading = false
                )
            }.collect { _state.value = it }
        }
    }

    fun triggerLock() {
        viewModelScope.launch {
            hostRepository.setLocked(true)
            hostRepository.insertSystemLog("error", "penalty", "用户手动触发系统锁死。")
        }
    }

    suspend fun resetAll() {
        // Clear and reinitialize — handled by database destructive migration on next version bump
        hostRepository.setLocked(false)
        hostRepository.updateLockCounter(0)
        hostRepository.insertSystemLog("info", "system", "系统完全重置。")
    }
}
