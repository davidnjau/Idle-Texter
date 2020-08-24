package com.centafrique.textsender.fragments;


import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.centafrique.textsender.Adapter.MissedCallsAdapter;
import com.centafrique.textsender.Database.DatabaseHelper;
import com.centafrique.textsender.Helperclass.MissedCallsClass;
import com.centafrique.textsender.R;

import java.util.ArrayList;


public class FragmentMissedCalls extends Fragment {

    private SharedPreferences preferences;
    private String txtSoapId;

    private boolean isGood = true;
    private MissedCallsAdapter missedCallsAdapter;

    public FragmentMissedCalls(){

    }

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseHelper databaseHelper;

    private LinearLayout myview;

    private ArrayList<MissedCallsClass> missedCallsClassArrayList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_missed_calls, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        myview = view.findViewById(R.id.myview);

        databaseHelper = new DatabaseHelper(getActivity());


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        startMissedCalls();
    }

    private void startMissedCalls() {

        missedCallsClassArrayList = databaseHelper.getMissedCalls();
        missedCallsAdapter = new MissedCallsAdapter(getActivity(), missedCallsClassArrayList);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(missedCallsAdapter);

        if (missedCallsClassArrayList.size() == 0){

            myview.setVisibility(View.VISIBLE);

        }else {

            myview.setVisibility(View.GONE);
        }

//        getAllContacts(getActivity());

    }

    private void getAllContacts(Context context) {

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }


    }


}
