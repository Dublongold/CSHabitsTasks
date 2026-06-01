package com.habits.coooins.croowsss.ttask.ui.habits

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
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitLogEntity
import org.kodein.di.compose.rememberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    onNavigateToDetail: (Long?) -> Unit,
    onNavigateToDelete: () -> Unit,
) {
    val viewModel by rememberViewModel<HabitsViewModel>()
    
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habits") },
                actions = {
                    IconButton(onClick = onNavigateToDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Habits")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToDetail(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.habits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No habits yet")
                    TextButton(onClick = { onNavigateToDetail(null) }) {
                        Text("Create your first habit")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.habits) { habit ->
                    HabitCard(
                        habit = habit,
                        logs = uiState.habitLogs[habit.id] ?: emptyList(),
                        onClick = { onNavigateToDetail(habit.id) },
                        onToggleActive = { viewModel.toggleHabitActive(habit.id) },
                        onLogToday = { count -> viewModel.logHabitToday(habit.id, count) }
                    )
                }
                item {
                    Spacer(Modifier.size(64.0.dp))
                }
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: HabitEntity,
    logs: List<HabitLogEntity>,
    onClick: () -> Unit,
    onToggleActive: () -> Unit,
    onLogToday: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(habit.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${habit.habitScheduleType.displayName} • Target: ${habit.targetCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = habit.active,
                    onCheckedChange = { onToggleActive() }
                )
            }
            
            // Streak info
            val recentLogs = logs.take(7)
            if (recentLogs.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    recentLogs.forEach { log ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(2.dp)
                        ) {
                            if (log.count >= habit.targetCount) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onLogToday(1) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Log Today")
                }
                Button(
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Details")
                }
            }
        }
    }
}
