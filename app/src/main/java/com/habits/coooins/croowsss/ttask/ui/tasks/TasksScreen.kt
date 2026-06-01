package com.habits.coooins.croowsss.ttask.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.habits.coooins.croowsss.ttask.data.task.TaskPriority
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus
import org.kodein.di.compose.rememberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    onNavigateToDetail: (Long?) -> Unit,
    onNavigateToDelete: () -> Unit,
) {
    val viewModel by rememberViewModel<TasksViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tasks") }, actions = {
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        if (showFilters) Icons.Default.SearchOff else Icons.Default.Search,
                        contentDescription = "Filter"
                    )
                }
                IconButton(onClick = onNavigateToDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Tasks"
                    )
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToDetail(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection)
                )
        ) {
            if (showFilters) {
                FiltersSection(
                    filterState = filterState,
                    onFilterStatus = viewModel::setFilterStatus,
                    onFilterPriority = viewModel::setFilterPriority,
                    onSearchQueryChange = viewModel::setSearchQuery
                )
                HorizontalDivider()
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .padding(vertical = 16.dp), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .padding(vertical = 16.dp), contentAlignment = Alignment.Center
                ) {
                    Text("No tasks found")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp + paddingValues.calculateBottomPadding(),
                    ), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.tasks) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onNavigateToDetail(task.id) },
                            onToggleDone = {
                                viewModel.markTasksDone(listOf(task.id))
                            })
                    }
                    item {
                        Spacer(Modifier.size(64.0.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FiltersSection(
    filterState: TasksFilterState,
    onFilterStatus: (TaskStatus?) -> Unit,
    onFilterPriority: (TaskPriority?) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Filters", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = filterState.searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) })

            // Status filter
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterState.status == null,
                    onClick = { onFilterStatus(null) },
                    label = { Text("All") })
                TaskStatus.entries.forEach { status ->
                    FilterChip(
                        selected = filterState.status == status,
                        onClick = { onFilterStatus(status) },
                        label = { Text(status.displayName) })
                }
            }

            // TaskPriority filter
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterState.taskPriority == null,
                    onClick = { onFilterPriority(null) },
                    label = { Text("All TaskPriority") })
                TaskPriority.entries.forEach { priority ->
                    FilterChip(
                        selected = filterState.taskPriority == priority,
                        onClick = { onFilterPriority(priority) },
                        label = { Text(priority.displayName) })
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskEntity, onClick: () -> Unit, onToggleDone: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(), onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.status == TaskStatus.DONE, onCheckedChange = { onToggleDone() })
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                if (task.notes.isNotEmpty()) {
                    Text(
                        task.notes, style = MaterialTheme.typography.bodySmall, maxLines = 2
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    AssistChip(onClick = {}, label = {
                        Text(
                            task.taskPriority.displayName, style = MaterialTheme.typography.labelSmall
                        )
                    })
                }
            }
        }
    }
}
