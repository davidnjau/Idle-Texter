package com.centafrique.textsender;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.centafrique.textsender.Activities.PaymentNew;
import com.centafrique.textsender.Database.DatabaseHelper;
import com.centafrique.textsender.Helperclass.FetchDatabase;
import com.centafrique.textsender.Helperclass.Formatter;
import com.centafrique.textsender.roomPersitence.Tables.Errors;
import com.centafrique.textsender.roomPersitence.ViewModel.IdleTexterViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SMSUtils extends BroadcastReceiver {

    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        preferences = context.getSharedPreferences("Texts", MODE_PRIVATE);
        String message1 = "";

        //Detect l'envoie de sms
        if (intent.getAction().equals(SENT_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK: // Sms sent
                    message1 = "SMS sent";
                    Toast.makeText(context, message1, Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: // generic failure
                case SmsManager.RESULT_ERROR_NULL_PDU: // null pdu
                    message1 = "Generic failure";
                    Toast.makeText(context, message1, Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: // No service
                    message1 = "No service";
                    Toast.makeText(context, message1, Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: //Radio off
                    message1 = "Null PDU";
                    Toast.makeText(context, message1, Toast.LENGTH_LONG).show();
                    break;
            }

            String contactDetails = preferences.getString("contactDetails", null);
            String timeDetails = preferences.getString("timeDetails", null);

            Errors errors = new Errors(timeDetails,message1,contactDetails,"",false);
            IdleTexterViewModel idleTexterViewModel = new IdleTexterViewModel((Application) context.getApplicationContext());
            idleTexterViewModel.insertError(errors);

        }
        //detect la reception d'un sms
        else if (intent.getAction().equals(DELIVERED_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_LONG).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public static boolean canSendSMS(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public static void sendSMS(final Context context, String phoneNumber, String phoneName, String message, String currentTime) {

        Formatter formatter = new Formatter();
        String checkNo = formatter.formatNumber(phoneNumber);

        if (!checkNo.equals("0716060198")){

            if (!canSendSMS(context)) {
                Toast.makeText(context, "Cant send sms", Toast.LENGTH_LONG).show();
                return;
            }
            DatabaseHelper databaseHelper = new DatabaseHelper(context);

            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_ACTION_NAME), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_SMS_ACTION_NAME), 0);

            final SMSUtils smsUtils = new SMSUtils();
            //register for sending and delivery
            context.getApplicationContext().registerReceiver(smsUtils, new IntentFilter(SMSUtils.SENT_SMS_ACTION_NAME));
            context.getApplicationContext().registerReceiver(smsUtils, new IntentFilter(DELIVERED_SMS_ACTION_NAME));

            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);

            ArrayList<PendingIntent> sendList = new ArrayList<>();
            sendList.add(sentPI);

            ArrayList<PendingIntent> deliverList = new ArrayList<>();
            deliverList.add(deliveredPI);

            databaseHelper.addMissedCall(phoneNumber, currentTime, phoneName);

            sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);

            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); //Current hour
            if (currentHour > 15 && currentHour < 18){

            }

            //we unsubscribed in 10 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    context.unregisterReceiver(smsUtils);
                }
            }, 10000);


        }else {
            formatter.createNotification(context);
        }



    }

    private static void addNotification(Context context) {

        int NOTIFICATION_ID = Integer.parseInt("1");
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String description = "This is my channel";
        String txtTitle = "Idle Texter";

//        IdleTexterViewModel idleTexterViewModel = new IdleTexterViewModel((Application) context.getApplicationContext());
//        List<Errors> errorsList = idleTexterViewModel.getErrors();
//        String text = "";
//        StringBuilder errors = new StringBuilder();
//        for (int i = 0; i < errorsList.size(); i++){
//
//            errors.append("\n").append(errorsList.get(i).getError()).append(":").append(errorsList.get(i).getDateTime());
//
//        }

//        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//
//            int importance = NotificationManager.IMPORTANCE_LOW;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            channel.enableVibration(false);
////            channel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
//            channel.setShowBadge(false);
//            assert notificationManager != null;
//            notificationManager.createNotificationChannel(channel);
//
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
//
//        builder.setSmallIcon(R.mipmap.ic_icon_logo);
//
//        builder.setContentTitle(txtTitle);
//        builder.setContentText("Error Messages.. Send to David");
//
//        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
//        notificationIntent.setData(Uri.parse(uriText));
//        PendingIntent pi = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//        builder.setContentIntent(pi);


//        Intent intent = new Intent(context, PaymentNew.class);

//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(PaymentNew.class);
//        stackBuilder.addNextIntent(intent);

//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);


//        assert notificationManager != null;
//        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }


}