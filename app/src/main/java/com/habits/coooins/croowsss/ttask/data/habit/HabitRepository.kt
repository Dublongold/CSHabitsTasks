package com.habits.coooins.croowsss.ttask.data.habit

import com.habits.coooins.croowsss.ttask.data.habit.local.HabitDao
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitEntity
import com.habits.coooins.croowsss.ttask.data.habit.local.HabitLogEntity
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    fun getAllHabits(): Flow<List<HabitEntity>> = habitDao.getAllHabits()

    fun getHabitById(habitId: Long): Flow<HabitEntity?> = habitDao.getHabitById(habitId)

    fun getActiveHabits(): Flow<List<HabitEntity>> = habitDao.getActiveHabits()

    suspend fun insertHabit(habit: HabitEntity): Long = habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: HabitEntity) = habitDao.updateHabit(habit)

    suspend fun deleteHabit(habit: HabitEntity) = habitDao.deleteHabit(habit)

    fun getHabitLogs(habitId: Long): Flow<List<HabitLogEntity>> =
        habitDao.getHabitLogs(habitId)

    fun getTodayLog(habitId: Long): Flow<HabitLogEntity?> =
        habitDao.getTodayLog(habitId)

    suspend fun insertHabitLog(log: HabitLogEntity): Long = habitDao.insertHabitLog(log)

    suspend fun updateHabitLog(log: HabitLogEntity) = habitDao.updateHabitLog(log)

    suspend fun deleteHabitLog(log: HabitLogEntity) = habitDao.deleteHabitLog(log)

    suspend fun deleteAllLogsForHabit(habitId: Long) =
        habitDao.deleteAllLogsForHabit(habitId)
    suspend fun deleteAllHabits() = habitDao.deleteAllHabits()
}