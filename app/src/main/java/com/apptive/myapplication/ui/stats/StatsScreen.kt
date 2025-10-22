package com.apptive.myapplication.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apptive.myapplication.model.Habit
import com.apptive.myapplication.ui.components.EmptyStatsState
import com.apptive.myapplication.ui.components.HabitStatsCard
import com.apptive.myapplication.ui.components.SummaryCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun StatsTab(
    habits: List<Habit>,
    modifier: Modifier = Modifier
) {
    if (habits.isEmpty()) {
        EmptyStatsState(modifier = modifier)
        return
    }

    val today = LocalDate.now()
    val weekStart = today.minusDays(6)
    val totalHabits = habits.size
    val completedToday = habits.count { it.isCompletedOn(today) }
    val totalCompletions = habits.sumOf { it.completionDates.size }
    val weeklyCompletions = habits.sumOf { habit ->
        habit.completionDates.count { date ->
            !date.isBefore(weekStart) && !date.isAfter(today)
        }
    }
    val completionRatePercent = if (totalHabits > 0) {
        ((completedToday.toDouble() / totalHabits) * 100).roundToInt()
    } else {
        0
    }

    val bestHabitLabel = habits
        .filter { it.completionDates.isNotEmpty() }
        .maxByOrNull { it.completionDates.size }
        ?.let { habit -> "${habit.name} (${habit.completionDates.size}회)" }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryCard(
                totalHabits = totalHabits,
                completedToday = completedToday,
                weekCompletions = weeklyCompletions,
                totalCompletions = totalCompletions,
                completionRatePercent = completionRatePercent,
                bestHabitLabel = bestHabitLabel
            )
        }
        item {
            Text(
                text = "습관별 현황",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        items(habits, key = { it.id }) { habit ->
            HabitStatsCard(
                habit = habit,
                today = today,
                weekStart = weekStart,
                dateFormatter = dateFormatter
            )
        }
    }
}
