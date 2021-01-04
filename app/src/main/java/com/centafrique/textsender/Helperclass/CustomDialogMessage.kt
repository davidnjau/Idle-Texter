package com.centafrique.textsender.Helperclass

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.centafrique.textsender.R


private lateinit var tvMessage: TextView

fun CustomDialogMessage(a: Activity, txtMessage: String) {
    val layoutInflater = a.layoutInflater
    val layout: View = layoutInflater.inflate(R.layout.custom_dialog,
            a.findViewById<View>(R.id.custom_toast_layout_id) as ViewGroup)


    tvMessage = layout.findViewById(R.id.tvMessage)
    tvMessage.text = txtMessage
    val toast = Toast(a)
    toast.duration = Toast.LENGTH_LONG
    toast.view = layout //setting the view of custom toast layout
    toast.show()
}