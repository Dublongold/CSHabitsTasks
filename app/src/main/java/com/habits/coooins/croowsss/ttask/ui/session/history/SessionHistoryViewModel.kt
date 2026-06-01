package com.habits.coooins.croowsss.ttask.ui.session.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.focussession.FocusSessionRepository
import com.habits.coooins.croowsss.ttask.data.focussession.local.FocusSessionEntity
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.time.Duration.Companion.seconds

data class SessionHistoryUiState(
    val sessions: List<FocusSessionEntity> = emptyList(),
    val startAt: Long = getStartOfWeek(),
    val endAt: Long = System.currentTimeMillis(),
    val totalMinutes: Long = 0,
    val isLoading: Boolean = true
) {
    companion object {
        val Default = SessionHistoryUiState()
    }
}

private fun getStartOfWeek(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

class SessionHistoryViewModel(
    private val focusSessionRepository: FocusSessionRepository,
    taskRepository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionHistoryUiState())
    val uiState = combine(
        focusSessionRepository.listenAllSessions(),
        taskRepository.listenAllTaskInfo(),
        _uiState
    ) { sessions, taskInfoList, uiState ->
        val completedSessions = sessions.filter {
            it.endAt != null && it.endAt >= uiState.startAt && it.endAt <= uiState.endAt
        }
        completedSessions.forEach {
            it.taskTitle = taskInfoList.firstOrNull { taskInfo -> taskInfo.id == it.taskId }?.title
        }
        val totalMinutes = completedSessions.sumOf { session ->
            ((session.endAt ?: 0) - session.startAt) / 1000 / 60
        }
        SessionHistoryUiState(
            sessions = completedSessions,
            startAt = uiState.startAt,
            endAt = uiState.endAt,
            totalMinutes = totalMinutes,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        SessionHistoryUiState.Default
    )

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1.seconds)
                _uiState.update {
                    it.copy(
                        startAt = getStartOfWeek(),
                        endAt = System.currentTimeMillis()
                    )
                }
            }
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            focusSessionRepository.getSessionById(sessionId).firstOrNull()?.let { session ->
                focusSessionRepository.deleteSession(session)
            }
        }
    }

    fun updateSessionLabel(sessionId: Long, label: String) {
        viewModelScope.launch {
            focusSessionRepository.getSessionById(sessionId).firstOrNull()?.let { session ->
                focusSessionRepository.upsertSession(session.copy(label = label))
            }
        }
    }
}
