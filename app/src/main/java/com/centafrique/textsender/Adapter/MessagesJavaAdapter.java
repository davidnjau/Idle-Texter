package com.centafrique.textsender.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.centafrique.textsender.Database.DatabaseHelper;
import com.centafrique.textsender.Helperclass.FetchDatabase;
import com.centafrique.textsender.Helperclass.MessageClass;
import com.centafrique.textsender.R;
import com.centafrique.textsender.fragments.FragmentMessages;

import java.util.ArrayList;

public class MessagesJavaAdapter extends RecyclerView.Adapter<MessagesJavaAdapter.ViewHolder>{


    private Context context;
    private ArrayList<MessageClass> planningCommentsArrayList;

    private DatabaseHelper databaseHelper;
    private MessagesJavaAdapter messagesJavaAdapter;

    public MessagesJavaAdapter(Context context, ArrayList<MessageClass> planningCommentsArrayList,
                               MessagesJavaAdapter messagesAdapter) {
        this.context = context;
        this.planningCommentsArrayList = planningCommentsArrayList;
        this.messagesJavaAdapter = messagesAdapter;
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

        if (messageStatus.equals("false"))
            holder.imgBtnSelected.setBackgroundResource(R.drawable.ic_action_unselected);
        else
            holder.imgBtnSelected.setBackgroundResource(R.drawable.ic_action_selected);

        holder.tvMessages.setText(messages);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputDialog(context, messages, messageId, position, holder.tvMessages,holder.imgBtnSelected);

            }
        });

    }

    private void InputDialog(final Context context, String messages, final String messageId,
                             final int position, final TextView tvMessages, final ImageButton imgBtnSelected) {

        final DatabaseHelper databaseHelper = new DatabaseHelper(context);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View view =  LayoutInflater.from(context).inflate(R.layout.update_messages, null);
        alertDialogBuilder.setView(view);

        final EditText etInformation = view.findViewById(R.id.etInformation);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        ImageButton imgBtnDelete = view.findViewById(R.id.imgBtnDelete);
        Button btn_Action = view.findViewById(R.id.btn_Action);
        Button btn_Cancel = view.findViewById(R.id.btn_Cancel);
        Button btnSend = view.findViewById(R.id.btnSend);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        imgBtnDelete.setVisibility(View.VISIBLE);

        tvTitle.setText("Update Message");
        etInformation.setText(messages);

        imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseHelper.deleteMessage(messageId);
                alertDialog.dismiss();

                Toast.makeText(context, "Message deleted successfully. Refresh to see the results",
                        Toast.LENGTH_SHORT).show();

                planningCommentsArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, planningCommentsArrayList.size());

            }
        });

        btn_Action.setText("Update");
        btn_Action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtInfo = etInformation.getText().toString();
                databaseHelper.updateMessage(txtInfo, messageId);

                Toast.makeText(context, "Message updated successfully. Refresh to see the results",
                        Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

                String message = databaseHelper.getUpdatedMessages(messageId);
                tvMessages.setText(message);


            }
        });

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FetchDatabase fetchDatabase = new FetchDatabase();
                fetchDatabase.CheckMessageStatus(messageId, context);

                imgBtnSelected.setBackgroundResource(R.drawable.ic_action_selected);

                Toast.makeText(context, "This will be the message that will be sent. Refresh to see changes", Toast.LENGTH_SHORT).show();

                alertDialog.dismiss();
            }
        });

        alertDialog.show();

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

            imgBtnSelected.setBackgroundResource(R.drawable.ic_action_unselected);

        }
    }


}
