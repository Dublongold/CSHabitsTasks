package com.habits.coooins.croowsss.ttask.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitEntity
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity
import org.kodein.di.compose.rememberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToTaskDetail: (Long?) -> Unit,
    onNavigateToFocusTimer: () -> Unit
) {
    val viewModel by rememberViewModel<HomeViewModel>()
    
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today") },
                actions = {
                    IconButton(onClick = onNavigateToFocusTimer) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Focus Timer")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Card
                    item {
                        SummaryCard(
                            taskCount = uiState.todayTasks.size,
                            habitCount = uiState.activeHabits.size,
                            goalCount = uiState.activeGoals.size,
                            onAddTask = { onNavigateToTaskDetail(null) },
                            onFocus = onNavigateToFocusTimer
                        )
                    }

                    // Today's Tasks Section
                    item {
                        SectionHeader(
                            title = "Today's Tasks",
                            actionText = if (uiState.todayTasks.isNotEmpty()) "View All" else null,
                            onActionClick = onNavigateToTasks
                        )
                    }

                    if (uiState.todayTasks.isEmpty()) {
                        item {
                            EmptyStateCard(
                                message = "No tasks for today",
                                actionText = "Add Task",
                                onActionClick = { onNavigateToTaskDetail(null) }
                            )
                        }
                    } else {
                        items(uiState.todayTasks.take(3)) { task ->
                            TaskItem(
                                task = task,
                                onToggleDone = { viewModel.markTaskDone(task.id) },
                                onClick = { onNavigateToTaskDetail(task.id) }
                            )
                        }
                        if (uiState.todayTasks.size > 3) {
                            item {
                                TextButton(
                                    onClick = onNavigateToTasks,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("View ${uiState.todayTasks.size - 3} more tasks")
                                }
                            }
                        }
                    }

                    // Habits Quick View
                    if (uiState.activeHabits.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Active Habits",
                                subtitle = "${uiState.activeHabits.size} active"
                            )
                        }

                        items(uiState.activeHabits.take(2)) { habit ->
                            HabitSummaryItem(
                                habit = habit,
                                onLogCount = { count -> viewModel.logHabitCount(habit.id, count) }
                            )
                        }
                    }

                    // Goals Quick View
                    if (uiState.activeGoals.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Goals in Progress",
                                subtitle = "${uiState.activeGoals.size} active"
                            )
                        }

                        items(uiState.activeGoals.take(2)) { goal ->
                            GoalProgressItem(goal = goal)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    taskCount: Int,
    habitCount: Int,
    goalCount: Int,
    onAddTask: () -> Unit,
    onFocus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Your Day at a Glance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Tasks", count = taskCount)
                StatItem(label = "Habits", count = habitCount)
                StatItem(label = "Goals", count = goalCount)
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = LocalContentColor.current)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddTask,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Task")
                }
                OutlinedButton(
                    onClick = onFocus,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(
                        width = ButtonDefaults.outlinedButtonBorder().width,
                        color = LocalContentColor.current
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Focus")
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleLarge)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (actionText != null && onActionClick != null) {
                TextButton(onClick = onActionClick) {
                    Text(actionText)
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onToggleDone: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = false,
                onCheckedChange = { onToggleDone() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                if (task.notes.isNotEmpty()) {
                    Text(
                        task.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                task.taskPriority.name,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun HabitSummaryItem(
    habit: HabitEntity,
    onLogCount: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "Target: ${habit.targetCount} per ${habit.habitScheduleType.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = { onLogCount(1) }) {
                Icon(Icons.Default.Add, contentDescription = "Log")
            }
        }
    }
}

@Composable
fun GoalProgressItem(goal: GoalEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(goal.title, style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(
                progress = { (goal.currentValue / goal.targetValue).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
