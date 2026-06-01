package com.habits.coooins.croowsss.ttask.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.habits.coooins.croowsss.ttask.data.task.TaskPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    
    companion object {
        val FIRST_DAY_OF_WEEK_KEY = intPreferencesKey("first_day_of_week")
        val DEFAULT_PRIORITY_KEY = stringPreferencesKey("default_priority")
    }

    val firstDayOfWeek: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FIRST_DAY_OF_WEEK_KEY] ?: 1 // Monday
    }
    
    val defaultTaskPriority: Flow<TaskPriority> = context.dataStore.data.map { preferences ->
        val value = preferences[DEFAULT_PRIORITY_KEY] ?: TaskPriority.MEDIUM.name
        TaskPriority.valueOf(value)
    }

    suspend fun setFirstDayOfWeek(day: Int) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_DAY_OF_WEEK_KEY] = day
        }
    }
    
    suspend fun setDefaultPriority(taskPriority: TaskPriority) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_PRIORITY_KEY] = taskPriority.name
        }
    }
}
