package com.centafrique.textsender.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.centafrique.textsender.Adapter.MessagesAdapter;
import com.centafrique.textsender.Database.DatabaseHelper;
import com.centafrique.textsender.Helperclass.MessageClass;
import com.centafrique.textsender.R;
import com.centafrique.textsender.messagelistener.MpesaDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class FragmentMessages extends Fragment {

    private SharedPreferences preferences;
    private String txtSoapId;

    private boolean isGood = true;

    public FragmentMessages(){

    }

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseHelper databaseHelper;

    private LinearLayout myview;

    private ArrayList<MessageClass> messageClassArrayList;
    private MessagesAdapter messagesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        myview = view.findViewById(R.id.myview);
        databaseHelper = new DatabaseHelper(getActivity());

        FloatingActionButton addTextMessages = view.findViewById(R.id.addTextMessages);
        addTextMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputDialog();

            }
        });

        FloatingActionButton mpesaText = view.findViewById(R.id.mpesaText);
        addTextMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), MpesaDetails.class));

            }
        });



        return view;
    }

    private void InputDialog() {

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.update_messages, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setView(promptsView);
        final EditText etInformation = promptsView.findViewById(R.id.etInformation);
        final TextView tvTitle = promptsView.findViewById(R.id.tvTitle);

        tvTitle.setText("Add new Message");

        final AlertDialog alertDialog = alertDialogBuilder.create();

        Button btn_Action = promptsView.findViewById(R.id.btn_Action);
        Button btn_Cancel = promptsView.findViewById(R.id.btn_Cancel);

        btn_Action.setText("Add");

        btn_Action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtInfo = etInformation.getText().toString();
                databaseHelper.addPredefinedMessage(txtInfo);

                Toast.makeText(getActivity(), "Message added successfully.", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

                StartRecyclerView();

//
//                IngredientsPojo ingredientsPojo = IngredientsPojoArrayList.get(position);
//                ingredientsPojo.setNotes(txtInfo);
//                notifyItemChanged(position);


            }
        });

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });


        alertDialog.show();


    }


    @Override
    public void onStart() {
        super.onStart();

        StartRecyclerView();

    }


    public void StartRecyclerView() {

        messageClassArrayList = databaseHelper.getMessages();
        messagesAdapter = new MessagesAdapter(getActivity(), messageClassArrayList );
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messagesAdapter);

        if (messageClassArrayList.size() == 0){

            myview.setVisibility(View.VISIBLE);
        }else {

            myview.setVisibility(View.GONE);

        }


    }



}
