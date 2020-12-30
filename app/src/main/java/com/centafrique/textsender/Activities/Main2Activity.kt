package com.centafrique.textsender.Activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.centafrique.textsender.Database.DatabaseHelper
import com.centafrique.textsender.R
import com.centafrique.textsender.fragments.FragmentMessages
import com.centafrique.textsender.fragments.FragmentMissedCalls

class Main2Activity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager
    private val fragmentMessages = FragmentMessages()
    private val fragmentMissedCalls = FragmentMissedCalls()
    private lateinit var cdt: CountDownTimer
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myview : LinearLayout = findViewById(R.id.myview)
        val mainPage : RelativeLayout = findViewById(R.id.mainPage)

        val btnMissedCalls : Button = findViewById(R.id.btnMissedCalls)
        val btnMessages : Button = findViewById(R.id.btnMessages)

        sharedPreferences = applicationContext.getSharedPreferences("payments", Context.MODE_PRIVATE)
        val sms = sharedPreferences.getString("sms", null)
        editor = sharedPreferences.edit()


        val databaseHelper = DatabaseHelper(applicationContext)

        if (sms != null) {

            if (databaseHelper.getCount() >= sms.toInt()){

                val intent = Intent(this@Main2Activity, PaymentNew::class.java)
                startActivity(intent)
                finish()

                Toast.makeText(this, "Complete your payment first", Toast.LENGTH_SHORT).show()

            }else{

//                editor.putString("sms", "0")
//                editor.apply()

                myview.visibility = View.GONE
                mainPage.isEnabled = true

            }

        }else{


            val intent = Intent(this@Main2Activity, PaymentNew::class.java)
            startActivity(intent)
            finish()

            Toast.makeText(this, "Complete your payment first",
                    Toast.LENGTH_SHORT).show()

        }


        btnMissedCalls.setOnClickListener {

            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.myFragment, fragmentMissedCalls)
            fragmentTransaction.commit()

        }

        btnMessages.setOnClickListener {


            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.myFragment, fragmentMessages)
            fragmentTransaction.commit()

        }

        if (!checkAndRequestPermissions()){

            Toast.makeText(this, "There are permissions required and they have not been enabled", Toast.LENGTH_LONG).show()
        }



    }

    private fun checkAndRequestPermissions(): Boolean {

        val smsPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
        val callLogPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG)
        val phoneStatePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
        val readPhonePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)

        val listPermissionNeeded = ArrayList<String>()
        if (smsPermission != PackageManager.PERMISSION_GRANTED)listPermissionNeeded.add(android.Manifest.permission.SEND_SMS)
        if (callLogPermission != PackageManager.PERMISSION_GRANTED)listPermissionNeeded.add(android.Manifest.permission.READ_CALL_LOG)
        if (phoneStatePermission != PackageManager.PERMISSION_GRANTED)listPermissionNeeded.add(android.Manifest.permission.READ_PHONE_STATE)
        if (readPhonePermission != PackageManager.PERMISSION_GRANTED)listPermissionNeeded.add(android.Manifest.permission.READ_CONTACTS)

        if (!listPermissionNeeded.isEmpty()){

            ActivityCompat.requestPermissions(this, listPermissionNeeded.toTypedArray(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false

        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                perms[android.Manifest.permission.SEND_SMS] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.READ_CALL_LOG] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
                perms[android.Manifest.permission.READ_CONTACTS] = PackageManager.PERMISSION_GRANTED

                if (grantResults.size > 0) {

                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    if (perms[android.Manifest.permission.SEND_SMS] == PackageManager.PERMISSION_GRANTED
                            && perms[android.Manifest.permission.READ_CALL_LOG] == PackageManager.PERMISSION_GRANTED
                            && perms[android.Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED
                            && perms[android.Manifest.permission.READ_CONTACTS] == PackageManager.PERMISSION_GRANTED
                    ){

                        Toast.makeText(applicationContext, "All permissions granted", Toast.LENGTH_SHORT).show()
                    }else{

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS )
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CALL_LOG)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)
                        ){
                            showDialogOK("These permissions are required for the app to work.",
                            DialogInterface.OnClickListener { dialog, which ->
                                when(which){

                                    DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                    DialogInterface.BUTTON_NEGATIVE -> finish()
                                }
                            })


                        }else{

                            explain("The app needs these permissions to work and cannot proceed without them. Do you want to go to settings?")
                        }



                    }

                }


            }

        }

    }

    override fun onStart() {
        super.onStart()

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.myFragment, fragmentMissedCalls)
        fragmentTransaction.commit()
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun explain(msg: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                    //  permissionsclass.requestPermission(type,code);
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.centafrique.textsender")))
                }
                .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> finish() }
        dialog.show()
    }

    companion object{

        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    }
}
