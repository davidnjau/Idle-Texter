package com.centafrique.textsender.messagelistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.centafrique.textsender.Database.DatabaseHelper;

public class SimpleSmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    private Context context;

    private String message = "";

    private DatabaseHelper databaseHelper;


    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        databaseHelper = new DatabaseHelper(context);

        for(int i=0 ; i<pdus.length ; i++){

            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //Check the sender to filter messages which we require to read
            if ( sender.equals("M-PAY"))
            {
                String messageBody = smsMessage.getMessageBody();

                message = message + messageBody;
            }

        }

        String NewMessage = message.split("at")[0];
        /*
        * Mpesa code
        * Amount
        * Sender
        * number
        * date
        *
        */

        Log.e("-*-*Message ", message);

        String code = message.substring(32, 42);
        String amount = message.substring(50, 55);

        databaseHelper.addMpesaDetails(code, amount);




//        String messageCode = NewMessage.split("Confirmed")[0];

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;

    }
}
