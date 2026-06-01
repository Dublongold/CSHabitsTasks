package com.habits.coooins.croowsss.ttask.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalEntity
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalStatus
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitLogEntity
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus
import com.habits.coooins.croowsss.ttask.data.goal.GoalRepository
import com.habits.coooins.croowsss.ttask.data.habit.HabitRepository
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val todayTasks: List<TaskEntity> = emptyList(),
    val activeHabits: List<HabitEntity> = emptyList(),
    val activeGoals: List<GoalEntity> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val goalRepository: GoalRepository,
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            combine(
                taskRepository.getTodayTasks(),
                habitRepository.getActiveHabits(),
                goalRepository.getGoalsByStatus(GoalStatus.IN_PROGRESS)
            ) { tasks, habits, goals ->
                HomeUiState(
                    todayTasks = tasks,
                    activeHabits = habits.take(5),
                    activeGoals = goals.take(3),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun markTaskDone(taskId: Long) {
        viewModelScope.launch {
            taskRepository.getTaskById(taskId).firstOrNull()?.let { task ->
                taskRepository.updateTask(
                    task.copy(
                        status = TaskStatus.DONE,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }
    
    fun logHabitCount(habitId: Long, count: Int) {
        viewModelScope.launch {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            habitRepository.insertHabitLog(
                HabitLogEntity(
                    habitId = habitId,
                    date = today,
                    count = count
                )
            )
        }
    }
}
