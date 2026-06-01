package com.habits.coooins.croowsss.ttask.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habits.coooins.croowsss.ttask.data.task.TaskPriority
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TasksUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val isLoading: Boolean = true
)

data class TasksFilterState(
    val status: TaskStatus? = null,
    val taskPriority: TaskPriority? = null,
    val searchQuery: String = "",
)

class TasksViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private val _filterState = MutableStateFlow(TasksFilterState())
    val filterState = _filterState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            combine(
                taskRepository.listenAllTasks(),
                filterState
            ) { tasks, filterState ->
                val filteredTasks = filterTasks(
                    tasks,
                    filterState.status,
                    filterState.taskPriority,
                    filterState.searchQuery
                )
                _uiState.value.copy(
                    tasks = filteredTasks,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    private fun filterTasks(
        tasks: List<TaskEntity>,
        status: TaskStatus?,
        taskPriority: TaskPriority?,
        query: String
    ): List<TaskEntity> {
        return tasks.filter { task ->
            (status == null || task.status == status) &&
            (taskPriority == null || task.taskPriority == taskPriority) &&
            (query.isEmpty() || task.title.contains(query, ignoreCase = true))
        }
    }
    
    fun setFilterStatus(status: TaskStatus?) {
        _filterState.update { it.copy(status = status) }
    }
    
    fun setFilterPriority(taskPriority: TaskPriority?) {
        _filterState.update { it.copy(taskPriority = taskPriority) }
    }
    fun setSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }
    
    fun markTasksDone(taskIds: List<Long>) {
        viewModelScope.launch {
            taskIds.forEach { taskId ->
                taskRepository.getTaskById(taskId).firstOrNull()?.let { task ->
                    taskRepository.updateTask(
                        task.copy(
                            status = if (task.status == TaskStatus.DONE) {
                                TaskStatus.OPEN
                            } else {
                                TaskStatus.DONE
                            },
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }
}
