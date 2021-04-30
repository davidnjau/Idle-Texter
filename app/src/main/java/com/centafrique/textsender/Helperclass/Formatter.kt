package com.centafrique.textsender.Helperclass

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.Html
import androidx.core.app.NotificationCompat
import com.centafrique.textsender.R

class Formatter {

    fun formatNumber(number: String): String  {

        var finalReversedNo = ""

        if (number.length >= 9){

            var number2 = number.toLong()

            var num = number2
            var reversed = 0L
            while (num != 0L) {
                val digit = num % 10
                reversed = reversed * 10 + digit
                num /= 10
            }

            var reversedNo = reversed.toString().substring(0, 9).toLong()
            var reversedFinal = 0L
            while (reversedNo != 0L) {
                val digit = reversedNo % 10
                reversedFinal = reversedFinal * 10 + digit
                reversedNo /= 10
            }

            finalReversedNo = "0$reversedFinal"

        }else{

            finalReversedNo = "invalid phone number"

        }



        return finalReversedNo.toString()
    }

    fun createNotification(
            context: Context
    ) {

        val NOTIFICATION_ID = "1".toInt()
        val CHANNEL_ID = "my_channel_01"
        val name: CharSequence = "uptech"

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = "Sms sending message"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(false)
            channel.setShowBadge(false)
            notificationManager.createNotificationChannel(channel)
        }



        val builder = NotificationCompat.Builder(context, CHANNEL_ID)

        builder.setSmallIcon(R.mipmap.ic_launcher_icon)

        builder.setContentTitle("Sms sending message")

        val orderData = "David (+254716060198) had tried to call you. Kindly call back."

        builder.setContentText(orderData)
        builder.setStyle(
                NotificationCompat.BigTextStyle()
                        .bigText(orderData)
        )


        notificationManager.notify(NOTIFICATION_ID, builder.build())

    }
}