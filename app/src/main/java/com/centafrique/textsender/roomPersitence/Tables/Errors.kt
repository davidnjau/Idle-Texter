package com.centafrique.textsender.roomPersitence.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "error")
data class Errors (

    val dateTime : String,
    val error : String,
    val contactDetails : String,
    val importantMessage : String,
    val submitted : Boolean
){
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null
}