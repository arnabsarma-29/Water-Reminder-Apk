package com.personal.hydrationmonitor
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val requestCode = intent?.getIntExtra("request_code", -1) ?: -1
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (requestCode == 104) {
            // Midnight Reset Logic
            val prefs = context.getSharedPreferences("hydration", Context.MODE_PRIVATE)
            val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            prefs.edit().putString("date", today).putInt("count", 0).apply()
            manager.cancelAll()
        } else {
            // Show Notification Logic
            val notificationId = System.currentTimeMillis().toInt()
            val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                .setContentTitle("💧 Drink Water")
                .setContentText("Stay hydrated!")
                .setSmallIcon(R.drawable.waterdrop)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(0, "Mark as Done", DoneReceiver.getPendingIntent(context, notificationId))
                .build()

            manager.notify(notificationId, notification)
        }

        // Re-schedule for tomorrow
        AlarmScheduler.schedule(context)
    }
}