package com.callsblocker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedCallLogDao {
    @Insert
    suspend fun insert(log: BlockedCallLog)

    @Query("SELECT * FROM blocked_call_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<BlockedCallLog>>

    @Query("DELETE FROM blocked_call_logs")
    suspend fun deleteAllLogs()

    @Query("DELETE FROM blocked_call_logs WHERE id = :id")
    suspend fun deleteLog(id: Long)
}
