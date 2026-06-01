package com.habits.coooins.croowsss.ttask.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habits.coooins.croowsss.ttask.data.task.TaskInfo
import org.kodein.di.compose.rememberViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerScreen(
    onNavigateToHistory: () -> Unit, onNavigateBack: () -> Unit
) {
    val viewModel by rememberViewModel<FocusTimerViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Focus Timer") }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            }, actions = {
                IconButton(onClick = onNavigateToHistory) {
                    Icon(Icons.Default.DateRange, contentDescription = "History")
                }
            })
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = formatTime(uiState.lastSession.elapsedSeconds),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.displayLarge
                    )

                    if (uiState.isPaused) {
                        Text(
                            "Paused",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (!uiState.isRunning) {
                        TaskSelectionDropdownMenu(
                            uiState.taskId, uiState.tasksToFocus, onSelect = viewModel::updateTaskId
                        )

                        OutlinedTextField(
                            value = uiState.lastSession.label ?: "",
                            onValueChange = viewModel::updateLabel,
                            label = { Text("Session Label (optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = viewModel::startSession, modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${if (uiState.readyToContinue) " Continue " else " Start "} Focus Session"
                            )
                        }
                    } else {
                        val label = uiState.lastSession.label
                        val taskId = uiState.taskId
                        if (label != null || taskId != null) {
                            Column {
                                if (label != null) {
                                    Text("Label: $label")
                                }
                                if (taskId != null) {
                                    val taskToFocus = uiState.tasksToFocus.firstOrNull {
                                        it.id == taskId
                                    }
                                    val taskString = buildAnnotatedString {
                                        val startText = "Task:"
                                        append(startText)
                                        append(taskToFocus?.title ?: "Invalid selected task…")
                                        addStyle(
                                            SpanStyle(
                                                fontStyle = if (taskToFocus == null) {
                                                    FontStyle.Italic
                                                } else FontStyle.Normal
                                            ), start = startText.length, end = length
                                        )
                                    }
                                    Text(taskString)
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (uiState.isPaused) {
                                Button(
                                    onClick = { viewModel.resumeSession() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Resume")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.pauseSession() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pause")
                                }
                            }

                            Button(
                                onClick = viewModel::stopSession,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Stop")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("About Focus Timer", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "This timer only works while the app is open. " + "There are no background notifications or alarms.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskSelectionDropdownMenu(
    currentId: Long?,
    values: List<TaskInfo>,
    onSelect: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        val selectedTask = values.firstOrNull { it.id == currentId }?.title
        OutlinedTextField(
            value = when {
            values.isEmpty() -> "No tasks available…"
            currentId == null -> "Select a task to focus on…"
            else -> selectedTask ?: "Invalid task selected…"
        },
            textStyle = LocalTextStyle.current.copy(
                fontStyle = if (selectedTask == null) FontStyle.Italic else FontStyle.Normal,
            ),
            onValueChange = { },
            label = { Text("Task") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            if (values.isEmpty()) {
                Text(
                    "No tasks available. Create a task and it will appear here.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                DropdownMenuItem(
                    text = { Text("No task…", fontStyle = FontStyle.Italic) },
                    onClick = {
                        onSelect(null)
                        expanded = false
                    },
                    trailingIcon = if (currentId == null) {
                        ::ActiveTaskTrailingIcon
                    } else null
                )
                values.forEach { task ->
                    DropdownMenuItem(
                        text = { Text(task.title) }, onClick = {
                        onSelect(task.id)
                        expanded = false
                    }, trailingIcon = if (task.id == currentId) {
                        ::ActiveTaskTrailingIcon
                    } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveTaskTrailingIcon() {
    Icon(
        Icons.Default.Check,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
    )
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, secs)
}
