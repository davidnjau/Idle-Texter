package com.centafrique.textsender.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.centafrique.textsender.Helperclass.MessageClass
import com.centafrique.textsender.Helperclass.MissedCallsClass
import com.centafrique.textsender.messagelistener.MpesaCodeClass

class DatabaseHelper(context: Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)  {

    companion object{

        val DATABASE_VERSION = 1
        val DATABASE_NAME = "Calls_Idle_Manager"

        val TABLE_MISSED_CALLS = "missed_calls"

        val KEY_ID = "id"

        val KEY_PHONE_NAME = "phone_name"

        val KEY_PHONE_NUMBER = "phone_number"
        val KEY_NUMBER_CALLS = "calls_number"
        val KEY_TIME = "time"

        val TABLE_MESSAGES = "messages"

        val KEY_MESSAGES = "predefined_messages"
        val KEY_STATUS = "status"

        val TABLE_MPESA_MESSAGES = "mpesa_messages"

        val KEY_MPESA_CODE = "mpesa_code"
        val KEY_MPESA_AMOUNT = "mpesa_amount"

        var numberOfCalls : Int = 1

    }


    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE_MISSED_CALLS = ("CREATE TABLE " + TABLE_MISSED_CALLS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_PHONE_NUMBER + " TEXT,"
                + KEY_PHONE_NAME + " TEXT,"
                + KEY_NUMBER_CALLS + " TEXT, "
                + KEY_TIME + " TEXT" + ")")

        val CREATE_TABLE_MESSAGES = ("CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_STATUS + " TEXT, "
                + KEY_MESSAGES + " TEXT" + ")")

        val CREATE_TABLE_MPESA_MESSAGES = ("CREATE TABLE " + TABLE_MPESA_MESSAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_MPESA_CODE + " TEXT, "
                + KEY_MPESA_AMOUNT + " TEXT" + ")")


