package com.habits.coooins.croowsss.ttask.data.focussession.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity

@Entity(
    tableName = "focus_sessions",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("taskId"), Index("startAt"), Index("label", unique = true)]
)
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startAt: Long,
    val endAt: Long? = null,
    val label: String? = null,
    val taskId: Long? = null
) {
    @Ignore
    var taskTitle: String? = null
}