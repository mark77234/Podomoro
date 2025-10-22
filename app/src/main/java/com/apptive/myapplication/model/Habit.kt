package com.apptive.myapplication.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.time.LocalDate

class Habit(
    val id: Int,
    val name: String
) {
    val completionDates: SnapshotStateList<LocalDate> = mutableStateListOf()

    fun isCompletedOn(date: LocalDate): Boolean = completionDates.contains(date)
}
