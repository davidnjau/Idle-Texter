package com.centafrique.textsender.messagelistener

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.centafrique.textsender.Adapter.MessagesAdapter
import com.centafrique.textsender.Database.DatabaseHelper
import com.centafrique.textsender.Helperclass.MessageClass
import com.centafrique.textsender.R
import java.util.*

class MpesaDetails : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var mpesaClassArrayList: ArrayList<MpesaCodeClass>
    private lateinit var messagesAdapter: MpesaCodeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mpesa_details)

        recyclerView = findViewById(R.id.recyclerView)
        databaseHelper = DatabaseHelper(applicationContext)

    }

    override fun onStart() {
        super.onStart()

        mpesaClassArrayList = databaseHelper.getMpesa()
        messagesAdapter = MpesaCodeAdapter(applicationContext, mpesaClassArrayList)
        layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = messagesAdapter

    }
}