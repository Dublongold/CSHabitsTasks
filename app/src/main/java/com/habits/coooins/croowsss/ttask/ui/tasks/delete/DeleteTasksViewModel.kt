package com.habits.coooins.croowsss.ttask.ui.tasks.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DeleteTasksUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val selectedTaskIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false
)

class DeleteTasksViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DeleteTasksUiState())
    val uiState: StateFlow<DeleteTasksUiState> = _uiState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            taskRepository.listenAllTasks().collect { tasks ->
                _uiState.update { state ->
                    state.copy(
                        tasks = tasks,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun toggleTaskSelection(taskId: Long) {
        _uiState.update { state ->
            val newSelection = if (taskId in state.selectedTaskIds) {
                state.selectedTaskIds - taskId
            } else {
                state.selectedTaskIds + taskId
            }
            state.copy(selectedTaskIds = newSelection)
        }
    }
    
    fun toggleSelectAll() {
        _uiState.update { state ->
            val newSelection = if (state.selectedTaskIds.size == state.tasks.size) {
                emptySet()
            } else {
                state.tasks.map { it.id }.toSet()
            }
            state.copy(selectedTaskIds = newSelection)
        }
    }
    
    fun showDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }
    
    fun hideDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }
    
    fun deleteSelectedTasks() {
        viewModelScope.launch {
            val taskIds = _uiState.value.selectedTaskIds
            taskIds.forEach { taskId ->
                taskRepository.getTaskById(taskId).firstOrNull()?.let { task ->
                    taskRepository.deleteTask(task)
                }
            }
            _uiState.update { state ->
                state.copy(
                    selectedTaskIds = emptySet(),
                    showDeleteDialog = false
                )
            }
        }
    }
}
