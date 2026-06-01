package com.habits.coooins.croowsss.ttask.ui.goals.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalEntity
import com.habits.coooins.croowsss.ttask.data.goal.GoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DeleteGoalsUiState(
    val goals: List<GoalEntity> = emptyList(),
    val selectedGoalIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false
)

class DeleteGoalsViewModel(
    private val goalRepository: GoalRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DeleteGoalsUiState())
    val uiState: StateFlow<DeleteGoalsUiState> = _uiState.asStateFlow()
    
    init {
        loadGoals()
    }
    
    private fun loadGoals() {
        viewModelScope.launch {
            goalRepository.getAllGoals().collect { goals ->
                _uiState.update { state ->
                    state.copy(
                        goals = goals,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun toggleGoalSelection(goalId: Long) {
        _uiState.update { state ->
            val newSelection = if (goalId in state.selectedGoalIds) {
                state.selectedGoalIds - goalId
            } else {
                state.selectedGoalIds + goalId
            }
            state.copy(selectedGoalIds = newSelection)
        }
    }
    
    fun toggleSelectAll() {
        _uiState.update { state ->
            val newSelection = if (state.selectedGoalIds.size == state.goals.size) {
                emptySet()
            } else {
                state.goals.map { it.id }.toSet()
            }
            state.copy(selectedGoalIds = newSelection)
        }
    }
    
    fun showDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }
    
    fun hideDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }
    
    fun deleteSelectedGoals() {
        viewModelScope.launch {
            val goalIds = _uiState.value.selectedGoalIds
            goalIds.forEach { goalId ->
                goalRepository.getGoalById(goalId).firstOrNull()?.let { goal ->
                    goalRepository.deleteGoal(goal)
                }
            }
            _uiState.update { state ->
                state.copy(
                    selectedGoalIds = emptySet(),
                    showDeleteDialog = false
                )
            }
        }
    }
}
