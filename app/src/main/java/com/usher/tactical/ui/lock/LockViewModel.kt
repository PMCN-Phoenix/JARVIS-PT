package com.usher.tactical.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usher.tactical.core.engine.LockEngine
import com.usher.tactical.domain.repository.HostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LockUiState(
    val isLocked: Boolean = false,
    val showRecompile: Boolean = false,
    val token: String = "",
    val countdown: Int = 300,        // 300秒
    val isCompiling: Boolean = false,
    val compileLines: List<String> = emptyList(),
    val compileDone: Boolean = false,
    val penaltyMessages: List<String> = emptyList()
)

@HiltViewModel
class LockViewModel @Inject constructor(
    private val hostRepository: HostRepository,
    private val lockEngine: LockEngine
) : ViewModel() {

    private val _state = MutableStateFlow(LockUiState())
    val state: StateFlow<LockUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            hostRepository.observeLockStatus().collect { ls ->
                _state.value = _state.value.copy(
                    isLocked = ls?.isLocked ?: false
                )
            }
        }
    }

    fun requestRecompile() {
        val token = lockEngine.generateToken()
        _state.value = _state.value.copy(
            showRecompile = true,
            token = token,
            countdown = 300
        )
        startCountdown()
    }

    private fun startCountdown() {
        viewModelScope.launch {
            for (i in 300 downTo 0) {
                if (_state.value.compileDone) break
                _state.value = _state.value.copy(countdown = i)
                delay(1000)
            }
            // 超时过期
            if (!_state.value.compileDone) {
                _state.value = _state.value.copy(
                    showRecompile = false,
                    token = ""
                )
            }
        }
    }

    fun onTokenVerified() {
        _state.value = _state.value.copy(isCompiling = true)
        viewModelScope.launch {
            val lines = listOf(
                "> Initiating system recompilation...",
                "> Penalty protocol engaged."
            )
            _state.value = _state.value.copy(compileLines = lines)
            delay(800)

            val penalty = lockEngine.executePenalty()
            val allLines = lines + penalty.map { "> - $it" } + listOf(
                "> Recompilation complete.",
                "> System lock lifted."
            )
            _state.value = _state.value.copy(compileLines = allLines)
            delay(2000)

            _state.value = _state.value.copy(
                compileDone = true,
                isLocked = false,
                showRecompile = false,
                penaltyMessages = penalty
            )
        }
    }
}
