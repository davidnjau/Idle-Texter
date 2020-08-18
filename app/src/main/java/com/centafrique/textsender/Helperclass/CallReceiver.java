package com.centafrique.textsender.Helperclass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.centafrique.textsender.Database.DatabaseHelper;
import com.centafrique.textsender.R;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static android.content.ContentValues.TAG;


public class CallReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    private String txtToSend;
    ITelephony telephonyService;

    private DatabaseHelper databaseHelper;
    private SimpleDateFormat dateTimeFormatter1,dateTimeFormatter;

    FetchDatabase fetchDatabase;
    private SharedPreferences sharedPreferences;
    private String sms;
    private int smsNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.w("intent " , intent.getAction().toString());

        sharedPreferences = context.getSharedPreferences("payments", Context.MODE_PRIVATE);
        sms = sharedPreferences.getString("sms", null);


        if (sms != null){

            smsNumber = Integer.parseInt(sms);

            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

            } else {

                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                onCallStateChanged(context, state, number);
            }

        }else {

            Log.e("-*-*-* ", "nope");

        }


    }


    public void onCallStateChanged(final Context context, int state, String number) {

        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;

                Toast.makeText(context, "Incoming Call Ringing" , Toast.LENGTH_SHORT).show();

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
//                    Toast.makeText(context, "Outgoing Call Started" , Toast.LENGTH_SHORT).show();
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            StringBuffer stringBuffer = new StringBuffer();

                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
                                    == PackageManager.PERMISSION_GRANTED) {

                                String[] projection = new String[]{CallLog.Calls.NUMBER};
                                Cursor cur = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                                        projection, null, null, CallLog.Calls.DATE +" desc");
                                cur.moveToFirst();

                                String lastCallnumber = cur.getString(0);

                                if (lastCallnumber != null){

                                    try{

                                        dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                        dateTimeFormatter1 = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date = new Date();
                                        databaseHelper = new DatabaseHelper(context);
                                        fetchDatabase = new FetchDatabase();

                                        if (databaseHelper.getCount() < smsNumber){

                                            Log.e("--*-* " , String.valueOf(databaseHelper.getCount()));

                                            String txtMessage = fetchDatabase.getMessage(context);
                                            if (txtMessage.equals("false")){

                                                txtToSend = context.getString(R.string.send_text_default) + "\n \nVia Idle Texter";


                                            }else {

                                                txtToSend = txtMessage + "\n \nVia Idle Texter";

                                            }

                                            String currentTime = dateTimeFormatter1.format(date);

                                            boolean isThere = fetchDatabase.CheckPhoneNumber(lastCallnumber,currentTime, context).getSecond();
                                            String txtId = fetchDatabase.CheckPhoneNumber(lastCallnumber,currentTime, context).getFirst();

                                            if (isThere){

                                                databaseHelper.updateMissedCall(currentTime, txtId);
                                                Toast.makeText(context, "SMS not Sent for this number", Toast.LENGTH_SHORT).show();

                                            }else {

                                                SmsManager smgr = SmsManager.getDefault();
                                                smgr.sendTextMessage(lastCallnumber,null,txtToSend,null,null);

                                                Log.e("-*-*-* ", txtToSend);

                                                Toast.makeText(context, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();

                                                databaseHelper.addMissedCall(lastCallnumber, currentTime);

                                            }

                                        }else{

                                            Toast.makeText(context, "Call the mobile developer for more details", Toast.LENGTH_LONG).show();

                                        }



                                    }
                                    catch (Exception e){

                                        Log.e("*-*-*- " , e.toString());
                                        Toast.makeText(context, "SMS Failed to Send to " + lastCallnumber, Toast.LENGTH_SHORT).show();
                                    }

                                }


                            }else {
                                Toast.makeText(context, "Not ", Toast.LENGTH_LONG).show();

                            }


                        }
                    }, 500);


                } else if(isIncoming){

//                    Toast.makeText(context, "Incoming " + savedNumber + " Call time " + callStartTime  , Toast.LENGTH_SHORT).show();

                } else{

//                    Toast.makeText(context, "outgoing " + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();

                }

                break;
        }
        lastState = state;
    }
}