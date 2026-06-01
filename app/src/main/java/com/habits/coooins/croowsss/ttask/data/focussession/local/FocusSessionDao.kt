package com.habits.coooins.croowsss.ttask.data.focussession.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY startAt DESC")
    fun listenAllSessions(): Flow<List<FocusSessionEntity>>
    @Query("SELECT label FROM focus_sessions WHERE label IS NOT NULL ORDER BY startAt DESC")
    fun listenAllSessionNames(): Flow<List<String>>

    @Query("SELECT * FROM focus_sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: Long): Flow<FocusSessionEntity?>


    @Query("SELECT * FROM focus_sessions WHERE startAt >= :startTime AND startAt <= :endTime ORDER BY startAt DESC")
    fun getSessionsInRange(startTime: Long, endTime: Long): Flow<List<FocusSessionEntity>>

    @Upsert
    suspend fun upsertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Delete
    suspend fun deleteSession(session: FocusSessionEntity)

    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllSessions()
}