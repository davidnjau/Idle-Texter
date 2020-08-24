package com.centafrique.textsender.Helperclass;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

public class getContacts {

    private String phoneName = "";

    public String getContactName(Context context, String phoneNumber){

        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> namePhone = new ArrayList<>();

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {

                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        nameList.add(name);
                        namePhone.add(phoneNo);
//
//                        Log.i("-*name-* ", "Name: " + name);
//                        Log.i("-*phone-* ", "Phone Number: " + phoneNo);

                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }



//        Log.e("*-*-*phone ", String.valueOf(namePhone));

        return phoneName;
    }

}
