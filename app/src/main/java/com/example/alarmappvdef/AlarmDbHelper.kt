package com.example.alarmappvdef

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AlarmDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HOUR INTEGER,
                $COLUMN_MINUTE INTEGER,
                $COLUMN_LABEL TEXT,
                $COLUMN_IS_ACTIVE INTEGER
            );
        """.trimIndent()
        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addAlarm(hour: Int, minute: Int, label: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HOUR, hour)
            put(COLUMN_MINUTE, minute)
            put(COLUMN_LABEL, label)
            put(COLUMN_IS_ACTIVE, 0) // Default inactive state
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllAlarms(): List<Alarm> {
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val alarms = mutableListOf<Alarm>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val hour = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HOUR))
            val minute = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINUTE))
            val label = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LABEL))
            val isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_ACTIVE)) == 1
            alarms.add(Alarm(id, hour, minute, label, isActive))
        }
        cursor.close()
        db.close()

        return alarms
    }

    fun updateAlarmStatus(id: Long, isActive: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_ACTIVE, if (isActive) 1 else 0)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    companion object {
        const val DATABASE_NAME = "alarm.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "alarms"
        const val COLUMN_ID = "id"
        const val COLUMN_HOUR = "hour"
        const val COLUMN_MINUTE = "minute"
        const val COLUMN_LABEL = "label"
        const val COLUMN_IS_ACTIVE = "is_active" // Nouvelle colonne pour stocker l'état de l'alarme
    }
}
