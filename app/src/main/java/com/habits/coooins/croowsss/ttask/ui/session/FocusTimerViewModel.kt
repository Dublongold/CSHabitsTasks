package com.habits.coooins.croowsss.ttask.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.focussession.local.FocusSessionEntity
import com.habits.coooins.croowsss.ttask.data.focussession.FocusSessionRepository
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import com.habits.coooins.croowsss.ttask.data.task.TaskInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ShortSessionData(
    val label: String? = null,
    val startAt: Long = System.currentTimeMillis(),
    val elapsedMillis: Long = 0L
) {
    val elapsedSeconds = elapsedMillis / 1000
    fun toEntity(taskId: Long? = null): FocusSessionEntity {
        return FocusSessionEntity(
            startAt = startAt,
            label = label,
            endAt = startAt + elapsedMillis,
            taskId = taskId
        )
    }
    fun updateElapsedTime(value: Long): ShortSessionData = copy(elapsedMillis = value)
    fun increaseElapsedTime(): ShortSessionData = updateElapsedTime(elapsedMillis + 1000)

    companion object {
        val Default = ShortSessionData()
    }
}

data class FocusTimerUiState(
    val readyToContinue: Boolean = false,
    val lastSession: ShortSessionData = ShortSessionData.Default,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val taskId: Long? = null,
    val tasksToFocus: List<TaskInfo> = emptyList(),
) {
    companion object {
        val Default = FocusTimerUiState()
    }
}

class FocusTimerViewModel(
    private val focusSessionRepository: FocusSessionRepository,
    taskRepository: TaskRepository
) : ViewModel() {
    private val sessionNames = focusSessionRepository.listenAllSessionNames()
    private val tasksToFocus = taskRepository.listenAllTaskInfo()
    private val _uiState = MutableStateFlow(FocusTimerUiState.Default)

    val uiState: StateFlow<FocusTimerUiState> = combine(
        sessionNames,
        tasksToFocus,
        _uiState
    ) { sessionNames, tasksToFocus, uiState ->
        uiState.copy(
            readyToContinue = uiState.lastSession.label in sessionNames,
            tasksToFocus = tasksToFocus,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        FocusTimerUiState.Default
    )

    private var timerJob: Job? = null

    fun updateLabel(label: String) {
        _uiState.update { it.copy(lastSession = it.lastSession.copy(label = label)) }
    }

    fun updateTaskId(taskId: Long?) {
        _uiState.update {
            it.copy(taskId = taskId)
        }
    }

    fun startSession() {
        _uiState.update {
            it.copy(
                isRunning = true,
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_uiState.value.isRunning && !_uiState.value.isPaused) {
                    _uiState.update {
                        it.copy(lastSession = it.lastSession.increaseElapsedTime())
                    }
                }
            }
        }
    }

    fun pauseSession() {
        _uiState.update { it.copy(isPaused = true) }
    }

    fun resumeSession() {
        _uiState.update { it.copy(isPaused = false) }
    }

    fun stopSession() {
        viewModelScope.launch {
            focusSessionRepository.upsertSession(
                _uiState.value.let { state ->
                    state.lastSession.toEntity(taskId = state.taskId)
                }
            )
            _uiState.update {
                it.copy(
                    isRunning = false,
                    isPaused = false,
                )
            }
            timerJob?.cancel()
            timerJob = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
