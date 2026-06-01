package com.habits.coooins.croowsss.ttask.ui.habits.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitLogEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitScheduleType
import com.habits.coooins.croowsss.ttask.data.habit.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HabitDetailUiState(
    val habit: HabitEntity? = null,
    val logs: List<HabitLogEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isEditing: Boolean = false
)

class HabitDetailViewModel(
    private val habitRepository: HabitRepository,
    private val habitId: Long?
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HabitDetailUiState())
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            if (habitId != null) {
                combine(
                    habitRepository.getHabitById(habitId),
                    habitRepository.getHabitLogs(habitId)
                ) { habit, logs ->
                    HabitDetailUiState(
                        habit = habit,
                        logs = logs,
                        isLoading = false,
                        isEditing = false
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } else {
                _uiState.value = HabitDetailUiState(
                    isLoading = false,
                    isEditing = true
                )
            }
        }
    }
    
    fun saveHabit(
        name: String,
        habitScheduleType: HabitScheduleType,
        targetCount: Int,
        active: Boolean
    ) {
        viewModelScope.launch {
            if (habitId == null) {
                habitRepository.insertHabit(
                    HabitEntity(
                        name = name,
                        habitScheduleType = habitScheduleType,
                        targetCount = targetCount,
                        active = active
                    )
                )
            } else {
                _uiState.value.habit?.let { habit ->
                    habitRepository.updateHabit(
                        habit.copy(
                            name = name,
                            habitScheduleType = habitScheduleType,
                            targetCount = targetCount,
                            active = active
                        )
                    )
                }
            }
        }
    }
    
    fun deleteLog(logId: Long) {
        viewModelScope.launch {
            _uiState.value.logs.find { it.id == logId }?.let { log ->
                habitRepository.deleteHabitLog(log)
            }
        }
    }
    
    fun resetHabitData() {
        viewModelScope.launch {
            habitId?.let {
                habitRepository.deleteAllLogsForHabit(it)
            }
        }
    }
    
    fun toggleEditing() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }
}
