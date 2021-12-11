package com.lumertz.cripto

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class CryptoTransactionDao(
    private val context: Context
) {
    val databaseHelper = DataBaseHelper(context)

    fun insert(cryptoTransaction: CryptoTransaction): String {
        val db = databaseHelper.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(CRYPTO_TRANSACTIONS_NAME, cryptoTransaction.name)
        contentValues.put(CRYPTO_TRANSACTIONS_CODE, cryptoTransaction.code)
        contentValues.put(CRYPTO_TRANSACTIONS_AMOUNT, cryptoTransaction.amount)
        contentValues.put(CRYPTO_TRANSACTIONS_VALUE, cryptoTransaction.value)
        contentValues.put(CRYPTO_TRANSACTIONS_TOTAL, cryptoTransaction.total)
        contentValues.put(CRYPTO_TRANSACTIONS_DATE, cryptoTransaction.date)

        var responseId = db.insert(TABLE_CRYPTO_TRANSACTIONS, null, contentValues)
        val msg = if (responseId != -1L) {
            "Inserido com sucesso"
        } else {
            "Erro ao inserir"
        }

        db.close()
        return msg
    }

    fun getAverageAll(): ArrayList<CryptoTransactionAverage> {
        val db = databaseHelper.writableDatabase
        val sql = "SELECT $CRYPTO_TRANSACTIONS_CODE, SUM($CRYPTO_TRANSACTIONS_AMOUNT) as AMOUNT, AVG($CRYPTO_TRANSACTIONS_VALUE) as AVERAGE\n" +
                "FROM $TABLE_CRYPTO_TRANSACTIONS\n" +
                "GROUP BY $CRYPTO_TRANSACTIONS_CODE\n" +
                "ORDER BY SUM($CRYPTO_TRANSACTIONS_ID) DESC"

        val cursor = db.rawQuery(sql ,null)
        val cryptoTransactions =ArrayList<CryptoTransactionAverage>()
        while (cursor.moveToNext()){
            val cryptoTransactionAverage = cryptoTransactionFrom(cursor)
            cryptoTransactions.add(cryptoTransactionAverage)
        }
        cursor.close()
        db.close()

        return cryptoTransactions
    }

    @SuppressLint("Range")
    private fun cryptoTransactionFrom(cursor: Cursor): CryptoTransactionAverage {
        val code = cursor.getString(cursor.getColumnIndex(CRYPTO_TRANSACTIONS_CODE))
        val amount = cursor.getDouble(cursor.getColumnIndex("AMOUNT"))
        val average = cursor.getDouble(cursor.getColumnIndex("AVERAGE"))
        return CryptoTransactionAverage(code, amount, average)
    }

    @SuppressLint("Range")
    fun getTotal(): Double {
        val db = databaseHelper.writableDatabase
        val sql = "SELECT SUM($CRYPTO_TRANSACTIONS_VALUE) as TOTAL FROM $TABLE_CRYPTO_TRANSACTIONS"

        val cursor = db.rawQuery(sql ,null)

        var total = 0.0

        while (cursor.moveToNext()){
            total = cursor.getDouble(cursor.getColumnIndex("TOTAL"))
        }

        cursor.close()
        db.close()

        return total
    }

    fun getAllByCode(code: String): ArrayList<CryptoTransaction> {
        val db = databaseHelper.writableDatabase
        val sql = "SELECT * FROM $TABLE_CRYPTO_TRANSACTIONS WHERE $CRYPTO_TRANSACTIONS_CODE = ?"

        val cursor = db.rawQuery(sql , arrayOf(code))
        val cryptoTransactions = ArrayList<CryptoTransaction>()
        while (cursor.moveToNext()){
            val cryptoTransactionAverage = cryptoTransaction(cursor)
            cryptoTransactions.add(cryptoTransactionAverage)
        }
        cursor.close()
        db.close()

        return cryptoTransactions
    }

    @SuppressLint("Range")
    private fun cryptoTransaction(cursor: Cursor): CryptoTransaction {
        val id = cursor.getInt(cursor.getColumnIndex(CRYPTO_TRANSACTIONS_ID))
        val name = cursor.getString(cursor.getColumnIndex(CRYPTO_TRANSACTIONS_NAME))
        val code = cursor.getString(cursor.getColumnIndex(CRYPTO_TRANSACTIONS_CODE))
        val date = cursor.getString(cursor.getColumnIndex(CRYPTO_TRANSACTIONS_DATE))
        val amount = cursor.getDouble(cursor.getColumnIndex(CRYPTO_TRANSACTIONS_AMOUNT))
        val value = cursor.getDouble(cursor.getColumnIndex(CRYPTO_TRANSACTIONS_VALUE))
        val total = cursor.getDouble(cursor.getColumnIndex(CRYPTO_TRANSACTIONS_TOTAL))

        return CryptoTransaction(id, name, code, amount, value, total, date)
    }
}
