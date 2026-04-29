package com.callsblocker.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BlockedEntryDao_Impl(
  __db: RoomDatabase,
) : BlockedEntryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBlockedEntry: EntityInsertAdapter<BlockedEntry>

  private val __callActionConverter: CallActionConverter = CallActionConverter()

  private val __deleteAdapterOfBlockedEntry: EntityDeleteOrUpdateAdapter<BlockedEntry>

  private val __updateAdapterOfBlockedEntry: EntityDeleteOrUpdateAdapter<BlockedEntry>
  init {
    this.__db = __db
    this.__insertAdapterOfBlockedEntry = object : EntityInsertAdapter<BlockedEntry>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `blocked_entries` (`id`,`pattern`,`isPrefix`,`label`,`action`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BlockedEntry) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.pattern)
        val _tmp: Int = if (entity.isPrefix) 1 else 0
        statement.bindLong(3, _tmp.toLong())
        statement.bindText(4, entity.label)
        val _tmp_1: String = __callActionConverter.fromCallAction(entity.action)
        statement.bindText(5, _tmp_1)
      }
    }
    this.__deleteAdapterOfBlockedEntry = object : EntityDeleteOrUpdateAdapter<BlockedEntry>() {
      protected override fun createQuery(): String = "DELETE FROM `blocked_entries` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: BlockedEntry) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfBlockedEntry = object : EntityDeleteOrUpdateAdapter<BlockedEntry>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `blocked_entries` SET `id` = ?,`pattern` = ?,`isPrefix` = ?,`label` = ?,`action` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: BlockedEntry) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.pattern)
        val _tmp: Int = if (entity.isPrefix) 1 else 0
        statement.bindLong(3, _tmp.toLong())
        statement.bindText(4, entity.label)
        val _tmp_1: String = __callActionConverter.fromCallAction(entity.action)
        statement.bindText(5, _tmp_1)
        statement.bindLong(6, entity.id)
      }
    }
  }

  public override suspend fun insert(entry: BlockedEntry): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfBlockedEntry.insertAndReturnId(_connection, entry)
    _result
  }

  public override suspend fun insertAll(entries: List<BlockedEntry>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfBlockedEntry.insert(_connection, entries)
  }

  public override suspend fun delete(entry: BlockedEntry): Unit = performSuspending(__db, false,
      true) { _connection ->
    __deleteAdapterOfBlockedEntry.handle(_connection, entry)
  }

  public override suspend fun update(entry: BlockedEntry): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfBlockedEntry.handle(_connection, entry)
  }

  public override fun getAll(): Flow<List<BlockedEntry>> {
    val _sql: String = "SELECT * FROM blocked_entries ORDER BY pattern ASC"
    return createFlow(__db, false, arrayOf("blocked_entries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPattern: Int = getColumnIndexOrThrow(_stmt, "pattern")
        val _columnIndexOfIsPrefix: Int = getColumnIndexOrThrow(_stmt, "isPrefix")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _result: MutableList<BlockedEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: BlockedEntry
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPattern: String
          _tmpPattern = _stmt.getText(_columnIndexOfPattern)
          val _tmpIsPrefix: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPrefix).toInt()
          _tmpIsPrefix = _tmp != 0
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpAction: CallAction
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfAction)
          _tmpAction = __callActionConverter.toCallAction(_tmp_1)
          _item = BlockedEntry(_tmpId,_tmpPattern,_tmpIsPrefix,_tmpLabel,_tmpAction)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllSync(): List<BlockedEntry> {
    val _sql: String = "SELECT * FROM blocked_entries ORDER BY pattern ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPattern: Int = getColumnIndexOrThrow(_stmt, "pattern")
        val _columnIndexOfIsPrefix: Int = getColumnIndexOrThrow(_stmt, "isPrefix")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _result: MutableList<BlockedEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: BlockedEntry
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPattern: String
          _tmpPattern = _stmt.getText(_columnIndexOfPattern)
          val _tmpIsPrefix: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPrefix).toInt()
          _tmpIsPrefix = _tmp != 0
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpAction: CallAction
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfAction)
          _tmpAction = __callActionConverter.toCallAction(_tmp_1)
          _item = BlockedEntry(_tmpId,_tmpPattern,_tmpIsPrefix,_tmpLabel,_tmpAction)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): BlockedEntry? {
    val _sql: String = "SELECT * FROM blocked_entries WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPattern: Int = getColumnIndexOrThrow(_stmt, "pattern")
        val _columnIndexOfIsPrefix: Int = getColumnIndexOrThrow(_stmt, "isPrefix")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _result: BlockedEntry?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPattern: String
          _tmpPattern = _stmt.getText(_columnIndexOfPattern)
          val _tmpIsPrefix: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPrefix).toInt()
          _tmpIsPrefix = _tmp != 0
          val _tmpLabel: String
          _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          val _tmpAction: CallAction
          val _tmp_1: String
          _tmp_1 = _stmt.getText(_columnIndexOfAction)
          _tmpAction = __callActionConverter.toCallAction(_tmp_1)
          _result = BlockedEntry(_tmpId,_tmpPattern,_tmpIsPrefix,_tmpLabel,_tmpAction)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Long) {
    val _sql: String = "DELETE FROM blocked_entries WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAll() {
    val _sql: String = "DELETE FROM blocked_entries"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
