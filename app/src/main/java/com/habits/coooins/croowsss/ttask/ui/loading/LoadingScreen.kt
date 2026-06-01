package com.habits.coooins.croowsss.ttask.ui.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.habits.coooins.croowsss.ttask.ui.theme.CSHabitsTasksTheme

@Composable
fun LoadingScreen() {
    Scaffold {
        Box(Modifier
            .fillMaxSize()
            .padding(it)
            .padding(20.dp)) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Loading…", style = MaterialTheme.typography.titleMedium)
                CircularProgressIndicator()
            }
            LinearProgressIndicator(Modifier.align(Alignment.BottomCenter).fillMaxWidth())
            LinearProgressIndicator(Modifier.align(Alignment.TopCenter).fillMaxWidth())
        }
    }
}

@Preview
@Composable
private fun LoadingScreenPreview() {
    CSHabitsTasksTheme {
        LoadingScreen()
    }
}