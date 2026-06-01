package com.habits.coooins.croowsss.ttask.data.task

import com.habits.coooins.croowsss.ttask.data.task.local.TaskDao
import com.habits.coooins.croowsss.ttask.data.task.local.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun listenAllTasks() = taskDao.listenAllTasks()
    fun listenAllTaskInfo() = taskDao.listenAllTaskInfo()

    fun getTaskById(taskId: Long): Flow<TaskEntity?> = taskDao.getTaskById(taskId)

    fun getTodayTasks(): Flow<List<TaskEntity>> = taskDao.getTodayTasks()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.upsertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()
}