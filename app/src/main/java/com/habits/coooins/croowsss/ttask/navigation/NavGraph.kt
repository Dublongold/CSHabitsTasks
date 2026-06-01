package com.habits.coooins.croowsss.ttask.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.habits.coooins.croowsss.ttask.ui.session.FocusTimerScreen
import com.habits.coooins.croowsss.ttask.ui.session.history.SessionHistoryScreen
import com.habits.coooins.croowsss.ttask.ui.goals.delete.DeleteGoalsScreen
import com.habits.coooins.croowsss.ttask.ui.goals.detail.GoalDetailScreen
import com.habits.coooins.croowsss.ttask.ui.goals.GoalsScreen
import com.habits.coooins.croowsss.ttask.ui.habits.delete.DeleteHabitsScreen
import com.habits.coooins.croowsss.ttask.ui.habits.detail.HabitDetailScreen
import com.habits.coooins.croowsss.ttask.ui.habits.HabitsScreen
import com.habits.coooins.croowsss.ttask.ui.home.HomeScreen
import com.habits.coooins.croowsss.ttask.ui.insights.InsightsScreen
import com.habits.coooins.croowsss.ttask.ui.more.MoreScreen
import com.habits.coooins.croowsss.ttask.ui.settings.SettingsScreen
import com.habits.coooins.croowsss.ttask.ui.tasks.delete.DeleteTasksScreen
import com.habits.coooins.croowsss.ttask.ui.tasks.detail.TaskDetailScreen
import com.habits.coooins.croowsss.ttask.ui.tasks.TasksScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Tasks : Screen("tasks")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Long?) = "task_detail/${taskId ?: "new"}"
    }
    object DeleteTasks : Screen("delete_tasks")
    object Habits : Screen("habits")
    object HabitDetail : Screen("habit_detail/{habitId}") {
        fun createRoute(habitId: Long?) = "habit_detail/${habitId ?: "new"}"
    }
    object DeleteHabits : Screen("delete_habits")
    object Goals : Screen("goals")
    object GoalDetail : Screen("goal_detail/{goalId}") {
        fun createRoute(goalId: Long?) = "goal_detail/${goalId ?: "new"}"
    }
    object DeleteGoals : Screen("delete_goals")
    object More : Screen("more")
    object FocusTimer : Screen("focus_timer")
    object SessionHistory : Screen("session_history")
    object Insights : Screen("insights")
    object Settings : Screen("settings")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onNavigateToFocusTimer = { navController.navigate(Screen.FocusTimer.route) }
            )
        }
        
        composable(Screen.Tasks.route) {
            TasksScreen(
                onNavigateToDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onNavigateToDelete = { navController.navigate(Screen.DeleteTasks.route) },
            )
        }
        
        composable(Screen.DeleteTasks.route) {
            DeleteTasksScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(
                navArgument("taskId") { 
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val taskIdStr = backStackEntry.arguments?.getString("taskId")
            val taskId = if (taskIdStr == "new") null else taskIdStr?.toLongOrNull()
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Habits.route) {
            HabitsScreen(
                onNavigateToDetail = { habitId ->
                    navController.navigate(Screen.HabitDetail.createRoute(habitId))
                },
                onNavigateToDelete = { navController.navigate(Screen.DeleteHabits.route) },
            )
        }
        
        composable(Screen.DeleteHabits.route) {
            DeleteHabitsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.HabitDetail.route,
            arguments = listOf(
                navArgument("habitId") { 
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val habitIdStr = backStackEntry.arguments?.getString("habitId")
            val habitId = if (habitIdStr == "new") null else habitIdStr?.toLongOrNull()
            HabitDetailScreen(
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Goals.route) {
            GoalsScreen(
                onNavigateToDetail = { goalId ->
                    navController.navigate(Screen.GoalDetail.createRoute(goalId))
                },
                onNavigateToDelete = { navController.navigate(Screen.DeleteGoals.route) },
            )
        }
        
        composable(Screen.DeleteGoals.route) {
            DeleteGoalsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.GoalDetail.route,
            arguments = listOf(
                navArgument("goalId") { 
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val goalIdStr = backStackEntry.arguments?.getString("goalId")
            val goalId = if (goalIdStr == "new") null else goalIdStr?.toLongOrNull()
            GoalDetailScreen(
                goalId = goalId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.FocusTimer.route) {
            FocusTimerScreen(
                onNavigateToHistory = { navController.navigate(Screen.SessionHistory.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.SessionHistory.route) {
            SessionHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.More.route) {
            MoreScreen(
                onNavigateToFocusTimer = { navController.navigate(Screen.FocusTimer.route) },
                onNavigateToSessionHistory = { navController.navigate(Screen.SessionHistory.route) },
                onNavigateToInsights = { navController.navigate(Screen.Insights.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.Insights.route) {
            InsightsScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
