package com.habits.coooins.croowsss.ttask.ui.insights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.kodein.di.compose.rememberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(onNavigateBack: () -> Unit) {
    val viewModel by rememberViewModel<InsightsViewModel>()
    
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights") },
                navigationIcon = {
                    IconButton(onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Time window selector
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Time Period", style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TimeWindow.entries.forEach { window ->
                                FilterChip(
                                    selected = uiState.timeWindow == window,
                                    onClick = { viewModel.setTimeWindow(window) },
                                    label = { Text(window.displayName) }
                                )
                            }
                        }
                    }
                }

                // Tasks Insights
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tasks", style = MaterialTheme.typography.titleLarge)
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                        }

                        StatRow("Total Tasks", uiState.data.totalTasks.toString())
                        StatRow("Completed", uiState.data.completedTasks.toString())

                        HorizontalDivider()

                        Text("Completion Rate", style = MaterialTheme.typography.titleMedium)
                        LinearProgressIndicator(
                            progress = { uiState.data.completionRate },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "${(uiState.data.completionRate * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Habits Insights
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Habits", style = MaterialTheme.typography.titleLarge)
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }

                        StatRow("Active Habits", uiState.data.activeHabits.toString())

                        HorizontalDivider()

                        Text("Consistency Score", style = MaterialTheme.typography.titleMedium)
                        LinearProgressIndicator(
                            progress = { uiState.data.habitConsistency },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "${(uiState.data.habitConsistency * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Focus Time Insights
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Focus Time", style = MaterialTheme.typography.titleLarge)
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                        }

                        val hours = uiState.data.totalFocusMinutes / 60
                        val minutes = uiState.data.totalFocusMinutes % 60

                        Text(
                            "$hours hours $minutes minutes",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            "Total focused time in selected period",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Goals Progress
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Goals", style = MaterialTheme.typography.titleLarge)
                            Icon(Icons.Default.Star, contentDescription = null)
                        }

                        Text("Average Progress", style = MaterialTheme.typography.titleMedium)
                        LinearProgressIndicator(
                            progress = { uiState.data.goalsProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "${(uiState.data.goalsProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
