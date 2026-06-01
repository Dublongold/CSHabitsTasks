package com.habits.coooins.croowsss.ttask.ui.tasks.delete

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity
import com.habits.coooins.croowsss.ttask.ui.tasks.displayName
import org.kodein.di.compose.rememberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteTasksScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel by rememberViewModel<DeleteTasksViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delete Tasks") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.toggleSelectAll() },
                        enabled = uiState.tasks.isNotEmpty()
                    ) {
                        Text(
                            if (uiState.selectedTaskIds.size == uiState.tasks.size) "Deselect All" 
                            else "Select All"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.selectedTaskIds.isNotEmpty()) {
                Surface(
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp).padding(
                                BottomAppBarDefaults.windowInsets.asPaddingValues()
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${uiState.selectedTaskIds.size} task${
                                if (uiState.selectedTaskIds.size > 1) "s" else ""
                            } selected",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = { viewModel.showDeleteDialog() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No tasks to delete",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp + paddingValues.calculateStartPadding(layoutDirection),
                    top = 16.dp + paddingValues.calculateTopPadding(),
                    end = 16.dp + paddingValues.calculateEndPadding(layoutDirection),
                    bottom = 16.dp + paddingValues.calculateBottomPadding(),
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.tasks) { task ->
                    DeleteTaskItem(
                        task = task,
                        isSelected = task.id in uiState.selectedTaskIds,
                        onToggleSelection = { viewModel.toggleTaskSelection(task.id) }
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text("Delete Tasks") },
            text = { 
                Text("Are you sure you want to delete ${uiState.selectedTaskIds.size} task${
                    if (uiState.selectedTaskIds.size > 1) "s" else ""
                }? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteSelectedTasks() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DeleteTaskItem(
    task: TaskEntity,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggleSelection
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelection() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (task.notes.isNotEmpty()) {
                    Text(
                        task.notes,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        border = AssistChipDefaults.assistChipBorder(enabled = true),
                        colors = AssistChipDefaults.assistChipColors().let {
                            it.copy(
                                disabledContainerColor = it.containerColor,
                                disabledLabelColor = it.labelColor,
                                disabledTrailingIconContentColor = it.trailingIconContentColor,
                                disabledLeadingIconContentColor = it.leadingIconContentColor
                            )
                        },
                        label = {
                            Text(
                                task.taskPriority.displayName,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                    AssistChip(
                        onClick = {},
                        enabled = false,
                        border = AssistChipDefaults.assistChipBorder(enabled = true),
                        colors = AssistChipDefaults.assistChipColors().let {
                            it.copy(
                                disabledContainerColor = it.containerColor,
                                disabledLabelColor = it.labelColor,
                                disabledTrailingIconContentColor = it.trailingIconContentColor,
                                disabledLeadingIconContentColor = it.leadingIconContentColor
                            )
                        },
                        label = {
                            Text(
                                task.status.displayName,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }
    }
}
