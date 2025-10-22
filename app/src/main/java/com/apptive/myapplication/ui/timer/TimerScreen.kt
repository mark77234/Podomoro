package com.apptive.myapplication.ui.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Locale

private enum class TimerSelection(
    val label: String,
    val focusDefault: Int,
    val breakDefault: Int,
    val description: String
) {
    FIFTY_TEN(label = "50/10", focusDefault = 50, breakDefault = 10, description = "50분 집중 · 10분 휴식"),
    THIRTY_FIVE(label = "30/5", focusDefault = 30, breakDefault = 5, description = "30분 집중 · 5분 휴식"),
    CUSTOM(label = "커스텀", focusDefault = 50, breakDefault = 10, description = "원하는 집중/휴식 시간을 설정")
}

private enum class TimerPhase {
    FOCUS,
    BREAK;

    fun next(): TimerPhase = if (this == FOCUS) BREAK else FOCUS

    val title: String
        get() = when (this) {
            FOCUS -> "집중 시간"
            BREAK -> "휴식 시간"
        }

    val message: String
        get() = when (this) {
            FOCUS -> "지금은 몰입에 집중해보세요"
            BREAK -> "잠깐 쉬면서 에너지를 충전하세요"
        }
}

@Composable
fun TimerTab(modifier: Modifier = Modifier) {
    var selectedOption by rememberSaveable { mutableStateOf(TimerSelection.FIFTY_TEN) }
    var customFocusInput by rememberSaveable { mutableStateOf(TimerSelection.CUSTOM.focusDefault.toString()) }
    var customBreakInput by rememberSaveable { mutableStateOf(TimerSelection.CUSTOM.breakDefault.toString()) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var currentPhase by rememberSaveable { mutableStateOf(TimerPhase.FOCUS) }
    var remainingSeconds by rememberSaveable { mutableIntStateOf(TimerSelection.FIFTY_TEN.focusDefault * 60) }

    val focusMinutes = selectedOption.focusMinutes(customFocusInput.toMinutesOrZero())
    val breakMinutes = selectedOption.breakMinutes(customBreakInput.toMinutesOrZero())
    val isConfigValid = focusMinutes > 0 && breakMinutes > 0
    val totalSecondsThisPhase = when (currentPhase) {
        TimerPhase.FOCUS -> focusMinutes * 60
        TimerPhase.BREAK -> breakMinutes * 60
    }
    val progress = if (totalSecondsThisPhase > 0) {
        1f - (remainingSeconds.toFloat() / totalSecondsThisPhase.toFloat())
    } else {
        0f
    }

    LaunchedEffect(selectedOption, focusMinutes, breakMinutes) {
        isRunning = false
        currentPhase = TimerPhase.FOCUS
        remainingSeconds = focusMinutes.coerceAtLeast(0) * 60
    }

    LaunchedEffect(isRunning, currentPhase, focusMinutes, breakMinutes) {
        if (!isRunning) return@LaunchedEffect

        while (isRunning && remainingSeconds > 0) {
            delay(1_000L)
            remainingSeconds = (remainingSeconds - 1).coerceAtLeast(0)
        }

        if (!isRunning) return@LaunchedEffect

        val nextPhase = currentPhase.next()
        val nextDurationMinutes = when (nextPhase) {
            TimerPhase.FOCUS -> focusMinutes
            TimerPhase.BREAK -> breakMinutes
        }

        if (nextDurationMinutes <= 0) {
            isRunning = false
            currentPhase = nextPhase
            remainingSeconds = 0
        } else {
            currentPhase = nextPhase
            remainingSeconds = nextDurationMinutes * 60
        }
    }

    val (containerColor, contentColor) = if (currentPhase == TimerPhase.FOCUS) {
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "포모도로 타이머",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = selectedOption.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TimerSelection.entries.forEach { option ->
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = option == selectedOption,
                    onClick = {
                        if (selectedOption != option) {
                            selectedOption = option
                        }
                    },
                    leadingIcon = if (option == selectedOption) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null
                            )
                        }
                    } else null,
                    label = { Text(option.label) }
                )
            }
        }

        if (selectedOption == TimerSelection.CUSTOM) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = customFocusInput,
                        onValueChange = { input ->
                            customFocusInput = input.filter { it.isDigit() }.take(3)
                        },
                        singleLine = true,
                        label = { Text("집중 (분)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        suffix = { Text("분") }
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = customBreakInput,
                        onValueChange = { input ->
                            customBreakInput = input.filter { it.isDigit() }.take(3)
                        },
                        singleLine = true,
                        label = { Text("휴식 (분)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        suffix = { Text("분") }
                    )
                }
                Text(
                    text = "1~600분 사이의 값을 입력할 수 있어요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = currentPhase.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatAsClock(remainingSeconds),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.SemiBold
                )
                LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = contentColor,
                trackColor = contentColor.copy(alpha = 0.2f),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
                Text(
                    text = currentPhase.message,
                    style = MaterialTheme.typography.bodyMedium
                )
                val nextPhaseText = if (currentPhase == TimerPhase.FOCUS) {
                    "다음은 휴식 ${breakMinutes}분"
                } else {
                    "다음은 집중 ${focusMinutes}분"
                }
                Text(
                    text = nextPhaseText,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.8f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                enabled = isConfigValid,
                onClick = {
                    if (isRunning) {
                        isRunning = false
                    } else {
                        if (remainingSeconds <= 0) {
                            currentPhase = TimerPhase.FOCUS
                            remainingSeconds = focusMinutes * 60
                        }
                        if (isConfigValid) {
                            isRunning = true
                        }
                    }
                }
            ) {
                Text(if (isRunning) "일시정지" else "시작")
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    isRunning = false
                    currentPhase = TimerPhase.FOCUS
                    remainingSeconds = focusMinutes * 60
                }
            ) {
                Text("리셋")
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "현재 설정: 집중 ${focusMinutes}분 / 휴식 ${breakMinutes}분",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!isConfigValid) {
                Text(
                    text = "집중과 휴식 시간을 모두 1분 이상으로 설정해주세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun TimerSelection.focusMinutes(customFocusMinutes: Int): Int =
    if (this == TimerSelection.CUSTOM) customFocusMinutes else focusDefault

private fun TimerSelection.breakMinutes(customBreakMinutes: Int): Int =
    if (this == TimerSelection.CUSTOM) customBreakMinutes else breakDefault

private fun String.toMinutesOrZero(maxMinutes: Int = 600): Int =
    this.toIntOrNull()?.coerceIn(0, maxMinutes) ?: 0

private fun formatAsClock(seconds: Int): String {
    val safeSeconds = seconds.coerceAtLeast(0)
    val minutesPart = safeSeconds / 60
    val secondsPart = safeSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutesPart, secondsPart)
}
