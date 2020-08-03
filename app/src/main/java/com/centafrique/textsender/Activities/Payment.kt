package com.centafrique.textsender.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.centafrique.textsender.R

class Payment : AppCompatActivity() {

    private lateinit var btnActivatePlan: Button
    private lateinit var etMpesaCode: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        btnActivatePlan = findViewById(R.id.btnActivatePlan)
        etMpesaCode = findViewById(R.id.etMpesaCode)

        btnActivatePlan.setOnClickListener {

            val mpesaCode = etMpesaCode.text
            if (!TextUtils.isEmpty(mpesaCode)){

                val intent = Intent(this, Main2Activity::class.java)
                startActivity(intent)

            }else{

                etMpesaCode.error = "You cannot proceed without payment."

            }

        }

    }
}