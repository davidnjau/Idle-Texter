package com.centafrique.textsender.Helperclass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.centafrique.textsender.Activities.PaymentNew;
import com.centafrique.textsender.Database.DatabaseHelper;
import com.centafrique.textsender.R;
import com.centafrique.textsender.SMSUtils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


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
    private String phoneName;
    private SharedPreferences preferences;


    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.w("intent " , intent.getAction().toString());

        sharedPreferences = context.getSharedPreferences("payments", Context.MODE_PRIVATE);
        sms = sharedPreferences.getString("sms", null);
        preferences = context.getSharedPreferences("Texts", MODE_PRIVATE);


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


        }


    }


    public void onCallStateChanged(final Context context, int state, final String number) {

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
                                String[] projection1 = new String[]{CallLog.Calls.CACHED_NAME};

                                Cursor cur = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                                        projection, null, null, CallLog.Calls.DATE +" desc");
                                Cursor cur1 = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                                        projection1, null, null, CallLog.Calls.DATE +" desc");

                                assert cur != null;
                                assert cur1 != null;

                                cur.moveToFirst();
                                cur1.moveToFirst();

                                String lastCallnumber = cur.getString(0);
                                String lastCall = "";

                                if (cur1.getString(0) != null){
                                    lastCall = cur1.getString(0);
                                }else {
                                    lastCall = "";
                                }


                                if (lastCallnumber != null){

                                    try{

                                        dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                        dateTimeFormatter1 = new SimpleDateFormat("dd/MM/yyyy");

                                        Date date = new Date();
                                        databaseHelper = new DatabaseHelper(context);
                                        fetchDatabase = new FetchDatabase();

                                        String tag = context.getResources().getString(R.string.hyperlink);
                                        String currentTime = dateTimeFormatter1.format(date);

                                        boolean isThere = fetchDatabase.CheckPhoneNumber(lastCallnumber,currentTime, context).getSecond();
                                        String txtId = fetchDatabase.CheckPhoneNumber(lastCallnumber,currentTime, context).getFirst();

                                        String txtMessage = fetchDatabase.getMessage(context);
                                        if (txtMessage.equals("false")){

                                            txtToSend = context.getString(R.string.send_text_default);
//                                            txtToSend = context.getString(R.string.send_text_default) + "\n \nIdleTexter Info: "+tag;

                                        }else {

                                            txtToSend = txtMessage;
//                                            txtToSend = txtMessage +  "\n \nIdleTexter Info: "+tag;

                                        }

                                        if (isThere){
                                            databaseHelper.updateMissedCall(currentTime, txtId);
                                        }else {

                                            SharedPreferences.Editor editor = preferences.edit();

                                            editor.putString("timeDetails",currentTime);
                                            editor.putString("contactDetails",lastCallnumber + "\n" + lastCall);
                                            editor.apply();

                                            SMSUtils.sendSMS(context, lastCallnumber,lastCall, txtToSend, currentTime);
                                        }


//                                        if (databaseHelper.getCount() < smsNumber){
//
//                                            String txtMessage = fetchDatabase.getMessage(context);
//                                            if (txtMessage.equals("false")){
//
//                                                txtToSend = context.getString(R.string.send_text_default) + "\n \nIdleTexter Info: "+tag;
//
//                                            }else {
//
//                                                txtToSend = txtMessage +  "\n \nIdleTexter Info: "+tag;
//
//                                            }
//
//                                            boolean isThere = fetchDatabase.CheckPhoneNumber(lastCallnumber,currentTime, context).getSecond();
//                                            String txtId = fetchDatabase.CheckPhoneNumber(lastCallnumber,currentTime, context).getFirst();
//
//                                            if (isThere){
//
//                                                databaseHelper.updateMissedCall(currentTime, txtId);
//                                                Toast.makeText(context, "SMS not Sent for this number", Toast.LENGTH_SHORT).show();
//
//                                            }else {
//
//
//                                                try{
//
////                                                    SmsManager smgr = SmsManager.getDefault();
////                                                    smgr.sendTextMessage(lastCallnumber,null,txtToSend,null,null);
////                                                    System.out.println("-*-*-*-* "+ lastCallnumber);
////                                                    Toast.makeText(context, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
//
//                                                    SMSUtils.sendSMS(context, lastCallnumber,lastCall, txtToSend, currentTime);
//
//                                                }catch (Exception e){
//                                                    e.printStackTrace();
//                                                }
//
//                                            }
//
//                                        }else{
//
//                                            addNotification(context);
//
//                                            Toast.makeText(context, "Call the mobile developer for more details", Toast.LENGTH_LONG).show();
//
//                                        }


                                    }
                                    catch (Exception e){

                                        Toast.makeText(context, "SMS Failed to Send to " + lastCallnumber, Toast.LENGTH_SHORT).show();
                                    }

                                }


                            }else {
                                Toast.makeText(context, "Please allow permissions ", Toast.LENGTH_LONG).show();

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

    private void addNotification(Context context) {

        int NOTIFICATION_ID = Integer.parseInt("1");
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String description = "This is my channel";
        String txtTitle = "Idle Texter";

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            channel.setShowBadge(false);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            builder.setSmallIcon(R.mipmap.ic_icon_logo);

        }else {

            builder.setSmallIcon(R.mipmap.ic_icon_logo);
        }

        builder.setContentTitle(txtTitle);
        builder.setContentText("You have exhausted your sms plan.\nGo to the payment page to get more plans.");

        Intent intent = new Intent(context, PaymentNew.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PaymentNew.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

}