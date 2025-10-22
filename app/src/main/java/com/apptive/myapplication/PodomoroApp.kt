package com.apptive.myapplication

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.apptive.myapplication.navigation.PodomoroDestination
import com.apptive.myapplication.navigation.PodomoroNavGraph

private data class BottomNavItem(
    val destination: PodomoroDestination,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(PodomoroDestination.Home, Icons.Filled.Home),
    BottomNavItem(PodomoroDestination.Timer, Icons.Filled.Info),
    BottomNavItem(PodomoroDestination.Stats, Icons.Filled.Info)
)

@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun PodomoroApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val appBarTitle = bottomNavItems
        .firstOrNull { item ->
            currentDestination?.hierarchy?.any { it.route == item.destination.route } == true
        }
        ?.destination
        ?.title
        ?: PodomoroDestination.Home.title

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = appBarTitle) }
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { destination -> destination.route == item.destination.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(item.destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.destination.title
                            )
                        },
                        label = { Text(item.destination.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        PodomoroNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
