package com.personal.hydrationmonitor

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationHelper.createChannel(this)
        AlarmScheduler.schedule(this)

        requestPermission()
        checkDateAndClearNotifications() // Check for day-change on app open
        updateProgress()

        findViewById<Button>(R.id.btnDone).setOnClickListener { markDone() }
        findViewById<Button>(R.id.btnReset).setOnClickListener { resetDay() }
    }

    override fun onResume() {
        super.onResume()
        checkDateAndClearNotifications()
        updateProgress()
    }

    private fun checkDateAndClearNotifications() {
        val prefs = getSharedPreferences("hydration", MODE_PRIVATE)
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val savedDate = prefs.getString("date", "")

        if (savedDate != today) {
            // It's a new day! Clear notifications and reset count
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancelAll()
            prefs.edit().putString("date", today).putInt("count", 0).apply()
        }
    }

    private fun updateProgress() {
        val prefs = getSharedPreferences("hydration", MODE_PRIVATE)
        val count = prefs.getInt("count", 0)
        findViewById<TextView>(R.id.progressText).text = "$count / 3 Completed"
    }

    private fun markDone() {
        val prefs = getSharedPreferences("hydration", MODE_PRIVATE)
        var count = prefs.getInt("count", 0)
        if (count < 3) {
            count++
            prefs.edit().putInt("count", count).apply()
        }
        updateProgress()
    }

    private fun resetDay() {
        val prefs = getSharedPreferences("hydration", MODE_PRIVATE)
        prefs.edit().putInt("count", 0).apply()

        // Clear all active notifications when manual reset is clicked
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()

        updateProgress()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }
}