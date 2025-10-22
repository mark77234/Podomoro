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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.apptive.myapplication.ui.home.HomeTab
import com.apptive.myapplication.ui.stats.StatsTab
import com.apptive.myapplication.ui.timer.TimerTab

enum class PodomoroTab(val label: String) {
    Home("홈"),
    Timer("타이머"),
    Stats("통계")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodomoroApp() {
    var selectedTab by rememberSaveable { mutableStateOf(PodomoroTab.Home) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = selectedTab.label) }
            )
        },
        bottomBar = {
            NavigationBar {
                PodomoroTab.entries.forEach { tab ->
                    val selected = tab == selectedTab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab },
                        icon = {
                            when (tab) {
                                PodomoroTab.Home -> Icon(
                                    Icons.Filled.Home,
                                    contentDescription = tab.label
                                )

                                PodomoroTab.Timer -> Icon(
                                    Icons.Filled.Info,
                                    contentDescription = tab.label
                                )

                                PodomoroTab.Stats -> Icon(
                                    Icons.Filled.Info,
                                    contentDescription = tab.label
                                )
                            }
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            PodomoroTab.Home -> HomeTab(modifier = Modifier.padding(innerPadding))
            PodomoroTab.Timer -> TimerTab(modifier = Modifier.padding(innerPadding))
            PodomoroTab.Stats -> StatsTab(modifier = Modifier.padding(innerPadding))
        }
    }
}
