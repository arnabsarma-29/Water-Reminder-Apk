package com.personal.hydrationmonitor

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class DoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = context.getSharedPreferences("hydration", Context.MODE_PRIVATE)
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        val savedDate = prefs.getString("date", "")
        if (savedDate != today) {
            prefs.edit().putString("date", today).putInt("count", 0).apply()
        }

        var count = prefs.getInt("count", 0)
        if (count < 3) {
            count++
            prefs.edit().putInt("count", count).apply()
        }

        // Get the specific ID for this notification
        val notificationId = intent?.getIntExtra("notification_id", -1) ?: -1
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationId != -1) {
            // ONLY cancel the specific notification clicked
            manager.cancel(notificationId)
        }

        Toast.makeText(context, "Progress: $count / 3 💧", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getPendingIntent(context: Context, notificationId: Int): PendingIntent {
            val intent = Intent(context, DoneReceiver::class.java).apply {
                putExtra("notification_id", notificationId)
            }
            return PendingIntent.getBroadcast(
                context,
                notificationId, // Use the ID as request code to keep intents unique
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}