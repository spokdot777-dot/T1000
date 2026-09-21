package com.aura.ai.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.aura.ai.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_TITLE = "t1000_reminder_title"
        const val EXTRA_DESCRIPTION = "t1000_reminder_description"
        private const val CHANNEL_ID = "t1000_reminders"
        private const val CHANNEL_NAME = "T1000 Reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "T1000 Reminder"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION).orEmpty()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                ),
            )
        }

        val tapIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(description.ifBlank { "Reminder from T1000" })
            .setAutoCancel(true)
            .setContentIntent(tapIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
