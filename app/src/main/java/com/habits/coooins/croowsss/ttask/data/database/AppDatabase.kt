package com.habits.coooins.croowsss.ttask.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.habits.coooins.croowsss.ttask.data.focussession.local.FocusSessionDao
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalDao
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitDao
import com.habits.coooins.croowsss.ttask.data.task.local.TaskDao
import com.habits.coooins.croowsss.ttask.data.focussession.local.FocusSessionEntity
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitLogEntity
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        GoalEntity::class,
        FocusSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun goalDao(): GoalDao
    abstract fun focusSessionDao(): FocusSessionDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focus_ledger_database"
                ) .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
