package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderCalendarSelector(
    dateLabel: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onSelectToday: () -> Unit,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousDay,
            modifier = Modifier
                .background(if (isDarkTheme) Color(0x1B1E293B) else Color(0x0F000000), CircleShape)
                .testTag("prev_day_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Day",
                tint = if (isDarkTheme) Color.White else Color(0xFF1E293B)
            )
        }

        // Clickable date text triggers simple Jump to today
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onSelectToday() }
                .testTag("date_label_clickable")
        ) {
            Text(
                text = dateLabel,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )
            if (dateLabel != "Today") {
                Text(
                    text = "Tap to jump today",
                    fontSize = 11.sp,
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        IconButton(
            onClick = onNextDay,
            modifier = Modifier
                .background(if (isDarkTheme) Color(0x1B1E293B) else Color(0x0F000000), CircleShape)
                .testTag("next_day_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Day",
                tint = if (isDarkTheme) Color.White else Color(0xFF1E293B)
            )
        }
    }
}
