package com.example.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.WaterDatabase
import com.example.data.WaterLog
import com.example.data.WaterRepository
import com.example.receiver.WaterReminderReceiver
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val database = WaterDatabase.getDatabase(application)
    private val repository = WaterRepository(database.waterLogDao)
    private val sharedPrefs = application.getSharedPreferences("water_logger_prefs", Context.MODE_PRIVATE)

    // Current Date Calendar context for time sync
    private val _selectedCalendar = MutableStateFlow(Calendar.getInstance())
    val selectedCalendar: StateFlow<Calendar> = _selectedCalendar.asStateFlow()

    // Date formatted strings
    val selectedDateString: StateFlow<String> = _selectedCalendar.map { calendar ->
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), getFormattedDate(Date()))

    val selectedDateLabel: StateFlow<String> = _selectedCalendar.map { calendar ->
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        
        when {
            isSameDay(calendar, today) -> "Today"
            isSameDay(calendar, yesterday) -> "Yesterday"
            else -> SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()).format(calendar.time)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Today")

    // The daily hydration goal (stored in SharedPreferences)
    private val _hydrationGoal = MutableStateFlow(sharedPrefs.getInt("hydration_goal", 2500))
    val hydrationGoal: StateFlow<Int> = _hydrationGoal.asStateFlow()

    // Logs for the currently selected day
    val logsForSelectedDay: StateFlow<List<WaterLog>> = selectedDateString.flatMapLatest { dayStr ->
        repository.getLogsForDay(dayStr)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sum of logged water for the selected day
    val totalIntakeForDay: StateFlow<Int> = logsForSelectedDay.map { logs ->
        logs.sumOf { it.ml }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Reminders settings (Enabled state & Interval hours)
    private val _remindersEnabled = MutableStateFlow(sharedPrefs.getBoolean("reminders_enabled", false))
    val remindersEnabled: StateFlow<Boolean> = _remindersEnabled.asStateFlow()

    private val _reminderIntervalHours = MutableStateFlow(sharedPrefs.getInt("reminder_interval_hours", 2))
    val reminderIntervalHours: StateFlow<Int> = _reminderIntervalHours.asStateFlow()

    // Static/Explicit Light-Dark Theme system setting (stores null = system-match, false = force light, true = force dark)
    private val _themeOverride = MutableStateFlow<Boolean?>(
        if (sharedPrefs.contains("theme_override")) {
            sharedPrefs.getBoolean("theme_override", false)
        } else {
            null
        }
    )
    val themeOverride: StateFlow<Boolean?> = _themeOverride.asStateFlow()

    fun addWaterLog(ml: Int) {
        viewModelScope.launch {
            val calendar = _selectedCalendar.value
            val logTime = Calendar.getInstance()
            // Set the logged item to correspond to the selected calendar day
            logTime.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            logTime.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            logTime.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
            
            val dayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(logTime.time)
            val log = WaterLog(
                ml = ml,
                timestamp = logTime.timeInMillis,
                dayString = dayStr
            )
            repository.insertLog(log)
            
            // Re-sync alert scheduling
            if (_remindersEnabled.value) {
                scheduleReminder()
            }
        }
    }

    fun deleteWaterLog(log: WaterLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
            // Re-sync alerts
            if (_remindersEnabled.value) {
                scheduleReminder()
            }
        }
    }

    fun changeGoal(newGoal: Int) {
        if (newGoal <= 0) return
        _hydrationGoal.value = newGoal
        sharedPrefs.edit().putInt("hydration_goal", newGoal).apply()
        
        // Re-sync alerts
        if (_remindersEnabled.value) {
            scheduleReminder()
        }
    }

    fun toggleReminders(enabled: Boolean) {
        _remindersEnabled.value = enabled
        sharedPrefs.edit().putBoolean("reminders_enabled", enabled).apply()
        if (enabled) {
            scheduleReminder()
        } else {
            cancelReminder()
        }
    }

    fun updateReminderInterval(hours: Int) {
        if (hours <= 0) return
        _reminderIntervalHours.value = hours
        sharedPrefs.edit().putInt("reminder_interval_hours", hours).apply()
        if (_remindersEnabled.value) {
            scheduleReminder()
        }
    }

    fun setThemePreference(isDark: Boolean?) {
        _themeOverride.value = isDark
        val editor = sharedPrefs.edit()
        if (isDark == null) {
            editor.remove("theme_override")
        } else {
            editor.putBoolean("theme_override", isDark)
        }
        editor.apply()
    }

    fun selectPreviousDay() {
        _selectedCalendar.update { current ->
            val prev = Calendar.getInstance().apply { time = current.time }
            prev.add(Calendar.DATE, -1)
            prev
        }
    }

    fun selectNextDay() {
        _selectedCalendar.update { current ->
            val next = Calendar.getInstance().apply { time = current.time }
            next.add(Calendar.DATE, 1)
            next
        }
    }

    fun selectToday() {
        _selectedCalendar.value = Calendar.getInstance()
    }

    // AlarmManager scheduling for background-aware reminder intervals description
    private fun scheduleReminder() {
        val context = getApplication<Application>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            123,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate trigger interval in millis
        val intervalMillis = _reminderIntervalHours.value * 60 * 60 * 1000L
        val triggerAtMillis = System.currentTimeMillis() + intervalMillis

        try {
            alarmManager.cancel(pendingIntent)
            // Use setInexactRepeating to wake up comfortably and conserve battery
            alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                triggerAtMillis,
                intervalMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            // Safe fallback logging logic
        }
    }

    private fun cancelReminder() {
        val context = getApplication<Application>()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            123,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun getFormattedDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
