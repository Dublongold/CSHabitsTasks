package com.habits.coooins.croowsss.ttask.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    companion object {
        val items = listOf(Home, Tasks, Habits, Goals, More)
    }
    object Home : BottomNavItem(Screen.Home.route, Icons.Default.Home, "Today")
    object Tasks : BottomNavItem(Screen.Tasks.route, Icons.Default.CheckCircle, "Tasks")
    object Habits : BottomNavItem(Screen.Habits.route, Icons.Default.DateRange, "Habits")
    object Goals : BottomNavItem(Screen.Goals.route, Icons.Default.Star, "Goals")
    object More : BottomNavItem(Screen.More.route, Icons.Default.Menu, "More")
}