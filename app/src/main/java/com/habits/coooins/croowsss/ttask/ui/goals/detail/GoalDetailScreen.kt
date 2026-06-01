package com.habits.coooins.croowsss.ttask.ui.goals.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalStatus
import com.habits.coooins.croowsss.ttask.data.goal.GoalRepository
import com.habits.coooins.croowsss.ttask.ui.goals.displayName
import org.kodein.di.compose.rememberInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: Long?,
    onNavigateBack: () -> Unit
) {
    val goalRepository by rememberInstance<GoalRepository>()
    val viewModel = viewModel {
        GoalDetailViewModel(
            goalRepository = goalRepository,
            goalId = goalId
        )
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var targetValue by remember { mutableStateOf("") }
    var currentValue by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(GoalStatus.IN_PROGRESS) }
    
    LaunchedEffect(uiState.goal) {
        uiState.goal?.let { goal ->
            title = goal.title
            description = goal.description
            targetValue = goal.targetValue.toInt().toString()
            currentValue = goal.currentValue.toInt().toString()
            unit = goal.unit
            status = goal.status
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (goalId == null) "New Goal" else "Goal Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!uiState.isEditing && goalId != null) {
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
            if (uiState.isEditing || goalId == null) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetValue,
                        onValueChange = { targetValue = it },
                        label = { Text("Target *") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    OutlinedTextField(
                        value = currentValue,
                        onValueChange = { currentValue = it },
                        label = { Text("Current") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (e.g., kg, hours)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Status", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GoalStatus.entries.forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.displayName) }
                        )
                    }
                }
                
                Button(
                    onClick = {
                        if (title.isNotBlank() && targetValue.isNotBlank()) {
                            viewModel.saveGoal(
                                title = title,
                                description = description,
                                targetValue = targetValue.toFloatOrNull() ?: 0f,
                                currentValue = currentValue.toFloatOrNull() ?: 0f,
                                unit = unit,
                                status = status
                            )
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Goal")
                }
            } else {
                // View mode
                uiState.goal?.let { goal ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(goal.title, style = MaterialTheme.typography.titleLarge)
                            val dividerColor = LocalContentColor.current.copy(.5f)
                            if (goal.description.isNotEmpty()) {
                                HorizontalDivider(color = dividerColor)
                                Text("Description", style = MaterialTheme.typography.labelMedium)
                                Text(goal.description, style = MaterialTheme.typography.bodyMedium)
                            }

                            HorizontalDivider(color = dividerColor)
                            Text("Progress", style = MaterialTheme.typography.labelMedium)
                            
                            LinearProgressIndicator(
                                progress = { (goal.currentValue / goal.targetValue).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Text(
                                "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            HorizontalDivider(color = dividerColor)
                            Text("Status: ${goal.status.displayName}")
                            
                            // Progress adjustment
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                            ) {
                                var adjustValue by remember { mutableStateOf("1") }
                                
                                OutlinedTextField(
                                    value = adjustValue,
                                    onValueChange = { adjustValue = it },
                                    label = { Text("Amount") },
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Button(
                                    onClick = {
                                        adjustValue.toFloatOrNull()?.let { value ->
                                            viewModel.updateProgress(goal.currentValue + value)
                                        }
                                    }
                                ) {
                                    Text("Update")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
