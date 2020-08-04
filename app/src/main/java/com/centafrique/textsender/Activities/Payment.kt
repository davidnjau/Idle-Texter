package com.centafrique.textsender.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.centafrique.textsender.R
import com.google.firebase.FirebaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Payment : AppCompatActivity() {

    private lateinit var btnActivatePlan: Button
    private lateinit var etMpesaCode: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        btnActivatePlan = findViewById(R.id.btnActivatePlan)
        etMpesaCode = findViewById(R.id.etMpesaCode)

        sharedPreferences = applicationContext.getSharedPreferences("payments", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val sms = sharedPreferences.getString("sms", null)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("payments")

        btnActivatePlan.setOnClickListener {

            val mpesaCode = etMpesaCode.text.toString()
            if (!TextUtils.isEmpty(mpesaCode)){

                myRef.orderByChild("mpesa_code").equalTo(mpesaCode)
                        .addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.exists()){

                            //exists
                            val intent = Intent(this@Payment, Main2Activity::class.java)
                            startActivity(intent)

                            for (ds in p0.children) {

                                val amount = ds.child("amount").getValue(String::class.java)
                                editor.putString("sms", amount)
                                editor.apply()

                            }

                            Log.e("-*-*-* ", "exists")

                        }else{

                            //doesn't exist
                            Log.e("-*-*-* ", "doesn't exist")

                        }

                    }

                })


            }else{

                etMpesaCode.error = "You cannot proceed without payment."

            }

        }

    }
}