package com.centafrique.textsender.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.centafrique.textsender.Database.DatabaseHelper
import com.centafrique.textsender.Helperclass.FetchDatabase
import com.centafrique.textsender.Helperclass.MessageClass
import com.centafrique.textsender.R

class MessagesAdapter(private val context: Context, private val messagesList : ArrayList<MessageClass>)
    : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return MessagesViewHolder(inflater, parent)

    }



    override fun getItemCount() = messagesList.size

    fun refreshData() {

        notifyDataSetChanged()

    }
    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {

        val messages : MessageClass = messagesList[position]
        holder.Assign(messages, context)


    }

    class MessagesViewHolder(inflater: LayoutInflater, parent: ViewGroup):
            RecyclerView.ViewHolder(inflater.inflate(R.layout.messages, parent, false)){

        private var tvMessages : TextView? = null
        private var imgBtnSelected : ImageButton? = null

        init {

            tvMessages = itemView.findViewById(R.id.tvMessages)
            imgBtnSelected = itemView.findViewById(R.id.imgBtnSelected)

        }

        fun Assign(messageClass: MessageClass, context: Context){

            val status : String = messageClass.status

            if (status == "false"){
                imgBtnSelected?.setBackgroundResource(R.drawable.ic_action_unselected)
            }else{
                imgBtnSelected?.setBackgroundResource(R.drawable.ic_action_selected)
            }

            tvMessages?.text = messageClass.message

            imgBtnSelected?.setOnClickListener {

                val fetchDatabase = FetchDatabase()
                fetchDatabase.CheckMessageStatus(messageClass.messageId, context)

                Toast.makeText(context, "This will be the message that will be sent.", Toast.LENGTH_SHORT).show()

            }

            itemView.setOnClickListener {

                InputDialog(context, tvMessages?.text.toString(), messageClass.messageId)

            }

        }



        private fun InputDialog(context: Context, text1: String, messageId: String) {

            val databaseHelper : DatabaseHelper = DatabaseHelper(context)

            val li = LayoutInflater.from(context)
            val promptsView = li.inflate(R.layout.update_messages, null)
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setView(promptsView)

            val etInformation = promptsView.findViewById<EditText>(R.id.etInformation)
            val tvTitle = promptsView.findViewById<TextView>(R.id.tvTitle)

            val imgBtnDelete = promptsView.findViewById<ImageButton>(R.id.imgBtnDelete)
            imgBtnDelete.visibility = View.VISIBLE

            tvTitle.text = "Update Message"
            etInformation.setText(text1)

            val alertDialog = alertDialogBuilder.create()

            val btn_Action = promptsView.findViewById<Button>(R.id.btn_Action)
            val btn_Cancel = promptsView.findViewById<Button>(R.id.btn_Cancel)

            imgBtnDelete.setOnClickListener {

                databaseHelper.deleteMessage(messageId)
                alertDialog.dismiss()

                Toast.makeText(context, "Message deleted successfully. Refresh to see the results", Toast.LENGTH_SHORT).show()


            }

            btn_Action.text = "Update"
            btn_Action.setOnClickListener {


                val txtInfo = etInformation.text.toString()
                databaseHelper.updateMessage(txtInfo, messageId)


                Toast.makeText(context, "Message updated successfully. Refresh to see the results", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()

            }
            btn_Cancel.setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }



    }


}