        db?.execSQL(CREATE_TABLE_MISSED_CALLS)
        db?.execSQL(CREATE_TABLE_MESSAGES)
        db?.execSQL(CREATE_TABLE_MPESA_MESSAGES)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_MISSED_CALLS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MPESA_MESSAGES")
        onCreate(db)

    }



    fun addMissedCall(number : String, time : String, name: String = "new"){

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_PHONE_NUMBER, number)
        contentValues.put(KEY_PHONE_NAME, name)
        contentValues.put(KEY_TIME, time)
        contentValues.put(KEY_NUMBER_CALLS, "1")

        Log.e("-*-*- ", number)

        db.insert(TABLE_MISSED_CALLS, null, contentValues)
        db.close()

    }

    fun updateMissedCall(time: String, id: String){

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TIME, time)
        contentValues.put(KEY_NUMBER_CALLS, getCallNumber(id) +1)

        db.update(TABLE_MISSED_CALLS, contentValues, "id=$id",null )
        db.close()

    }

    fun deleteCall(id: String){

        val db = this.writableDatabase
        // Deleting Row
        db.delete(TABLE_MISSED_CALLS, "id=$id",null)
        db.close()
    }

    fun addPredefinedMessage(message : String){

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_MESSAGES, message)
        contentValues.put(KEY_STATUS, "false")

        db.insert(TABLE_MESSAGES, null, contentValues)
        db.close()

    }

    fun addMpesaDetails(mpesaCode : String, mpesaAmount : String ){

        val db = this.writableDatabase
        val contentValues = ContentValues()

        val query = "Select * From $TABLE_MPESA_MESSAGES WHERE $KEY_MPESA_CODE = '$mpesaCode'"

        if (getDataCount(query)!!.count > 0){


        }else{

            contentValues.put(KEY_MPESA_CODE, mpesaCode)
            contentValues.put(KEY_MPESA_AMOUNT, mpesaAmount)

            db.insert(TABLE_MPESA_MESSAGES, null, contentValues)
            db.close()
        }



    }

    private fun getDataCount(sql: String?): Cursor? {
        val database = readableDatabase
        return database.rawQuery(sql, null)
    }


    fun deleteMessage(id: String){

        val db = this.writableDatabase
        // Deleting Row
        db.delete(TABLE_MESSAGES, "id=$id",null)
        db.close()
    }

    fun updateMessage(message : String, id: String){

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_MESSAGES, message)

        db.update(TABLE_MESSAGES, cv, "id=$id",null )
        db.close()

    }

    fun updateStatus(status: String, id: String){

        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(KEY_STATUS, status)
        db.update(TABLE_MESSAGES, cv, "id=$id",null )
        db.close()


    }

    fun getCallNumber(id: String):Int{

        val selectQuery = "SELECT * FROM $TABLE_MISSED_CALLS WHERE $KEY_ID = $id"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        cursor = db.rawQuery(selectQuery, null)

        if (cursor != null){

            if (cursor.moveToFirst()) {
                do {

                    numberOfCalls = cursor.getInt(cursor.getColumnIndex(KEY_NUMBER_CALLS))

                } while (cursor.moveToNext())
            }
            cursor.close()

        }

        return numberOfCalls
    }

    fun getMissedCalls():ArrayList<MissedCallsClass>{
        val empList: ArrayList<MissedCallsClass> = ArrayList<MissedCallsClass>()
        val selectQuery = "SELECT  * FROM $TABLE_MISSED_CALLS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var userId: String
        var userPhoneNumber: String
        var userPhoneName: String
        var userCallTime: String
        var userCallNumber: String

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    userId = cursor.getString(cursor.getColumnIndex(KEY_ID))
                    userPhoneNumber = cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER))
                    userPhoneName = cursor.getString(cursor.getColumnIndex(KEY_PHONE_NAME))

                    val name = cursor.getString(cursor.getColumnIndex(KEY_PHONE_NAME))
                    Log.e("*-*-*- ", name)


                    userCallTime = cursor.getString(cursor.getColumnIndex(KEY_TIME))
                    userCallNumber = cursor.getString(cursor.getColumnIndex(KEY_NUMBER_CALLS))

                    val missedCalls = MissedCallsClass(userId = userId, userPhoneNumber = userPhoneNumber,
                            userPhoneName = userPhoneName, time = userCallTime, call_number = userCallNumber)

                    empList.add(missedCalls)

                } while (cursor.moveToNext())
            }
            cursor.close()

        }
        return empList
    }

    fun getMessages():ArrayList<MessageClass>{
        val empList: ArrayList<MessageClass> = ArrayList<MessageClass>()
        val selectQuery = "SELECT  * FROM $TABLE_MESSAGES"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var messageId: String
        var message: String
        var status: String


        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {

                    messageId = cursor.getString(cursor.getColumnIndex(KEY_ID))
                    message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGES))
                    status = cursor.getString(cursor.getColumnIndex(KEY_STATUS))

                    val messages = MessageClass(messageId = messageId, message = message, status = status)

                    empList.add(messages)

                } while (cursor.moveToNext())
            }
            cursor.close()

        }
        return empList
    }

    fun getUpdatedMessages(messageId : String):String{

        val selectQuery = "SELECT * FROM $TABLE_MESSAGES WHERE $KEY_ID = $messageId"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ""
        }
        var messageId: String
        var message: String = ""
        var status: String


        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {

                    messageId = cursor.getString(cursor.getColumnIndex(KEY_ID))
                    message = cursor.getString(cursor.getColumnIndex(KEY_MESSAGES))
                    status = cursor.getString(cursor.getColumnIndex(KEY_STATUS))


                } while (cursor.moveToNext())
            }
            cursor.close()

        }
        return message
    }

    fun getMpesa():ArrayList<MpesaCodeClass>{
        val empList: ArrayList<MpesaCodeClass> = ArrayList<MpesaCodeClass>()
        val selectQuery = "SELECT  * FROM $TABLE_MPESA_MESSAGES"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var mpesaCode: String
        var mpesaAmount: String


        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {

                    mpesaCode = cursor.getString(cursor.getColumnIndex(KEY_MPESA_CODE))
                    mpesaAmount = cursor.getString(cursor.getColumnIndex(KEY_MPESA_AMOUNT))

                    val messages = MpesaCodeClass(mpesaCode = mpesaCode, mpesaAmount = mpesaAmount)

                    empList.add(messages)

                } while (cursor.moveToNext())
            }
            cursor.close()

        }
        return empList
    }

    fun getCount() : Long{

        val db = this.readableDatabase
        val selectQuery = TABLE_MISSED_CALLS

        val long  = DatabaseUtils.queryNumEntries(db, selectQuery)
        return long

    }


}