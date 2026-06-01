package com.habits.coooins.croowsss.ttask.ui.habits.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitEntity
import com.habits.coooins.croowsss.ttask.data.habit.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DeleteHabitsUiState(
    val habits: List<HabitEntity> = emptyList(),
    val selectedHabitIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false
)

class DeleteHabitsViewModel(
    private val habitRepository: HabitRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DeleteHabitsUiState())
    val uiState: StateFlow<DeleteHabitsUiState> = _uiState.asStateFlow()
    
    init {
        loadHabits()
    }
    
    private fun loadHabits() {
        viewModelScope.launch {
            habitRepository.getAllHabits().collect { habits ->
                _uiState.update { state ->
                    state.copy(
                        habits = habits,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun toggleHabitSelection(habitId: Long) {
        _uiState.update { state ->
            val newSelection = if (habitId in state.selectedHabitIds) {
                state.selectedHabitIds - habitId
            } else {
                state.selectedHabitIds + habitId
            }
            state.copy(selectedHabitIds = newSelection)
        }
    }
    
    fun toggleSelectAll() {
        _uiState.update { state ->
            val newSelection = if (state.selectedHabitIds.size == state.habits.size) {
                emptySet()
            } else {
                state.habits.map { it.id }.toSet()
            }
            state.copy(selectedHabitIds = newSelection)
        }
    }
    
    fun showDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }
    
    fun hideDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }
    
    fun deleteSelectedHabits() {
        viewModelScope.launch {
            val habitIds = _uiState.value.selectedHabitIds
            habitIds.forEach { habitId ->
                habitRepository.getHabitById(habitId).firstOrNull()?.let { habit ->
                    habitRepository.deleteHabit(habit)
                }
            }
            _uiState.update { state ->
                state.copy(
                    selectedHabitIds = emptySet(),
                    showDeleteDialog = false
                )
            }
        }
    }
}
