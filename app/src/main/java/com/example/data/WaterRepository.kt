package com.example.data

import kotlinx.coroutines.flow.Flow

class WaterRepository(private val waterLogDao: WaterLogDao) {
    val allLogs: Flow<List<WaterLog>> = waterLogDao.getAllLogs()

    fun getLogsForDay(day: String): Flow<List<WaterLog>> {
        return waterLogDao.getLogsForDay(day)
    }

    fun getTodayTotal(day: String): Flow<Int?> {
        return waterLogDao.getTodayTotal(day)
    }

    suspend fun insertLog(log: WaterLog) {
        waterLogDao.insertLog(log)
    }

    suspend fun deleteLog(log: WaterLog) {
        waterLogDao.deleteLog(log)
    }

    suspend fun deleteLogById(id: Int) {
        waterLogDao.deleteLogById(id)
    }
}
