package com.centafrique.textsender.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.centafrique.textsender.Database.DatabaseHelper
import com.centafrique.textsender.Helperclass.MissedCallsClass
import com.centafrique.textsender.Helperclass.getContacts
import com.centafrique.textsender.R

class MissedCallsAdapter(private val context: Context, private val missedCallsList : ArrayList<MissedCallsClass>)
    : RecyclerView.Adapter<MissedCallsAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissedCallsAdapter.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)

    }

    override fun getItemCount() = missedCallsList.size

    override fun onBindViewHolder(holder: MissedCallsAdapter.ViewHolder, position: Int) {

        val missedCallsClass : MissedCallsClass = missedCallsList[position]

        holder.Assign(missedCallsClass, context)


    }

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.missed_calls, parent, false)){

        private var tvNumber : TextView? = null
        private var tvTime : TextView? = null
        private var tvCallNumber : TextView? = null
        private var tabMode : TableLayout? = null

        private var LayoutCall : LinearLayout? = null
        private var layoutDelete : LinearLayout? = null

        init {

            tvNumber = itemView.findViewById(R.id.tvNumber)
            tvTime = itemView.findViewById(R.id.tvTime)
            tvCallNumber = itemView.findViewById(R.id.tvCallNumber)

            tabMode = itemView.findViewById(R.id.tabMode)

            LayoutCall = itemView.findViewById(R.id.LayoutCall)
            layoutDelete = itemView.findViewById(R.id.layoutDelete)



        }


        fun Assign(missedCallsClass: MissedCallsClass, context: Context){

            val name = missedCallsClass.userPhoneName
            val number = missedCallsClass.userPhoneNumber

            val contact = "$name  $number"

            tvNumber?.text = contact
            tvTime?.text = missedCallsClass.time
            tvCallNumber?.text = missedCallsClass.call_number

            itemView.setOnClickListener {

                if (tabMode?.visibility == View.GONE){
                    tabMode?.visibility=View.VISIBLE
                }else if (tabMode?.visibility == View.VISIBLE){
                    tabMode?.visibility=View.GONE
                }


            }

            LayoutCall?.setOnClickListener {

                val i = Intent(Intent.ACTION_DIAL)
                val p = "tel:${missedCallsClass.userPhoneNumber}"
                i.data = Uri.parse(p)
                context.startActivity(i)

            }

            layoutDelete?.setOnClickListener {

                val databaseHelper = DatabaseHelper(context)
                databaseHelper.deleteCall(missedCallsClass.userId)

                Toast.makeText(context, "Call deleted successfully. Refresh to see the results", Toast.LENGTH_SHORT).show()


            }
        }


    }





}