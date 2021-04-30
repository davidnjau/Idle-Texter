package com.centafrique.textsender.roomPersitence.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.centafrique.textsender.roomPersitence.Tables.Errors

@Dao
interface IdleTexter {

    @Insert
    suspend fun addErrors(errors: Errors)

    @Query("SELECT * from error WHERE submitted LIKE :isSubmitted")
    suspend fun getErrors(isSubmitted: Boolean): List<Errors>

    @Query("DELETE FROM error WHERE id =:id")
    suspend fun deleteError(id : Int)
}