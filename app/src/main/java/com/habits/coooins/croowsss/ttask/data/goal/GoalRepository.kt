package com.habits.coooins.croowsss.ttask.data.goal

import com.habits.coooins.croowsss.ttask.data.goal.local.GoalDao
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalEntity
import com.habits.coooins.croowsss.ttask.data.goal.local.GoalStatus
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {
    fun getAllGoals(): Flow<List<GoalEntity>> = goalDao.getAllGoals()

    fun getGoalById(goalId: Long): Flow<GoalEntity?> = goalDao.getGoalById(goalId)

    fun getGoalsByStatus(status: GoalStatus): Flow<List<GoalEntity>> =
        goalDao.getGoalsByStatus(status)

    suspend fun insertGoal(goal: GoalEntity): Long = goalDao.upsertGoal(goal)

    suspend fun updateGoal(goal: GoalEntity) = goalDao.updateGoal(goal)

    suspend fun deleteGoal(goal: GoalEntity) = goalDao.deleteGoal(goal)

    suspend fun deleteAllGoals() = goalDao.deleteAllGoals()
}