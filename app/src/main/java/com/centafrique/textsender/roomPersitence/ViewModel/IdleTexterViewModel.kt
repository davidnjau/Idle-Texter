package com.centafrique.textsender.roomPersitence.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.centafrique.textsender.roomPersitence.Database.Database
import com.centafrique.textsender.roomPersitence.Repository.IdleTexterRepository
import com.centafrique.textsender.roomPersitence.Tables.Errors
import kotlinx.coroutines.*
import java.util.ArrayList

class IdleTexterViewModel(application: Application) : AndroidViewModel(application) {

    private val idleTexterRepository : IdleTexterRepository

    init {

        val idleTexter = Database.getDatabase(application).idleTexter()
        idleTexterRepository = IdleTexterRepository(idleTexter)

    }

    fun getErrors()  = runBlocking{
        idleTexterRepository.getErrors()
    }

    fun insertError(errors: Errors) = viewModelScope.launch(Dispatchers.IO){
        idleTexterRepository.insertError(errors)
    }

    fun deleteError(id:Int) = viewModelScope.launch(Dispatchers.IO) {
        idleTexterRepository.deleteError(id)
    }

//    fun insertError1(errors: Errors) = viewModelScope.launch(Dispatchers.IO){
//        val error = errors.error
//        idleTexterRepository.insertError(error)
//    }


}