package com.centafrique.textsender.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.centafrique.textsender.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminPage extends AppCompatActivity {

    private EditText etMpesaCode, etAmount;
    private Button btnActivatePlan;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        recyclerView = findViewById(R.id.recyclerView);

        etAmount = findViewById(R.id.etAmount);
        etMpesaCode = findViewById(R.id.etMpesaCode);
        btnActivatePlan = findViewById(R.id.btnActivatePlan);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference().child("payments");

        btnActivatePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtMpesaCode = etMpesaCode.getText().toString();
                String txtAmount = etAmount.getText().toString();

                if (!TextUtils.isEmpty(txtMpesaCode) && !TextUtils.isEmpty(txtAmount)){

                    DatabaseReference newPost = myRef.child(txtMpesaCode);
                    newPost.child("mpesa_code").setValue(txtMpesaCode);
                    newPost.child("amount").setValue(txtAmount);


                }else {

                    if (TextUtils.isEmpty(txtMpesaCode))etMpesaCode.setError("Enter code..");
                    if (TextUtils.isEmpty(txtAmount))etAmount.setError("Enter Amount..");
                }

            }
        });

        findViewById(R.id.btnMainPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(intent);

            }
        });
    }

    private void getData(){



    }
}