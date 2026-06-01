package com.habits.coooins.croowsss.ttask.ui

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habits.coooins.croowsss.ttask.data.fake.SampleDataGenerator
import com.habits.coooins.croowsss.ttask.navigation.AppNavGraph
import com.habits.coooins.croowsss.ttask.navigation.Screen
import com.habits.coooins.croowsss.ttask.navigation.BottomNavItem
import kotlinx.coroutines.launch
import org.kodein.di.compose.rememberInstance

@Composable
fun FocusLedgerApp() {
    val navController = rememberNavController()

    // Only show bottom nav on main screens
    val mainRoutes = listOf(
        Screen.Home.route,
        Screen.Tasks.route,
        Screen.Habits.route,
        Screen.Goals.route,
        Screen.More.route
    )
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        Log.d("FocusLedgerApp", "currentRoute=$currentRoute")
    }
    NavigationSuiteScaffold(
        modifier = Modifier.fillMaxSize(), navigationSuiteItems = {
            for (item in BottomNavItem.items) {
                item(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            if (item.route == Screen.Home.route) {
                                navController.popBackStack(Screen.Home.route, inclusive = false)
                            } else {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    })
            }
        }) {
        Scaffold { paddingValues ->
            val asdaf by animateDpAsState(
                if (currentRoute in mainRoutes) {
                    paddingValues.calculateBottomPadding()
                } else 0.dp, animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = Dp.VisibilityThreshold
                )
            )
            val qwePV = PaddingValues(
                bottom = asdaf,
            )
            AppNavGraph(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier
                    .padding(qwePV)
                    .consumeWindowInsets(qwePV)
            )
        }
    }
}
