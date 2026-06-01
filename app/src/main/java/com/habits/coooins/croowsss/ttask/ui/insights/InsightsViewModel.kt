package com.habits.coooins.croowsss.ttask.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalStatus
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus
import com.habits.coooins.croowsss.ttask.data.focussession.FocusSessionRepository
import com.habits.coooins.croowsss.ttask.data.goal.GoalRepository
import com.habits.coooins.croowsss.ttask.data.habit.HabitRepository
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class InsightsData(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val completionRate: Float = 0f,
    val activeHabits: Int = 0,
    val habitConsistency: Float = 0f,
    val totalFocusMinutes: Long = 0,
    val goalsProgress: Float = 0f
)

data class InsightsUiState(
    val data: InsightsData = InsightsData(),
    val timeWindow: TimeWindow = TimeWindow.WEEK,
    val isLoading: Boolean = true
)

enum class TimeWindow(val days: Int, val displayName: String) {
    WEEK(7, "Week"),
    MONTH(30, "Month"),
    ALL(-1, "All")
}

class InsightsViewModel(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val goalRepository: GoalRepository,
    private val focusSessionRepository: FocusSessionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            combine(
                taskRepository.listenAllTasks(),
                habitRepository.getAllHabits(),
                goalRepository.getAllGoals(),
                focusSessionRepository.listenAllSessions()
            ) { tasks, habits, goals, sessions ->
                val timeWindow = _uiState.value.timeWindow
                val cutoffTime = if (timeWindow == TimeWindow.ALL) {
                    0L
                } else {
                    System.currentTimeMillis() - (timeWindow.days * 24 * 60 * 60 * 1000L)
                }
                
                val recentTasks = tasks.filter { it.createdAt >= cutoffTime }
                val completedTasks = recentTasks.count { it.status == TaskStatus.DONE }
                val completionRate = if (recentTasks.isNotEmpty()) {
                    completedTasks.toFloat() / recentTasks.size
                } else 0f
                
                val activeHabits = habits.count { it.active }
                
                val recentSessions = sessions.filter { 
                    it.startAt >= cutoffTime && it.endAt != null 
                }
                val totalMinutes = recentSessions.sumOf { session ->
                    ((session.endAt ?: 0) - session.startAt) / 1000 / 60
                }
                
                val activeGoals = goals.filter { it.status == GoalStatus.IN_PROGRESS }
                val goalsProgress = if (activeGoals.isNotEmpty()) {
                    activeGoals.map { 
                        if (it.targetValue > 0) it.currentValue / it.targetValue else 0f 
                    }.average().toFloat()
                } else 0f
                
                InsightsUiState(
                    data = InsightsData(
                        totalTasks = recentTasks.size,
                        completedTasks = completedTasks,
                        completionRate = completionRate,
                        activeHabits = activeHabits,
                        habitConsistency = 0.75f, // Simplified
                        totalFocusMinutes = totalMinutes,
                        goalsProgress = goalsProgress
                    ),
                    timeWindow = timeWindow,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun setTimeWindow(window: TimeWindow) {
        _uiState.update { it.copy(timeWindow = window) }
        loadData()
    }
}
