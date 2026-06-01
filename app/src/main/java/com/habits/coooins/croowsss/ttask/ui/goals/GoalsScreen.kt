package com.habits.coooins.croowsss.ttask.ui.goals

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
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalStatus
import org.kodein.di.compose.rememberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateToDetail: (Long?) -> Unit,
    onNavigateToDelete: () -> Unit,
) {
    val viewModel by rememberViewModel<GoalsViewModel>()
    
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goals") },
                actions = {
                    IconButton(onClick = onNavigateToDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Goals")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToDetail(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
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
        } else if (uiState.goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No goals yet")
                    TextButton(onClick = { onNavigateToDetail(null) }) {
                        Text("Create your first goal")
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
                items(uiState.goals) { goal ->
                    GoalCard(
                        goal = goal,
                        onClick = { onNavigateToDetail(goal.id) },
                        onIncrement = { viewModel.incrementProgress(goal.id, 1f) },
                        onDecrement = { viewModel.decrementProgress(goal.id, 1f) },
                        onArchive = { viewModel.archiveGoal(goal.id) }
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
fun GoalCard(
    goal: GoalEntity,
    onClick: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onArchive: () -> Unit
) {
    var showArchiveDialog by remember {
        mutableStateOf(false)
    }
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(goal.title, style = MaterialTheme.typography.titleMedium)
                    if (goal.description.isNotEmpty()) {
                        Text(
                            goal.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                AssistChip(
                    onClick = {
                        if (goal.status != GoalStatus.ARCHIVED) {
                            showArchiveDialog = true
                        }
                    },
                    label = { Text(goal.status.displayName) }
                )
            }
            
            LinearProgressIndicator(
                progress = { (goal.currentValue / goal.targetValue).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onDecrement) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease")
                    }
                    IconButton(onClick = onIncrement) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increase")
                    }
                }
            }
        }
    }
    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = {
                showArchiveDialog = false
            },
            title = {
                Text("Archive this goal?")
            }, text = {
                Text("Goal status is \"${goal.status.displayName}\".")
            }, confirmButton = {
                TextButton({
                    showArchiveDialog = false
                    onArchive()
                }) {
                    Text("Archive")
                }
            }, dismissButton = {
                TextButton({
                    showArchiveDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
