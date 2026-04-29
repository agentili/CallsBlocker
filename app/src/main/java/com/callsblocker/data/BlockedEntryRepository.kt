package com.callsblocker.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BlockedEntryRepository @Inject constructor(
    private val blockedEntryDao: BlockedEntryDao,
    private val blockedCallLogDao: BlockedCallLogDao
) {
    suspend fun insert(entry: BlockedEntry): Long = blockedEntryDao.insert(entry)

    suspend fun insertAll(entries: List<BlockedEntry>) = blockedEntryDao.insertAll(entries)

    suspend fun update(entry: BlockedEntry) = blockedEntryDao.update(entry)

    suspend fun delete(entry: BlockedEntry) = blockedEntryDao.delete(entry)

    suspend fun deleteById(id: Long) = blockedEntryDao.deleteById(id)

    suspend fun deleteAll() = blockedEntryDao.deleteAll()

    fun getAll(): Flow<List<BlockedEntry>> = blockedEntryDao.getAll()

    suspend fun getAllSync(): List<BlockedEntry> = blockedEntryDao.getAllSync()

    suspend fun getById(id: Long): BlockedEntry? = blockedEntryDao.getById(id)

    // Call Logs
    fun getAllLogs(): Flow<List<BlockedCallLog>> = blockedCallLogDao.getAllLogs()

    suspend fun insertLog(log: BlockedCallLog) = blockedCallLogDao.insert(log)

    suspend fun deleteLog(id: Long) = blockedCallLogDao.deleteLog(id)

    suspend fun deleteAllLogs() = blockedCallLogDao.deleteAllLogs()
}
