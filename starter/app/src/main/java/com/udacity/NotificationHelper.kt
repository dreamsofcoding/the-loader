package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.Constants.FILE_NAME
import com.udacity.Constants.STATUS
import com.udacity.Constants.SUCCESS
import kotlinx.serialization.StringFormat


private lateinit var notificationManager: NotificationManager
private lateinit var pendingIntent: PendingIntent
private lateinit var action: NotificationCompat.Action

object NotificationHelper {

    fun setupChannel(context: Context, channelId: String) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Download Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for download status"
        }
        notificationManager.createNotificationChannel(channel)

    }


    fun sendNotification(context: Context, fileName: String, status: String, channelId: String) {

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val detailIntent = Intent(context, DetailActivity::class.java).apply {
            putExtra(FILE_NAME, fileName)
            putExtra(STATUS, status)
        }

        pendingIntent = PendingIntent.getActivity(
            context,
            0,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(if(status == SUCCESS)R.string.notification_description_success else R.string.notification_description_failed))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_assistant_black_24dp, context.getString(R.string.notification_button), pendingIntent)

        notificationManager.notify(0, builder.build())
    }
}
