package com.habits.coooins.croowsss.ttask.ui.habits

import com.habits.coooins.croowsss.ttask.data.habit.local.HabitScheduleType


val HabitScheduleType.displayName
    get() = when (this) {
        HabitScheduleType.DAILY -> "Daily"
        HabitScheduleType.WEEKLY -> "Weekly"
    }