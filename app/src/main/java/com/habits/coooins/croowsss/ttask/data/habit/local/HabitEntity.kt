package com.habits.coooins.croowsss.ttask.data.habit.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val habitScheduleType: HabitScheduleType = HabitScheduleType.DAILY,
    val targetCount: Int = 1,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

