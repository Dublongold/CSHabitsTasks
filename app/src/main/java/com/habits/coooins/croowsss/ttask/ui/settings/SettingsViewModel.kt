package com.habits.coooins.croowsss.ttask.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.settings.SettingsManager
import com.habits.coooins.croowsss.ttask.data.task.TaskPriority
import com.habits.coooins.croowsss.ttask.data.focussession.FocusSessionRepository
import com.habits.coooins.croowsss.ttask.data.goal.GoalRepository
import com.habits.coooins.croowsss.ttask.data.habit.HabitRepository
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val firstDayOfWeek: Int = 1,
    val defaultTaskPriority: TaskPriority = TaskPriority.MEDIUM,
    val isLoading: Boolean = true
)

class SettingsViewModel(
    private val settingsManager: SettingsManager,
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val goalRepository: GoalRepository,
    private val focusSessionRepository: FocusSessionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsManager.firstDayOfWeek,
                settingsManager.defaultTaskPriority
            ) { firstDay, priority ->
                SettingsUiState(
                    firstDayOfWeek = firstDay,
                    defaultTaskPriority = priority,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    fun setFirstDayOfWeek(day: Int) {
        viewModelScope.launch {
            settingsManager.setFirstDayOfWeek(day)
        }
    }
    
    fun setDefaultPriority(taskPriority: TaskPriority) {
        viewModelScope.launch {
            settingsManager.setDefaultPriority(taskPriority)
        }
    }
    
    fun resetAllData() {
        viewModelScope.launch {
            taskRepository.deleteAllTasks()
            goalRepository.deleteAllGoals()
            focusSessionRepository.deleteAllSessions()
            habitRepository.deleteAllHabits()
            // Habits will cascade delete their logs
        }
    }
}
