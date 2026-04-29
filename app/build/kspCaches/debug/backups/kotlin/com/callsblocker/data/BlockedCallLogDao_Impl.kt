package com.callsblocker.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
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
public class BlockedCallLogDao_Impl(
  __db: RoomDatabase,
) : BlockedCallLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBlockedCallLog: EntityInsertAdapter<BlockedCallLog>

  private val __callActionConverter: CallActionConverter = CallActionConverter()
  init {
    this.__db = __db
    this.__insertAdapterOfBlockedCallLog = object : EntityInsertAdapter<BlockedCallLog>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `blocked_call_logs` (`id`,`number`,`timestamp`,`action`,`label`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BlockedCallLog) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.number)
        statement.bindLong(3, entity.timestamp)
        val _tmp: String = __callActionConverter.fromCallAction(entity.action)
        statement.bindText(4, _tmp)
        val _tmpLabel: String? = entity.label
        if (_tmpLabel == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpLabel)
        }
      }
    }
  }

  public override suspend fun insert(log: BlockedCallLog): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfBlockedCallLog.insert(_connection, log)
  }

  public override fun getAllLogs(): Flow<List<BlockedCallLog>> {
    val _sql: String = "SELECT * FROM blocked_call_logs ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("blocked_call_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfAction: Int = getColumnIndexOrThrow(_stmt, "action")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _result: MutableList<BlockedCallLog> = mutableListOf()
        while (_stmt.step()) {
          val _item: BlockedCallLog
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpAction: CallAction
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfAction)
          _tmpAction = __callActionConverter.toCallAction(_tmp)
          val _tmpLabel: String?
          if (_stmt.isNull(_columnIndexOfLabel)) {
            _tmpLabel = null
          } else {
            _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          }
          _item = BlockedCallLog(_tmpId,_tmpNumber,_tmpTimestamp,_tmpAction,_tmpLabel)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllLogs() {
    val _sql: String = "DELETE FROM blocked_call_logs"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteLog(id: Long) {
    val _sql: String = "DELETE FROM blocked_call_logs WHERE id = ?"
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
