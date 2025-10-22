package com.apptive.myapplication.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import com.apptive.myapplication.model.Habit
import com.apptive.myapplication.ui.components.HabitRow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeTab(
    habits: SnapshotStateList<Habit>,
    onAddHabit: (String) -> Unit,
    onToggleHabit: (Habit, Boolean) -> Unit,
    onRemoveHabit: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("M월 d일") }
    var newHabitName by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "오늘의 습관",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = today.format(dateFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newHabitName,
                    onValueChange = { newHabitName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("새 습관 이름") },
                    placeholder = { Text("예: 아침 스트레칭 10분") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = {
                            onAddHabit(newHabitName)
                            newHabitName = ""
                        },
                        enabled = newHabitName.isNotBlank()
                    ) {
                        Text("추가")
                    }
                }
            }
        }

        if (habits.isEmpty()) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "아직 등록된 습관이 없어요.",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "추가 버튼을 눌러 오늘 실천할 습관을 만들어보세요!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(
                items = habits,
                key = { it.id }
            ) { habit ->
                val isCompleted = habit.isCompletedOn(today)
                HabitRow(
                    habit = habit,
                    isCompletedToday = isCompleted,
                    onToggleCompletion = { onToggleHabit(habit, it) },
                    onRemoveHabit = { onRemoveHabit(habit) }
                )
            }
        }
    }
}
