package com.habits.coooins.croowsss.ttask.ui.tasks.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.task.TaskPriority
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val task: TaskEntity? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false
)

class TaskDetailViewModel(
    private val taskRepository: TaskRepository,
    private val taskId: Long?
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            if (taskId != null) {
                taskRepository.getTaskById(taskId).collect { task ->
                    _uiState.value = TaskDetailUiState(
                        task = task,
                        isLoading = false,
                        isEditing = false
                    )
                }
            } else {
                _uiState.value = TaskDetailUiState(
                    task = null,
                    isLoading = false,
                    isEditing = true
                )
            }
        }
    }
    
    fun saveTask(
        title: String,
        notes: String,
        dueDateTime: Long?,
        taskPriority: TaskPriority,
        status: TaskStatus
    ) {
        viewModelScope.launch {
            if (taskId == null) {
                taskRepository.insertTask(
                    TaskEntity(
                        title = title,
                        notes = notes,
                        dueDateTime = dueDateTime,
                        taskPriority = taskPriority,
                        status = status
                    )
                ).let {
                    println(it)
                }
            } else {
                _uiState.value.task?.let { task ->
                    taskRepository.updateTask(
                        task.copy(
                            title = title,
                            notes = notes,
                            dueDateTime = dueDateTime,
                            taskPriority = taskPriority,
                            status = status,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }
    
    fun toggleEditing() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }
}
