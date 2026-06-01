package com.habits.coooins.croowsss.ttask.ui.habits.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitLogEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitScheduleType
import com.habits.coooins.croowsss.ttask.data.habit.HabitRepository
import org.kodein.di.compose.rememberInstance
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale
import com.habits.coooins.croowsss.ttask.ui.habits.displayName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: Long?,
    onNavigateBack: () -> Unit
) {
    val habitRepository by rememberInstance<HabitRepository>()
    val viewModel = viewModel {
        HabitDetailViewModel(
            habitRepository = habitRepository,
            habitId = habitId
        )
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var habitScheduleType by remember { mutableStateOf(HabitScheduleType.DAILY) }
    var targetCount by remember { mutableIntStateOf(1) }
    var active by remember { mutableStateOf(true) }
    var showResetDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.habit) {
        uiState.habit?.let { habit ->
            name = habit.name
            habitScheduleType = habit.habitScheduleType
            targetCount = habit.targetCount
            active = habit.active
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (habitId == null) "New Habit" else "Habit Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!uiState.isEditing && habitId != null) {
                        IconButton(onClick = { viewModel.toggleEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isEditing || habitId == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Schedule", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HabitScheduleType.entries.forEach { type ->
                        FilterChip(
                            selected = habitScheduleType == type,
                            onClick = { habitScheduleType = type },
                            label = { Text(type.displayName) }
                        )
                    }
                }
                
                OutlinedTextField(
                    value = targetCount.toString(),
                    onValueChange = { targetCount = it.toIntOrNull() ?: 1 },
                    label = { Text("Target Count") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Active")
                    Switch(checked = active, onCheckedChange = { active = it })
                }
                
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.saveHabit(name, habitScheduleType, targetCount, active)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Habit")
                }
            }
        } else {
            // View mode with logs
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    uiState.habit?.let { habit ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(habit.name, style = MaterialTheme.typography.titleLarge)
                                Text("Schedule: ${habit.habitScheduleType.displayName}")
                                Text("Target: ${habit.targetCount}")
                                Text("Status: ${if (habit.active) "Active" else "Inactive"}")
                            }
                        }
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("History", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { showResetDialog = true }) {
                            Text("Reset Data")
                        }
                    }
                }
                
                if (uiState.logs.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "No logs yet",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(uiState.logs) { log ->
                        LogItem(
                            log = log,
                            onDelete = { viewModel.deleteLog(log.id) }
                        )
                    }
                }
            }
        }
    }
    
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Habit Data?") },
            text = { Text("This will delete all logs for this habit. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetHabitData()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LogItem(
    log: HabitLogEntity,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", LocalLocale.current.platformLocale)
                Text(dateFormat.format(Date(log.date)), style = MaterialTheme.typography.titleMedium)
                Text("Count: ${log.count}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
