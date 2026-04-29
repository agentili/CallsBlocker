package com.callsblocker.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _blockedEntryDao: Lazy<BlockedEntryDao> = lazy {
    BlockedEntryDao_Impl(this)
  }

  private val _blockedCallLogDao: Lazy<BlockedCallLogDao> = lazy {
    BlockedCallLogDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "22b11f0d7ebb8280716a41a6826af57a", "c27ef554c0b56faff4de8519fd9532af") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `blocked_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `pattern` TEXT NOT NULL, `isPrefix` INTEGER NOT NULL, `label` TEXT NOT NULL, `action` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `blocked_call_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `number` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `action` TEXT NOT NULL, `label` TEXT)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '22b11f0d7ebb8280716a41a6826af57a')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `blocked_entries`")
        connection.execSQL("DROP TABLE IF EXISTS `blocked_call_logs`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsBlockedEntries: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBlockedEntries.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedEntries.put("pattern", TableInfo.Column("pattern", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedEntries.put("isPrefix", TableInfo.Column("isPrefix", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedEntries.put("label", TableInfo.Column("label", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedEntries.put("action", TableInfo.Column("action", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBlockedEntries: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBlockedEntries: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBlockedEntries: TableInfo = TableInfo("blocked_entries", _columnsBlockedEntries,
            _foreignKeysBlockedEntries, _indicesBlockedEntries)
        val _existingBlockedEntries: TableInfo = read(connection, "blocked_entries")
        if (!_infoBlockedEntries.equals(_existingBlockedEntries)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |blocked_entries(com.callsblocker.data.BlockedEntry).
              | Expected:
              |""".trimMargin() + _infoBlockedEntries + """
              |
              | Found:
              |""".trimMargin() + _existingBlockedEntries)
        }
        val _columnsBlockedCallLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBlockedCallLogs.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedCallLogs.put("number", TableInfo.Column("number", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedCallLogs.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedCallLogs.put("action", TableInfo.Column("action", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBlockedCallLogs.put("label", TableInfo.Column("label", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBlockedCallLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBlockedCallLogs: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBlockedCallLogs: TableInfo = TableInfo("blocked_call_logs",
            _columnsBlockedCallLogs, _foreignKeysBlockedCallLogs, _indicesBlockedCallLogs)
        val _existingBlockedCallLogs: TableInfo = read(connection, "blocked_call_logs")
        if (!_infoBlockedCallLogs.equals(_existingBlockedCallLogs)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |blocked_call_logs(com.callsblocker.data.BlockedCallLog).
              | Expected:
              |""".trimMargin() + _infoBlockedCallLogs + """
              |
              | Found:
              |""".trimMargin() + _existingBlockedCallLogs)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "blocked_entries",
        "blocked_call_logs")
  }

  public override fun clearAllTables() {
    super.performClear(false, "blocked_entries", "blocked_call_logs")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(BlockedEntryDao::class, BlockedEntryDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BlockedCallLogDao::class, BlockedCallLogDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun blockedEntryDao(): BlockedEntryDao = _blockedEntryDao.value

  public override fun blockedCallLogDao(): BlockedCallLogDao = _blockedCallLogDao.value
}
