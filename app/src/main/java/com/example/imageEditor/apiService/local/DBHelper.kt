package com.example.imageEditor.apiService.local

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.imageEditor.model.QueryModel

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        // Tạo bảng nếu chưa tồn tại
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int,
    ) {
        // Xóa bảng cũ nếu tồn tại và tạo lại
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addData(name: String) {
        val db = writableDatabase
        // Kiểm tra xem bản ghi với cùng name đã tồn tại trong bảng chưa
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME = ?"
        val cursor = db.rawQuery(query, arrayOf(name))

        if (!cursor.moveToFirst()) {
            // Bản ghi chưa tồn tại, chèn một bản ghi mới
            val values = ContentValues()
            values.put(COLUMN_NAME, name)
            db.insert(TABLE_NAME, null, values)
        }

        // Đóng cursor và đóng cơ sở dữ liệu
        cursor.close()
        db.close()
    }

    // Phương thức xóa dữ liệu khỏi cơ sở dữ liệu
    fun deleteData(id: Long): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getAllData(): List<QueryModel> {
        val dataList = mutableListOf<QueryModel>()
        val query = "SELECT * FROM $TABLE_NAME"
        val db = readableDatabase
        val cursor: Cursor? = db.rawQuery(query, null)

        cursor?.use {
            while (it.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val name = it.getString(it.getColumnIndex(COLUMN_NAME))
                dataList.add(QueryModel(id, name))
            }
        }

        cursor?.close()
        db.close()
        return dataList
    }

    companion object {
        private const val DATABASE_NAME = "ImageEditor"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "history"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"

        private var instance: DBHelper? = null

        @JvmStatic
        fun getInstance(context: Context): DBHelper {
            if (instance == null) {
                // Sử dụng synchronized để đảm bảo chỉ có một luồng có thể khởi tạo instance
                synchronized(DBHelper::class.java) {
                    // Kiểm tra lại instance trong synchronized block
                    if (instance == null) {
                        instance = DBHelper(context)
                    }
                }
            }
            return instance!!
        }
    }
}
