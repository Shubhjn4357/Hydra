package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.WaterDatabase
import com.example.data.WaterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        
        // Use a coroutine on Dispatchers.IO to check the database before posting notifications
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = WaterDatabase.getDatabase(context)
                val repository = WaterRepository(database.waterLogDao)
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                
                // Get today's total
                val todayTotal = repository.getTodayTotal(today).firstOrNull() ?: 0
                
                // Let's assume a default target or read customized user preferences.
                // To keep database simple, we can store user goals in SharedPreferences.
                val sharedPrefs = context.getSharedPreferences("water_logger_prefs", Context.MODE_PRIVATE)
                val targetGoal = sharedPrefs.getInt("hydration_goal", 2500)
                val totalIntake = todayTotal ?: 0

                if (totalIntake < targetGoal) {
                    // Send notification since goal is not met yet
                    showNotification(context, targetGoal - totalIntake)
                }
            } catch (e: Exception) {
                // Fallback to sending a friendly reminder if something fails
                showNotification(context, null)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, remainingMl: Int?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "water_intake_reminder_channel"
        val channelName = "Hydration Reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Periodic reminders to drink water during the day"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Action to open MainActivity
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = if (remainingMl != null && remainingMl > 0) {
            "You still need ${remainingMl}ml of water to hit your daily goal! Tap to log. 💧"
        } else {
            "Time to take a sip! Stay healthy and hydrated. 💧"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Standard system icon, clean and safe
            .setContentTitle("Hydration Alert 🚰")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(777, notification)
    }
}
