package com.example.prachilabtest

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ExpenseDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tripexpense.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_IMAGE_PATH = "image_path"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_EXPENSES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT NOT NULL, "
                + COLUMN_AMOUNT + " REAL NOT NULL, "
                + COLUMN_CATEGORY + " TEXT NOT NULL, "
                + COLUMN_IMAGE_PATH + " TEXT, "
                + COLUMN_TIMESTAMP + " INTEGER NOT NULL" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    fun insertExpense(expense: Expense): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, expense.title)
            put(COLUMN_AMOUNT, expense.amount)
            put(COLUMN_CATEGORY, expense.category)
            put(COLUMN_IMAGE_PATH, expense.imagePath)
            put(COLUMN_TIMESTAMP, expense.timestamp)
        }
        val id = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return id
    }

    fun getAllExpenses(): List<Expense> {
        val expenseList = mutableListOf<Expense>()
        val selectQuery = "SELECT * FROM $TABLE_EXPENSES ORDER BY $COLUMN_TIMESTAMP DESC"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)) ?: ""
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))

                expenseList.add(Expense(id, title, amount, category, imagePath, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return expenseList
    }

    fun deleteExpense(id: Long): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_EXPENSES, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    fun getTotalSpent(): Double {
        val db = readableDatabase
        val query = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES"
        val cursor = db.rawQuery(query, null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    fun getCategoryTotals(): Map<String, Double> {
        val categoryTotals = mutableMapOf<String, Double>()
        val db = readableDatabase
        val query = "SELECT $COLUMN_CATEGORY, SUM($COLUMN_AMOUNT) AS total FROM $TABLE_EXPENSES GROUP BY $COLUMN_CATEGORY"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val category = cursor.getString(0)
                val total = cursor.getDouble(1)
                categoryTotals[category] = total
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return categoryTotals
    }
}
