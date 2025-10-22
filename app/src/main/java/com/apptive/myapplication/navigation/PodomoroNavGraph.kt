package com.apptive.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.apptive.myapplication.ui.home.HomeTab
import com.apptive.myapplication.ui.stats.StatsTab
import com.apptive.myapplication.ui.timer.TimerTab

enum class PodomoroDestination(val route: String, val title: String) {
    Home("home", "홈"),
    Timer("timer", "타이머"),
    Stats("stats", "통계")
}

@Composable
fun PodomoroNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = PodomoroDestination.Home.route,
        modifier = modifier
    ) {
        composable(route = PodomoroDestination.Home.route) {
            HomeTab()
        }
        composable(route = PodomoroDestination.Timer.route) {
            TimerTab()
        }
        composable(route = PodomoroDestination.Stats.route) {
            StatsTab()
        }
    }
}
