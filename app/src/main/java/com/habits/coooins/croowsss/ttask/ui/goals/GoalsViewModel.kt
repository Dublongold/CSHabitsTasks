package com.habits.coooins.croowsss.ttask.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalEntity
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalStatus
import com.habits.coooins.croowsss.ttask.data.goal.GoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GoalsUiState(
    val goals: List<GoalEntity> = emptyList(),
    val isLoading: Boolean = true
)

class GoalsViewModel(
    private val goalRepository: GoalRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            goalRepository.getAllGoals().collect { goals ->
                _uiState.value = GoalsUiState(
                    goals = goals,
                    isLoading = false
                )
            }
        }
    }
    
    fun incrementProgress(goalId: Long, amount: Float) {
        viewModelScope.launch {
            goalRepository.getGoalById(goalId).firstOrNull()?.let { goal ->
                val newValue = (goal.currentValue + amount).coerceAtMost(goal.targetValue)
                val newStatus = if (newValue >= goal.targetValue) {
                    GoalStatus.COMPLETED
                } else {
                    goal.status
                }
                goalRepository.updateGoal(
                    goal.copy(
                        currentValue = newValue,
                        status = newStatus
                    )
                )
            }
        }
    }
    
    fun decrementProgress(goalId: Long, amount: Float) {
        viewModelScope.launch {
            goalRepository.getGoalById(goalId).firstOrNull()?.let { goal ->
                val newValue = (goal.currentValue - amount).coerceAtLeast(0f)
                goalRepository.updateGoal(
                    goal.copy(
                        currentValue = newValue,
                        status = GoalStatus.IN_PROGRESS
                    )
                )
            }
        }
    }
    
    fun archiveGoal(goalId: Long) {
        viewModelScope.launch {
            goalRepository.getGoalById(goalId).firstOrNull()?.let { goal ->
                goalRepository.updateGoal(goal.copy(status = GoalStatus.ARCHIVED))
            }
        }
    }
}
