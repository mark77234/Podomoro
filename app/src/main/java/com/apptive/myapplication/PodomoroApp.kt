package com.apptive.myapplication

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.apptive.myapplication.model.Habit
import com.apptive.myapplication.ui.home.HomeTab
import com.apptive.myapplication.ui.stats.StatsTab
import com.apptive.myapplication.ui.timer.TimerTab
import java.time.LocalDate

enum class PodomoroTab(val label: String) {
    Home("홈"),
    Timer("타이머"),
    Stats("통계")
}

@Composable
fun PodomoroApp() {
    var selectedTab by rememberSaveable { mutableStateOf(PodomoroTab.Home) }
    val habits = remember { mutableStateListOf<Habit>() }
    var nextHabitId by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                PodomoroTab.entries.forEach { tab ->
                    val selected = tab == selectedTab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab },
                        icon = {
                            when (tab) {
                                PodomoroTab.Home -> Icon(Icons.Filled.Home, contentDescription = tab.label)
                                PodomoroTab.Timer -> Icon(Icons.Filled.Info, contentDescription = tab.label)
                                PodomoroTab.Stats -> Icon(Icons.Filled.Info, contentDescription = tab.label)
                            }
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            PodomoroTab.Home -> HomeTab(
                habits = habits,
                onAddHabit = { name ->
                    val trimmed = name.trim()
                    if (trimmed.isNotEmpty()) {
                        habits.add(Habit(id = nextHabitId++, name = trimmed))
                    }
                },
                onToggleHabit = { habit, completed ->
                    val today = LocalDate.now()
                    if (completed) {
                        if (!habit.isCompletedOn(today)) {
                            habit.completionDates.add(today)
                        }
                    } else {
                        habit.completionDates.remove(today)
                    }
                },
                onRemoveHabit = { habit -> habits.remove(habit) },
                modifier = Modifier.padding(innerPadding)
            )

            PodomoroTab.Timer -> TimerTab(modifier = Modifier.padding(innerPadding))
            PodomoroTab.Stats -> StatsTab(habits = habits, modifier = Modifier.padding(innerPadding))
        }
    }
}
