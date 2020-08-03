package com.centafrique.textsender.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.centafrique.textsender.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Register extends AppCompatActivity {

    private EditText etEmailAddress, etPhoneNUmber, etPassword;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference();

        etEmailAddress = findViewById(R.id.etEmailAddress);
        etPhoneNUmber = findViewById(R.id.etPhoneNUmber);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Registration on going.");
                progressDialog.setMessage("Please wait as you're being registered.");
                progressDialog.setCanceledOnTouchOutside(false);

                String txtEmail = etEmailAddress.getText().toString();
                String txtPhoneNumber = etPhoneNUmber.getText().toString();
                String txtPassword = etPassword.getText().toString();

                if (!TextUtils.isEmpty(txtEmail)
                        && !TextUtils.isEmpty(txtPhoneNumber) && txtPhoneNumber.length() >= 10
                        && !TextUtils.isEmpty(txtPassword) && txtPassword.length() > 6){

                    progressDialog.show();
                    createUser(txtEmail, txtPassword, txtPhoneNumber);

                }else {

                    progressDialog.dismiss();

                    if (TextUtils.isEmpty(txtEmail))etEmailAddress.setError("Email address cannot be empty..");
                    if (TextUtils.isEmpty(txtPhoneNumber))etPhoneNUmber.setError("Phone number cannot be empty..");
                    if (TextUtils.isEmpty(txtPassword))etPassword.setError("Password cannot be empty..");
                    if (txtPassword.length() <= 6)
                        Toast.makeText(Register.this, "Enter as stronger password.", Toast.LENGTH_SHORT).show();
                    if (txtPhoneNumber.length() <10)
                        Toast.makeText(Register.this, "Enter a valid phone number.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void createUser(final String txtEmail, String txtPassword, final String txtPhoneNumber) {

        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    DatabaseReference newPost = databaseReference.child("users").child(uid);
                    newPost.child("uid").setValue(uid);
                    newPost.child("email").setValue(txtEmail);
                    newPost.child("phone_number").setValue(txtPhoneNumber);

                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), Payment.class);
                    startActivity(intent);
                    finish();

                }else {
                    progressDialog.dismiss();
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Register.this, "Authentication failed. Try again",
                            Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){

            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
            startActivity(intent);
            finish();

        }

    }
}