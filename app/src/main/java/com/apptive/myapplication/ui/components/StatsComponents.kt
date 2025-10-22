package com.apptive.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apptive.myapplication.model.Habit
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EmptyStatsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "아직 통계가 없어요",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "홈 탭에서 습관을 추가하고 체크하면 통계가 채워져요.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun SummaryCard(
    totalHabits: Int,
    completedToday: Int,
    weekCompletions: Int,
    totalCompletions: Int,
    completionRatePercent: Int,
    bestHabitLabel: String?,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    val dailyProgress = if (totalHabits > 0) {
        completedToday.toFloat() / totalHabits
    } else {
        0f
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "오늘의 현황",
                style = MaterialTheme.typography.titleMedium
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "$completedToday / $totalHabits 습관 달성",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = { dailyProgress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = contentColor,
                    trackColor = contentColor.copy(alpha = 0.2f),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
                Text(
                    text = "오늘 완료율 ${completionRatePercent}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatValue(
                    label = "이번 주 체크",
                    value = "${weekCompletions}회",
                    labelColor = contentColor.copy(alpha = 0.75f),
                    valueColor = contentColor,
                    modifier = Modifier.weight(1f)
                )
                StatValue(
                    label = "누적 완료",
                    value = "${totalCompletions}회",
                    labelColor = contentColor.copy(alpha = 0.75f),
                    valueColor = contentColor,
                    modifier = Modifier.weight(1f)
                )
                StatValue(
                    label = "등록된 습관",
                    value = "${totalHabits}개",
                    labelColor = contentColor.copy(alpha = 0.75f),
                    valueColor = contentColor,
                    modifier = Modifier.weight(1f)
                )
            }
            bestHabitLabel?.let { highlight ->
                Text(
                    text = "가장 꾸준한 습관: $highlight",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun StatValue(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
fun HabitStatsCard(
    habit: Habit,
    today: LocalDate,
    weekStart: LocalDate,
    dateFormatter: DateTimeFormatter,
    modifier: Modifier = Modifier
) {
    val isCompletedToday = habit.isCompletedOn(today)
    val weeklyCount = habit.completionDates.count { date ->
        !date.isBefore(weekStart) && !date.isAfter(today)
    }
    val totalCount = habit.completionDates.size
    val latestCompletion = habit.completionDates.maxOrNull()
    val weeklyProgress = (weeklyCount / 7f).coerceIn(0f, 1f)

    val containerColor = if (isCompletedToday) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isCompletedToday) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor.copy(alpha = 0.95f),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (isCompletedToday) "오늘 완료" else "아직 체크 전",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isCompletedToday) MaterialTheme.colorScheme.primary else contentColor
                )
            }
            LinearProgressIndicator(
                progress = { weeklyProgress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
            Text(
                text = "이번 주 ${weeklyCount}회 / 목표 7회",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatValue(
                    label = "오늘",
                    value = if (isCompletedToday) "완료" else "미완료",
                    labelColor = contentColor.copy(alpha = 0.7f),
                    valueColor = contentColor.copy(alpha = 0.95f),
                    modifier = Modifier.weight(1f)
                )
                StatValue(
                    label = "이번 주",
                    value = "${weeklyCount}회",
                    labelColor = contentColor.copy(alpha = 0.7f),
                    valueColor = contentColor.copy(alpha = 0.95f),
                    modifier = Modifier.weight(1f)
                )
                StatValue(
                    label = "누적",
                    value = "${totalCount}회",
                    labelColor = contentColor.copy(alpha = 0.7f),
                    valueColor = contentColor.copy(alpha = 0.95f),
                    modifier = Modifier.weight(1f)
                )
            }
            latestCompletion?.let { date ->
                Text(
                    text = "최근 완료: ${date.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}
