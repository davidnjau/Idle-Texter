package com.centafrique.textsender.messagelistener

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.centafrique.textsender.R

class MpesaCodeAdapter(private val context: Context, private val mpesaCode : ArrayList<MpesaCodeClass>)
    : RecyclerView.Adapter<MpesaCodeAdapter.ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MpesaCodeAdapter.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)

    }

    override fun getItemCount() = mpesaCode.size

    override fun onBindViewHolder(holder: MpesaCodeAdapter.ViewHolder, position: Int) {

        val mpesaClass : MpesaCodeClass = mpesaCode[position]

        holder.Assign(mpesaClass, context)


    }

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.mpesa_code, parent, false)){

        private var tvMpesaMpesaCode : TextView? = null
        private var tvMpesaAmount : TextView? = null


        init {

            tvMpesaMpesaCode = itemView.findViewById(R.id.tvMpesaMpesaCode)
            tvMpesaAmount = itemView.findViewById(R.id.tvMpesaAmount)

        }


        fun Assign(mpesaClass: MpesaCodeClass, context: Context){

            tvMpesaMpesaCode?.text = mpesaClass.mpesaCode
            tvMpesaAmount?.text = mpesaClass.mpesaAmount
        }


    }




}