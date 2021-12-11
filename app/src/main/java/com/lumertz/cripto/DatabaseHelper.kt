package com.lumertz.cripto

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {

        val sql = "CREATE TABLE $TABLE_CRYPTO_TRANSACTIONS ($CRYPTO_TRANSACTIONS_ID  INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, $CRYPTO_TRANSACTIONS_NAME TEXT,$CRYPTO_TRANSACTIONS_CODE TEXT,$CRYPTO_TRANSACTIONS_AMOUNT DOUBLE, $CRYPTO_TRANSACTIONS_VALUE DOUBLE, $CRYPTO_TRANSACTIONS_TOTAL DOUBLE, $CRYPTO_TRANSACTIONS_DATE TEXT)"
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME)
        onCreate(db)
    }
}