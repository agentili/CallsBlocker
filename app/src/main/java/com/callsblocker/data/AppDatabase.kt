package com.callsblocker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter

@Database(entities = [BlockedEntry::class, BlockedCallLog::class], version = 3)
@androidx.room.TypeConverters(CallActionConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedEntryDao(): BlockedEntryDao
    abstract fun blockedCallLogDao(): BlockedCallLogDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(context, AppDatabase::class.java, "callblocker.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}

class CallActionConverter {
    @TypeConverter
    fun fromCallAction(action: CallAction): String = action.name

    @TypeConverter
    fun toCallAction(value: String): CallAction = CallAction.valueOf(value)
}
