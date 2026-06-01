package com.habits.coooins.croowsss.ttask.ui.tasks.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habits.coooins.croowsss.ttask.data.task.TaskPriority
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus
import com.habits.coooins.croowsss.ttask.data.task.TaskRepository
import com.habits.coooins.croowsss.ttask.ui.tasks.displayName
import org.kodein.di.compose.rememberInstance
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long?,
    onNavigateBack: () -> Unit
) {
    val taskRepository by rememberInstance<TaskRepository>()

    val viewModel = viewModel(key = taskId?.toString()) {
        TaskDetailViewModel(
            taskRepository = taskRepository,
            taskId = taskId
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var status by remember { mutableStateOf(TaskStatus.OPEN) }

    LaunchedEffect(uiState.task) {
        uiState.task?.let { task ->
            title = task.title
            notes = task.notes
            taskPriority = task.taskPriority
            status = task.status
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "New Task" else "Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!uiState.isEditing && taskId != null) {
                        IconButton(onClick = { viewModel.toggleEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isEditing || taskId == null) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Text("TaskPriority", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskPriority.entries.forEach { p ->
                        FilterChip(
                            selected = taskPriority == p,
                            onClick = { taskPriority = p },
                            label = { Text(p.displayName) }
                        )
                    }
                }

                Text("Status", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskStatus.entries.forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.displayName) }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.saveTask(
                                title = title,
                                notes = notes,
                                dueDateTime = null,
                                taskPriority = taskPriority,
                                status = status
                            )
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Task")
                }
            } else {
                // View mode
                uiState.task?.let { task ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Title", style = MaterialTheme.typography.labelMedium)
                            Text(task.title, style = MaterialTheme.typography.titleLarge)

                            if (task.notes.isNotEmpty()) {
                                HorizontalDivider()
                                Text("Notes", style = MaterialTheme.typography.labelMedium)
                                Text(task.notes, style = MaterialTheme.typography.bodyMedium)
                            }

                            HorizontalDivider()
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Column {
                                    Text("TaskPriority", style = MaterialTheme.typography.labelMedium)
                                    Text(task.taskPriority.displayName)
                                }
                                Column {
                                    Text("Status", style = MaterialTheme.typography.labelMedium)
                                    Text(task.status.displayName)
                                }
                            }

                            HorizontalDivider()
                            val dateFormat = SimpleDateFormat(
                                "MMM dd, yyyy HH:mm",
                                LocalLocale.current.platformLocale
                            )
                            Text(
                                "Created: ${dateFormat.format(Date(task.createdAt))}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Updated: ${dateFormat.format(Date(task.updatedAt))}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
