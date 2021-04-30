package com.centafrique.textsender.roomPersitence.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.centafrique.textsender.roomPersitence.Dao.IdleTexter
import com.centafrique.textsender.roomPersitence.Tables.Errors


@Database(
        entities = [
           Errors::class
        ],
        version = 1,
        exportSchema = false
)

public abstract class Database : RoomDatabase() {

    abstract fun idleTexter() : IdleTexter

    companion object{

        @Volatile
        private var INSTANCE : com.centafrique.textsender.roomPersitence.Database.Database? = null

        fun getDatabase(context: Context): com.centafrique.textsender.roomPersitence.Database.Database{

            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){

                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        com.centafrique.textsender.roomPersitence.Database.Database::class.java,
                        "idle_texter"
                ).build()
                INSTANCE = instance
                return instance

            }

        }

    }

}