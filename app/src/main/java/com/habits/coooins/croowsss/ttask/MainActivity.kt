package com.habits.coooins.croowsss.ttask

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.habits.coooins.croowsss.ttask.ui.FocusLedgerApp
import com.habits.coooins.croowsss.ttask.ui.theme.CSHabitsTasksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            SystemBarStyle.dark(Color.TRANSPARENT),
            SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            CSHabitsTasksTheme {
                FocusLedgerApp()
            }
        }
    }
}

