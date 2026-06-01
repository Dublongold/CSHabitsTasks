package com.habits.coooins.croowsss.ttask.data.task.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.habits.coooins.croowsss.ttask.data.task.TaskPriority
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val dueDateTime: Long? = null,
    val taskPriority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

