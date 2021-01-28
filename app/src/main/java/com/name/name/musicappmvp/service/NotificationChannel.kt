package com.name.name.musicappmvp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.name.name.musicappmvp.R
import com.name.name.musicappmvp.ultis.ChannelEntity

class NotificationChannel {
    fun songNotification(context: Context, tilted: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ChannelEntity.NOTIFY_ID.toString(),
                context.getString(R.string.notify_playing_state),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val serviceNotification =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            serviceNotification.createNotificationChannel(channel)
        }
        val notificationBuilder = NotificationCompat.Builder(
            context,
            ChannelEntity.NOTIFY_ID.toString()
        )
        notification1 = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_music_note)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(context.getString(R.string.notify_playing_state))
            .setContentText(tilted)
            .build()
    }

    companion object {
        var notification1: Notification? = null
    }
}
