package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ml: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val dayString: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
)
