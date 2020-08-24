package com.centafrique.textsender.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.centafrique.textsender.Database.DatabaseHelper;
import com.centafrique.textsender.Helperclass.MessageClass;
import com.centafrique.textsender.R;

import java.util.ArrayList;

public class MessagesJavaAdapter extends RecyclerView.Adapter<MessagesJavaAdapter.ViewHolder>{


    private Context context;
    private ArrayList<MessageClass> planningCommentsArrayList;

    private DatabaseHelper databaseHelper;

    public MessagesJavaAdapter(Context context, ArrayList<MessageClass> planningCommentsArrayList) {
        this.context = context;
        this.planningCommentsArrayList = planningCommentsArrayList;
    }

    @NonNull
    @Override
    public MessagesJavaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages, parent, false);
        ViewHolder holder = new ViewHolder(view);

        databaseHelper = new DatabaseHelper(context);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String messages = planningCommentsArrayList.get(position).getMessage();
        final String messageId = planningCommentsArrayList.get(position).getMessageId();
        String messageStatus = planningCommentsArrayList.get(position).getStatus();

        holder.tvMessages.setText(messages);

        holder.imgBtnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputDialog(context, messages, messageId);

            }
        });

    }

    private void InputDialog(Context context, String messages, String messageId) {




    }


    @Override
    public int getItemCount() {

        return planningCommentsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvMessages;
        ImageButton imgBtnSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessages = itemView.findViewById(R.id.tvMessages);
            imgBtnSelected = itemView.findViewById(R.id.imgBtnSelected);


        }
    }


}
