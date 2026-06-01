package com.habits.coooins.croowsss.ttask.ui.tasks

import com.habits.coooins.croowsss.ttask.data.task.TaskPriority
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus

val TaskPriority.displayName
    get() = when (this) {
        TaskPriority.LOW -> "Low"
        TaskPriority.MEDIUM -> "Medium"
        TaskPriority.HIGH -> "High"
    }

val TaskStatus.displayName
    get() = when (this) {
        TaskStatus.OPEN -> "Open"
        TaskStatus.DONE -> "Done"
        TaskStatus.ARCHIVED -> "Archived"
    }
