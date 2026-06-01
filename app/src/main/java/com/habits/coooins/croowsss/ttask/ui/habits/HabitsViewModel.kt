package com.habits.coooins.croowsss.ttask.ui.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitLogEntity
import com.habits.coooins.croowsss.ttask.data.habit.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class HabitsUiState(
    val habits: List<HabitEntity> = emptyList(),
    val habitLogs: Map<Long, List<HabitLogEntity>> = emptyMap(),
    val isLoading: Boolean = true
)

class HabitsViewModel(
    private val habitRepository: HabitRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HabitsUiState())
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            habitRepository.getAllHabits().collect { habits ->
                val logsMap = mutableMapOf<Long, List<HabitLogEntity>>()
                habits.forEach { habit ->
                    habitRepository.getHabitLogs(habit.id).firstOrNull()?.let { logs ->
                        logsMap[habit.id] = logs
                    }
                }
                _uiState.value = HabitsUiState(
                    habits = habits,
                    habitLogs = logsMap,
                    isLoading = false
                )
            }
        }
    }
    
    fun toggleHabitActive(habitId: Long) {
        viewModelScope.launch {
            habitRepository.getHabitById(habitId).firstOrNull()?.let { habit ->
                habitRepository.updateHabit(habit.copy(active = !habit.active))
            }
        }
    }
    
    fun logHabitToday(habitId: Long, count: Int) {
        viewModelScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            habitRepository.getTodayLog(habitId).firstOrNull()?.let { existingLog ->
                habitRepository.updateHabitLog(existingLog.copy(count = count))
            } ?: habitRepository.insertHabitLog(
                HabitLogEntity(habitId = habitId, date = today, count = count)
            )
        }
    }
}
