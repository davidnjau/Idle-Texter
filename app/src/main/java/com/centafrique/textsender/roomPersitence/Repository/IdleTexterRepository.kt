package com.centafrique.textsender.roomPersitence.Repository

import com.centafrique.textsender.roomPersitence.Dao.IdleTexter
import com.centafrique.textsender.roomPersitence.Tables.Errors

class IdleTexterRepository(private val idleTexter: IdleTexter) {

    suspend fun getErrors(): List<Errors>{
        return idleTexter.getErrors(false)
    }

    suspend fun insertError(errors: Errors){
        idleTexter.addErrors(errors)
    }
//    suspend fun insertError1(errorMessage: String){
//        idleTexter.addErrors(errorMessage)
//    }

    suspend fun deleteError(id:Int){
        idleTexter.deleteError(id)
    }

}