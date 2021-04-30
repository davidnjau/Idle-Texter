package com.centafrique.textsender;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.centafrique.textsender.Helperclass.EncryptData;

public class MySmsReceiver extends BroadcastReceiver {

    public static final String pdu_type = "pdus";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strMessage = "";
        String format = bundle.getString("format");

        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);

        if (pdus != null) {
            // Check the Android version.
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            // Fill the msgs array.
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                // Build the message to show.
                strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                strMessage += " :" + msgs[i].getMessageBody() + "\n";

                strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                strMessage += " :" + msgs[i].getMessageBody() + "\n";
                // Log and display the SMS message.
                Log.e("-**- ", "onReceive: " + strMessage);
                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();

//                String messageBody = msgs[i].getMessageBody();
//                String address = msgs[i].getOriginatingAddress();
//                String encryptedMessage = "";
//                try {
//                    encryptedMessage = EncryptData.encrypt(messageBody);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                Log.e("-*-*-*2", messageBody);
//                Log.e("-*-*-*3", encryptedMessage);
//                Log.e("-*-*-*4", address);

            }
        }else {
            Log.e("-*-*-*5", "address");

        }

    }
}