package com.callsblocker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedEntryDao {
    @Insert
    suspend fun insert(entry: BlockedEntry): Long

    @Insert
    suspend fun insertAll(entries: List<BlockedEntry>)

    @Update
    suspend fun update(entry: BlockedEntry)

    @Delete
    suspend fun delete(entry: BlockedEntry)

    @Query("DELETE FROM blocked_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM blocked_entries")
    suspend fun deleteAll()

    @Query("SELECT * FROM blocked_entries ORDER BY pattern ASC")
    fun getAll(): Flow<List<BlockedEntry>>

    @Query("SELECT * FROM blocked_entries ORDER BY pattern ASC")
    suspend fun getAllSync(): List<BlockedEntry>

    @Query("SELECT * FROM blocked_entries WHERE id = :id")
    suspend fun getById(id: Long): BlockedEntry?
}
