package com.habits.coooins.croowsss.ttask.ui.goals.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalEntity
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalStatus
import com.habits.coooins.croowsss.ttask.data.goal.GoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GoalDetailUiState(
    val goal: GoalEntity? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false
)

class GoalDetailViewModel(
    private val goalRepository: GoalRepository,
    private val goalId: Long?
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GoalDetailUiState())
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            if (goalId != null) {
                goalRepository.getGoalById(goalId).collect { goal ->
                    _uiState.value = GoalDetailUiState(
                        goal = goal,
                        isLoading = false,
                        isEditing = false
                    )
                }
            } else {
                _uiState.value = GoalDetailUiState(
                    isLoading = false,
                    isEditing = true
                )
            }
        }
    }
    
    fun saveGoal(
        title: String,
        description: String,
        targetValue: Float,
        currentValue: Float,
        unit: String,
        status: GoalStatus
    ) {
        viewModelScope.launch {
            if (goalId == null) {
                goalRepository.insertGoal(
                    GoalEntity(
                        title = title,
                        description = description,
                        targetValue = targetValue,
                        currentValue = currentValue,
                        unit = unit,
                        status = status
                    )
                )
            } else {
                _uiState.value.goal?.let { goal ->
                    goalRepository.updateGoal(
                        goal.copy(
                            title = title,
                            description = description,
                            targetValue = targetValue,
                            currentValue = currentValue,
                            unit = unit,
                            status = status
                        )
                    )
                }
            }
        }
    }
    
    fun updateProgress(newValue: Float) {
        viewModelScope.launch {
            _uiState.value.goal?.let { goal ->
                val clampedValue = newValue.coerceIn(0f, goal.targetValue)
                val newStatus = if (clampedValue >= goal.targetValue) {
                    GoalStatus.COMPLETED
                } else {
                    GoalStatus.IN_PROGRESS
                }
                goalRepository.updateGoal(
                    goal.copy(
                        currentValue = clampedValue,
                        status = newStatus
                    )
                )
            }
        }
    }
    
    fun toggleEditing() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }
}
