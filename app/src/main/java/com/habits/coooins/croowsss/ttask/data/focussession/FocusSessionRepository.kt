package com.habits.coooins.croowsss.ttask.data.focussession

import com.habits.coooins.croowsss.ttask.data.focussession.local.FocusSessionDao
import com.habits.coooins.croowsss.ttask.data.focussession.local.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

class FocusSessionRepository(private val focusSessionDao: FocusSessionDao) {
    fun listenAllSessions(): Flow<List<FocusSessionEntity>> = focusSessionDao.listenAllSessions()
    fun listenAllSessionNames(): Flow<List<String>> = focusSessionDao.listenAllSessionNames()

    fun getSessionById(sessionId: Long): Flow<FocusSessionEntity?> =
        focusSessionDao.getSessionById(sessionId)

    suspend fun upsertSession(session: FocusSessionEntity) =
        focusSessionDao.upsertSession(session)

    suspend fun deleteSession(session: FocusSessionEntity) =
        focusSessionDao.deleteSession(session)

    suspend fun deleteAllSessions() = focusSessionDao.deleteAllSessions()
}