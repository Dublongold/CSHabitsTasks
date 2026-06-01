package com.habits.coooins.croowsss.ttask.ui.session.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.unit.dp
import com.habits.coooins.croowsss.ttask.data.focussession.local.FocusSessionEntity
import org.kodein.di.compose.rememberViewModel
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionHistoryScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel by rememberViewModel<SessionHistoryViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Session History") }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                )
        ) {
            // Summary card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Summary", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Total Focus Time: ${uiState.totalMinutes} minutes",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "Sessions: ${uiState.sessions.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
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
            } else if (uiState.sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .padding(vertical = 16.dp), contentAlignment = Alignment.Center
                ) {
                    Text("No focus sessions yet")
                }
            } else {
                HorizontalDivider(Modifier.padding(top = 16.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp + paddingValues.calculateBottomPadding()
                    ), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.sessions) { session ->
                        SessionCard(
                            session = session,
                            onDelete = { viewModel.deleteSession(session.id) },
                            onEditLabel = { label ->
                                viewModel.updateSessionLabel(session.id, label)
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun SessionCard(
    session: FocusSessionEntity, onDelete: () -> Unit, onEditLabel: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        session.label ?: "Focus Session",
                        style = MaterialTheme.typography.titleMedium
                    )
                    val dateFormat =
                        SimpleDateFormat("MMM dd, yyyy HH:mm", LocalLocale.current.platformLocale)
                    Text(
                        dateFormat.format(Date(session.startAt)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }

            val duration = if (session.endAt != null) {
                (session.endAt - session.startAt) / 1000
            } else 0L
            fun endWithS(value: Long) = if (value.absoluteValue == 1L) "" else "s"
            fun addIfNotZero(value: Long, unit: String) =
                if (value > 0) "$value $unit${endWithS(value)}" else null
            val data = listOfNotNull(
                addIfNotZero(duration / (60 * 60), "hour"),
                addIfNotZero(duration / 60 % 60, "minute"),
                addIfNotZero(duration % 60, "second")
            ).joinToString(", ")
            Text(
                "Duration: $data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (session.taskTitle != null) {
                Text(
                    "Task: ${session.taskTitle}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }

    if (showEditDialog) {
        var newLabel by remember { mutableStateOf(session.label ?: "") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Session Label") },
            text = {
                OutlinedTextField(
                    value = newLabel,
                    onValueChange = { newLabel = it },
                    label = { Text("Label") })
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEditLabel(newLabel)
                        showEditDialog = false
                    }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            })
    }
}
