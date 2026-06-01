package com.habits.coooins.croowsss.ttask.data.goal.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val targetValue: Float,
    val currentValue: Float = 0f,
    val unit: String = "",
    val status: GoalStatus = GoalStatus.IN_PROGRESS,
    val createdAt: Long = System.currentTimeMillis()
)

