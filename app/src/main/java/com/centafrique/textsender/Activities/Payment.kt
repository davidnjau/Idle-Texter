package com.centafrique.textsender.Activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        btnActivatePlan = findViewById(R.id.btnActivatePlan)
        etMpesaCode = findViewById(R.id.etMpesaCode)

        progressDialog = ProgressDialog(this)

        sharedPreferences = applicationContext.getSharedPreferences("payments", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val sms = sharedPreferences.getString("sms", null)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("payments")

        btnActivatePlan.setOnClickListener {

            progressDialog.setTitle("Please wait..")
            progressDialog.setMessage("Processing payment..")
            progressDialog.setCanceledOnTouchOutside(false)

            val mpesaCode = etMpesaCode.text.toString()
            if (!TextUtils.isEmpty(mpesaCode)){

                progressDialog.show()

                myRef.orderByChild("mpesa_code").equalTo(mpesaCode)
                        .addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.exists()){

                            for (ds in p0.children) {

                                val amount = ds.child("amount").getValue(String::class.java)
                                val usage = ds.child("usage").getValue(String::class.java)

                                if (usage == "inactive") {

                                    editor.putString("sms", amount)
                                    editor.apply()

                                    //exists
                                    val intent = Intent(this@Payment, Main2Activity::class.java)
                                    startActivity(intent)

                                }else{

                                    Toast.makeText(this@Payment, "The M-PESA code is already in use by another person", Toast.LENGTH_LONG).show()
                                    progressDialog.dismiss()

                                }

                            }


                        }else{

                            progressDialog.dismiss()
                            Toast.makeText(this@Payment, "Please wait while we update the payment. Try later on", Toast.LENGTH_LONG).show()

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