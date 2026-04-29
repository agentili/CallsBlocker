package com.callsblocker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_call_logs")
data class BlockedCallLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: String,
    val timestamp: Long,
    val action: CallAction,
    val label: String? = null
)
