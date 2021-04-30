package com.centafrique.textsender.Helperclass

import android.content.Context
import android.database.Cursor
import com.centafrique.textsender.Database.DatabaseHelper

class FetchDatabase {

    private var isThere: Boolean = false

    private var phoneId: String = "0"
    private lateinit var MessagesIds : String
    private lateinit var MessagesStatus : String
    private lateinit var dbMessage : String
    private var SendMessage : String = "false"

    private lateinit var databaseHelper: DatabaseHelper

//    private val myListMessageIds: ArrayList<String> = ArrayList()
    private val myListMessageIds: MutableList<String> = ArrayList()

    fun CheckPhoneNumber (number : String, time:String, context: Context): Pair<String, Boolean> {

        databaseHelper = DatabaseHelper(context)

        val db = databaseHelper.readableDatabase

//        val cursor: Cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_MISSED_CALLS
//                + " WHERE " + DatabaseHelper.KEY_PHONE_NUMBER + "='" + number + "'", null)

        val cursor: Cursor = db.rawQuery("SELECT * FROM "
                + DatabaseHelper.TABLE_MISSED_CALLS + " WHERE "
                + DatabaseHelper.KEY_PHONE_NUMBER + "=?" + " AND "
                + DatabaseHelper.KEY_TIME + "=?", arrayOf(number, time))

        if (cursor != null){

            if (cursor.moveToFirst()) {
                do {

                    isThere = true

                    phoneId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ID))

                } while (cursor.moveToNext())
            }
            cursor.close()

        }


        return Pair(phoneId, isThere)

    }

    fun CheckMessageStatus(messageId : String, context: Context){

        databaseHelper = DatabaseHelper(context)

        databaseHelper.updateStatus("true", messageId)

        val db = databaseHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_MESSAGES, null)

        if (cursor != null){

            if (cursor.moveToFirst()){

                do {

                    MessagesIds = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ID))

                    if (messageId != MessagesIds){

                        myListMessageIds.add(MessagesIds)
                    }


                }while (cursor.moveToNext())

            }
            cursor.close()

            for (ids in myListMessageIds){

                databaseHelper.updateStatus("false", ids)

            }



        }

    }


    fun getMessage(context: Context) : String{

        databaseHelper = DatabaseHelper(context)

        val db = databaseHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("select * from " + "${DatabaseHelper.TABLE_MESSAGES}", null)

        if (cursor != null){

            if (cursor.moveToFirst()){

                do {

                    MessagesStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_STATUS))
                    dbMessage = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_MESSAGES))

                    if (MessagesStatus == "true"){

                        SendMessage = dbMessage

                    }


                }while (cursor.moveToNext())

            }
            cursor.close()

        }

        return SendMessage

    }



}