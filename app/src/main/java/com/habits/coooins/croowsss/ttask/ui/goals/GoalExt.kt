package com.habits.coooins.croowsss.ttask.ui.goals

import com.habits.coooins.croowsss.ttask.data.goal.local.GoalStatus

val GoalStatus.displayName
    get() = when (this) {
        GoalStatus.IN_PROGRESS -> "In progress"
        GoalStatus.COMPLETED -> "Completed"
        GoalStatus.ARCHIVED -> "Archived"
    }