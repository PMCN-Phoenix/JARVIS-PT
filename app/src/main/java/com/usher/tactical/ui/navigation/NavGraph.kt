package com.usher.tactical.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.usher.tactical.ui.attribute.AttributeScreen
import com.usher.tactical.ui.backpack.BackpackScreen
import com.usher.tactical.ui.dashboard.DashboardScreen
import com.usher.tactical.ui.log.SystemLogScreen
import com.usher.tactical.ui.task.TaskCenterScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val ATTRIBUTE = "attribute"
    const val TASK = "task"
    const val BACKPACK = "backpack"
    const val LOG = "log"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.DASHBOARD, "仪表盘", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    BottomNavItem(Routes.ATTRIBUTE, "属性", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    BottomNavItem(Routes.TASK, "任务", Icons.AutoMirrored.Filled.ListAlt, Icons.AutoMirrored.Outlined.ListAlt),
    BottomNavItem(Routes.BACKPACK, "背包", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
    BottomNavItem(Routes.LOG, "日志", Icons.Filled.Description, Icons.Outlined.Description)
)

@Composable
fun NavGraph(navController: NavHostController) {
    Scaffold(
        bottomBar = { TacticalBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) { DashboardScreen(navController = navController) }
            composable(Routes.ATTRIBUTE) { AttributeScreen() }
            composable(Routes.TASK) { TaskCenterScreen() }
            composable(Routes.BACKPACK) { BackpackScreen() }
            composable(Routes.LOG) { SystemLogScreen() }
        }
    }
}

@Composable
fun TacticalBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = com.usher.tactical.ui.theme.BgPrimary) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
