package com.habits.coooins.croowsss.ttask.data.database

import androidx.room.TypeConverter
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalStatus
import com.habits.coooins.croowsss.ttask.data.task.TaskPriority
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitScheduleType
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus

class Converters {
    @TypeConverter
    fun fromPriority(taskPriority: TaskPriority): String = taskPriority.name
    
    @TypeConverter
    fun toPriority(value: String): TaskPriority = TaskPriority.valueOf(value)
    
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String = status.name
    
    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)
    
    @TypeConverter
    fun fromGoalStatus(status: GoalStatus): String = status.name
    
    @TypeConverter
    fun toGoalStatus(value: String): GoalStatus = GoalStatus.valueOf(value)
    
    @TypeConverter
    fun fromScheduleType(type: HabitScheduleType): String = type.name
    
    @TypeConverter
    fun toScheduleType(value: String): HabitScheduleType = HabitScheduleType.valueOf(value)
}
