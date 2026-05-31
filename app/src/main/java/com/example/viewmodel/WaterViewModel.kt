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

    // The base daily hydration goal (stored in SharedPreferences)
    private val _hydrationGoal = MutableStateFlow(sharedPrefs.getInt("hydration_goal", 2500))
    val hydrationGoal: StateFlow<Int> = _hydrationGoal.asStateFlow()

    // Meteorological summer hot-weather scaling setting (+500ml)
    private val _isHotWeatherEnabled = MutableStateFlow(sharedPrefs.getBoolean("hot_weather_enabled", false))
    val isHotWeatherEnabled: StateFlow<Boolean> = _isHotWeatherEnabled.asStateFlow()

    fun toggleHotWeather(enabled: Boolean) {
        _isHotWeatherEnabled.value = enabled
        sharedPrefs.edit().putBoolean("hot_weather_enabled", enabled).apply()
    }

    // Biological and environmental smart target triggers
    private val _isCoffeeTaxEnabled = MutableStateFlow(sharedPrefs.getBoolean("coffee_tax_enabled", false))
    val isCoffeeTaxEnabled = _isCoffeeTaxEnabled.asStateFlow()

    private val _isAltitudeBoosterEnabled = MutableStateFlow(sharedPrefs.getBoolean("altitude_booster_enabled", false))
    val isAltitudeBoosterEnabled = _isAltitudeBoosterEnabled.asStateFlow()

    private val _isSodiumSaltTaxEnabled = MutableStateFlow(sharedPrefs.getBoolean("sodium_salt_tax_enabled", false))
    val isSodiumSaltTaxEnabled = _isSodiumSaltTaxEnabled.asStateFlow()

    private val _isPregnancyModeEnabled = MutableStateFlow(sharedPrefs.getBoolean("pregnancy_mode_enabled", false))
    val isPregnancyModeEnabled = _isPregnancyModeEnabled.asStateFlow()

    private val _isIllnessRecoveryEnabled = MutableStateFlow(sharedPrefs.getBoolean("illness_recovery_enabled", false))
    val isIllnessRecoveryEnabled = _isIllnessRecoveryEnabled.asStateFlow()

    fun toggleCoffeeTax(enabled: Boolean) {
        _isCoffeeTaxEnabled.value = enabled
        sharedPrefs.edit().putBoolean("coffee_tax_enabled", enabled).apply()
    }

    fun toggleAltitudeBooster(enabled: Boolean) {
        _isAltitudeBoosterEnabled.value = enabled
        sharedPrefs.edit().putBoolean("altitude_booster_enabled", enabled).apply()
    }

    fun toggleSodiumSaltTax(enabled: Boolean) {
        _isSodiumSaltTaxEnabled.value = enabled
        sharedPrefs.edit().putBoolean("sodium_salt_tax_enabled", enabled).apply()
    }

    fun togglePregnancyMode(enabled: Boolean) {
        _isPregnancyModeEnabled.value = enabled
        sharedPrefs.edit().putBoolean("pregnancy_mode_enabled", enabled).apply()
    }

    fun toggleIllnessRecovery(enabled: Boolean) {
        _isIllnessRecoveryEnabled.value = enabled
        sharedPrefs.edit().putBoolean("illness_recovery_enabled", enabled).apply()
    }

    // Effective goal combined dynamically based on weather adjustments
    val effectiveGoal: StateFlow<Int> = combine(
        _hydrationGoal,
        _isHotWeatherEnabled,
        _isCoffeeTaxEnabled,
        _isAltitudeBoosterEnabled
    ) { base, isHot, isCoffee, isAlt ->
        var goalVal = base
        if (isHot) goalVal += 500
        if (isCoffee) goalVal += 250
        if (isAlt) goalVal += 300
        goalVal
    }.combine(_isSodiumSaltTaxEnabled) { current, enabled ->
        if (enabled) current + 300 else current
    }.combine(_isPregnancyModeEnabled) { current, enabled ->
        if (enabled) current + 600 else current
    }.combine(_isIllnessRecoveryEnabled) { current, enabled ->
        if (enabled) current + 400 else current
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2500)

    // coach personality (Section 5)
    enum class CoachPersonality {
        ZEN_MASTER, WETNESS_INSPECTOR, DRILL_SERGEANT, GLADRY, MEDIEVAL_CRIER, PHILOSOPHER, SOMMELIER, GRANDMA_CARE
    }

    private val _coachPersonality = MutableStateFlow(
        sharedPrefs.getString("coach_personality", "ZEN_MASTER")?.let {
            try { CoachPersonality.valueOf(it) } catch (e: Exception) { CoachPersonality.ZEN_MASTER }
        } ?: CoachPersonality.ZEN_MASTER
    )
    val coachPersonality: StateFlow<CoachPersonality> = _coachPersonality.asStateFlow()

    fun changeCoachPersonality(personality: CoachPersonality) {
        _coachPersonality.value = personality
        sharedPrefs.edit().putString("coach_personality", personality.name).apply()
    }

    // Active Customization configs (The active theme, custom glass silhouette, etc.)
    private val _activeBottleTheme = MutableStateFlow(sharedPrefs.getString("active_bottle_theme", "Deep Blue") ?: "Deep Blue")
    val activeBottleTheme = _activeBottleTheme.asStateFlow()

    fun updateBottleTheme(theme: String) {
        _activeBottleTheme.value = theme
        sharedPrefs.edit().putString("active_bottle_theme", theme).apply()
    }

    private val _customVesselSilhouette = MutableStateFlow(sharedPrefs.getString("custom_vessel_silhouette", "Glass Jar") ?: "Glass Jar")
    val customVesselSilhouette = _customVesselSilhouette.asStateFlow()

    fun updateVesselSilhouette(silhouette: String) {
        _customVesselSilhouette.value = silhouette
        sharedPrefs.edit().putString("custom_vessel_silhouette", silhouette).apply()
    }

    private val _glassFrosting = MutableStateFlow(sharedPrefs.getFloat("glass_frosting", 0.7f))
    val glassFrosting = _glassFrosting.asStateFlow()

    fun updateGlassFrosting(value: Float) {
        _glassFrosting.value = value
        sharedPrefs.edit().putFloat("glass_frosting", value).apply()
    }

    private val _isLavaLampEnabled = MutableStateFlow(sharedPrefs.getBoolean("lava_lamp_enabled", false))
    val isLavaLampEnabled = _isLavaLampEnabled.asStateFlow()

    fun toggleLavaLamp(enabled: Boolean) {
        _isLavaLampEnabled.value = enabled
        sharedPrefs.edit().putBoolean("lava_lamp_enabled", enabled).apply()
    }

    private val _isRaindropsEnabled = MutableStateFlow(sharedPrefs.getBoolean("raindrops_enabled", false))
    val isRaindropsEnabled = _isRaindropsEnabled.asStateFlow()

    fun toggleRaindrops(enabled: Boolean) {
        _isRaindropsEnabled.value = enabled
        sharedPrefs.edit().putBoolean("raindrops_enabled", enabled).apply()
    }

    private val _isCoralForestEnabled = MutableStateFlow(sharedPrefs.getBoolean("coral_forest_enabled", false))
    val isCoralForestEnabled = _isCoralForestEnabled.asStateFlow()

    fun toggleCoralForest(enabled: Boolean) {
        _isCoralForestEnabled.value = enabled
        sharedPrefs.edit().putBoolean("coral_forest_enabled", enabled).apply()
    }

    private val _activeSticker = MutableStateFlow(sharedPrefs.getString("active_sticker", "None 🚫") ?: "None 🚫")
    val activeSticker = _activeSticker.asStateFlow()

    fun updateActiveSticker(sticker: String) {
        _activeSticker.value = sticker
        sharedPrefs.edit().putString("active_sticker", sticker).apply()
    }

    // RPG Avatar variables
    private val _rpgLevel = MutableStateFlow(sharedPrefs.getInt("rpg_level", 1))
    val rpgLevel = _rpgLevel.asStateFlow()

    private val _rpgXp = MutableStateFlow(sharedPrefs.getInt("rpg_xp", 0))
    val rpgXp = _rpgXp.asStateFlow()

    private val _rpgStrength = MutableStateFlow(sharedPrefs.getInt("rpg_strength", 10))
    val rpgStrength = _rpgStrength.asStateFlow()

    private val _rpgIntellect = MutableStateFlow(sharedPrefs.getInt("rpg_intellect", 10))
    val rpgIntellect = _rpgIntellect.asStateFlow()

    private val _rpgAgility = MutableStateFlow(sharedPrefs.getInt("rpg_agility", 10))
    val rpgAgility = _rpgAgility.asStateFlow()

    fun feedRpgAvatarWater(ml: Int) {
        val xpGain = ml / 10
        val nextXp = _rpgXp.value + xpGain
        val neededXp = _rpgLevel.value * 100
        if (nextXp >= neededXp) {
            _rpgLevel.value += 1
            _rpgXp.value = nextXp - neededXp
            _rpgStrength.value += 3
            _rpgIntellect.value += 2
            _rpgAgility.value += 2
        } else {
            _rpgXp.value = nextXp
        }
        val editor = sharedPrefs.edit()
        editor.putInt("rpg_level", _rpgLevel.value)
        editor.putInt("rpg_xp", _rpgXp.value)
        editor.putInt("rpg_strength", _rpgStrength.value)
        editor.putInt("rpg_intellect", _rpgIntellect.value)
        editor.putInt("rpg_agility", _rpgAgility.value)
        editor.apply()
    }

    // Sound customization
    private val _binauralChordFrequency = MutableStateFlow(sharedPrefs.getFloat("binaural_chord_frequency", 432f))
    val binauralChordFrequency = _binauralChordFrequency.asStateFlow()

    fun updateBinauralFrequency(freq: Float) {
        _binauralChordFrequency.value = freq
        sharedPrefs.edit().putFloat("binaural_chord_frequency", freq).apply()
    }

    private val _gulpTempo = MutableStateFlow(sharedPrefs.getFloat("gulp_tempo", 1.0f))
    val gulpTempo = _gulpTempo.asStateFlow()

    fun updateGulpTempo(tempo: Float) {
        _gulpTempo.value = tempo
        sharedPrefs.edit().putFloat("gulp_tempo", tempo).apply()
    }

    // Smart Tech configurations Override Morse Vibration toggle
    private val _isMorseVibrationEnabled = MutableStateFlow(sharedPrefs.getBoolean("morse_vibration_enabled", false))
    val isMorseVibrationEnabled = _isMorseVibrationEnabled.asStateFlow()

    fun toggleMorseVibration(enabled: Boolean) {
        _isMorseVibrationEnabled.value = enabled
        sharedPrefs.edit().putBoolean("morse_vibration_enabled", enabled).apply()
    }

    // Hydration Streak Flame-Glow calculation
    val streakCount: StateFlow<Int> = combine(repository.allLogs, effectiveGoal) { logs, goalVal ->
        calculateStreak(logs, goalVal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun calculateStreak(logs: List<WaterLog>, targetGoal: Int): Int {
        if (logs.isEmpty()) return 0
        val dailySums = logs.groupBy { it.dayString }
            .mapValues { entry -> entry.value.sumOf { it.ml } }
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(Date())
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val yesterdayStr = sdf.format(yesterdayCal.time)
        
        val metToday = (dailySums[todayStr] ?: 0) >= targetGoal
        val metYesterday = (dailySums[yesterdayStr] ?: 0) >= targetGoal
        
        if (!metToday && !metYesterday) return 0
        
        var streak = 0
        val checkCal = Calendar.getInstance()
        if (!metToday && metYesterday) {
            checkCal.add(Calendar.DATE, -1)
        }
        
        while (true) {
            val checkStr = sdf.format(checkCal.time)
            val sum = dailySums[checkStr] ?: 0
            if (sum >= targetGoal) {
                streak++
                checkCal.add(Calendar.DATE, -1)
            } else {
                break
            }
        }
        return streak
    }

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
            
            // Gain RPG level/XP on logging water
            feedRpgAvatarWater(ml)
            
            // Play physical tactile pouring/drinking acoustic cues
            try {
                if (ml <= 180) {
                    com.example.ui.WaterSoundSynthesizer.playGulp()
                } else if (ml <= 300) {
                    com.example.ui.WaterSoundSynthesizer.playPouring()
                } else {
                    com.example.ui.WaterSoundSynthesizer.playSplash()
                }
            } catch (e: Exception) {
                // Ignore in case of pure Unit Test runs lacking audio hardware
            }
            
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

    fun clearAllLogsForSelectedDay() {
        viewModelScope.launch {
            val logs = logsForSelectedDay.value
            logs.forEach { log ->
                repository.deleteLog(log)
            }
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
