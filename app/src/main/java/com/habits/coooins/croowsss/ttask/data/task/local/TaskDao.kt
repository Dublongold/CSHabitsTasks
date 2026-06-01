package com.habits.coooins.croowsss.ttask.data.task.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.habits.coooins.croowsss.ttask.data.task.TaskStatus
import com.habits.coooins.croowsss.ttask.data.task.TaskInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun listenAllTasks(): Flow<List<TaskEntity>>
    @Query("SELECT id FROM tasks ORDER BY createdAt DESC")
    fun listenAllTaskIds(): Flow<List<Long>>
    @Query("SELECT id, title FROM tasks ORDER BY createdAt DESC")
    fun listenAllTaskInfo(): Flow<List<TaskInfo>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY createdAt DESC")
    fun getTasksByStatus(status: TaskStatus): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date(createdAt / 1000, 'unixepoch', 'localtime') = date('now', 'localtime') AND status = :status")
    fun getTodayTasks(status: TaskStatus = TaskStatus.OPEN): Flow<List<TaskEntity>>

    @Upsert
    suspend fun upsertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}