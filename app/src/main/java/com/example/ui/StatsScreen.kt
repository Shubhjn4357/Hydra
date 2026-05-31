package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WaterLog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun statsScreenView(
    totalIntake: Int,
    goal: Int,
    logs: List<WaterLog>,
    onLogWater: (Int) -> Unit,
    onShowCustomDialog: () -> Unit,
    onDeleteLog: (WaterLog) -> Unit,
    showGoalDialog: () -> Unit,
    onBackClick: () -> Unit,
    remindersEnabled: Boolean,
    intervalHours: Int,
    onToggleReminders: (Boolean) -> Unit,
    onIntervalChange: (Int) -> Unit,
    isDarkTheme: Boolean,
    onDarkThemeToggle: (Boolean) -> Unit,
    dateLabel: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val percentage = if (goal > 0) (totalIntake.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val percentText = (percentage * 100).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Beautiful unified header with back control and settings menu triggers including unified Date Selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back to Logger",
                        tint = if (isDarkTheme) Color.White else Color(0xFF2E3252),
                        modifier = Modifier.size(28.dp)
                    )
                }

                // High aesthetic calendar center selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = onPreviousDay,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Day",
                            tint = if (isDarkTheme) Color.White else Color(0xFF2E3252)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = if (isDarkTheme) Color.White else Color(0xFF2E3252),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onNextDay,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Day",
                            tint = if (isDarkTheme) Color.White else Color(0xFF2E3252)
                        )
                    }
                }

                IconButton(
                    onClick = showGoalDialog,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Goals Setting",
                        tint = if (isDarkTheme) Color.White else Color(0xFF2E3252),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Circular Glass progress component
        item {
            ElevatedHydrationProgressRing(
                percentText = percentText,
                totalIntake = totalIntake,
                goal = goal,
                isDarkTheme = isDarkTheme
            )
        }

        // Gorgeous interactive 2x2 shortcut pill grid matching phone 2 in the screenshot
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tap a containerrr to log watr! 💧🥤",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShortcutPill(
                        ml = 250,
                        label = "Water Drop",
                        icon = Icons.Filled.WaterDrop,
                        iconBg = if (isDarkTheme) Color(0x333B82F6) else Color(0xFFEFF6FF),
                        iconTint = Color(0xFF3B82F6),
                        onClick = { onLogWater(250) },
                        isDarkTheme = isDarkTheme,
                        customCardBg = if (isDarkTheme) null else Color(0xFFEFF6FF),
                        customCardBorder = if (isDarkTheme) null else Color(0xFFDBEAFE),
                        modifier = Modifier.weight(1f)
                    )
                    ShortcutPill(
                        ml = 500,
                        label = "Water Bottle",
                        icon = Icons.Filled.LocalDrink,
                        iconBg = if (isDarkTheme) Color(0x3310B981) else Color(0xFFECFDF5),
                        iconTint = Color(0xFF10B981),
                        onClick = { onLogWater(500) },
                        isDarkTheme = isDarkTheme,
                        customCardBg = if (isDarkTheme) null else Color(0xFFECFDF5),
                        customCardBorder = if (isDarkTheme) null else Color(0xFFD1FAE5),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShortcutPill(
                        ml = 180,
                        label = "Coffee Cup",
                        icon = Icons.Filled.Coffee,
                        iconBg = if (isDarkTheme) Color(0x33F59E0B) else Color(0xFFFFFBEB),
                        iconTint = Color(0xFFD97706),
                        onClick = { onLogWater(180) },
                        isDarkTheme = isDarkTheme,
                        customCardBg = if (isDarkTheme) null else Color(0xFFFFFBEB),
                        customCardBorder = if (isDarkTheme) null else Color(0xFFFEF3C7),
                        modifier = Modifier.weight(1f)
                    )
                    ShortcutPill(
                        ml = 250,
                        label = "Custom Glass",
                        icon = Icons.Filled.LocalDrink,
                        iconBg = if (isDarkTheme) Color(0x33FF6B4A) else Color(0xFFFFF2EE),
                        iconTint = Color(0xFFF97316),
                        onClick = onShowCustomDialog,
                        isDarkTheme = isDarkTheme,
                        customCardBg = if (isDarkTheme) null else Color(0xFFFFF2EE),
                        customCardBorder = if (isDarkTheme) null else Color(0xFFFFD5CC),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Daily goal controller & alarms config card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isDarkTheme) Color(0x1AFFFFFF) else Color(0x1A000000),
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0x1A1E293B) else Color(0xB3FFFFFF)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "Settings & Targets",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Daily Goal Setting Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showGoalDialog() }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Adjust,
                                contentDescription = "Goal Icon",
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Daily Intake Target",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Current: ${goal}ml",
                                    fontSize = 11.sp,
                                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit goal",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    HorizontalDivider(
                        color = if (isDarkTheme) Color(0x1AFFFFFF) else Color(0x0D000000),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Notification Reminder Switch Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (remindersEnabled) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsOff,
                                contentDescription = "Alarm Active Icon",
                                tint = if (remindersEnabled) Color(0xFF0D9488) else Color(0xFF64748B),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Hydration Reminders",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Notify every ${intervalHours}h if goal remains",
                                    fontSize = 11.sp,
                                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = remindersEnabled,
                            onCheckedChange = { onToggleReminders(it) },
                            modifier = Modifier.testTag("notification_switch")
                        )
                    }

                    if (remindersEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Reminder Interval (Hours)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF475569)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(1, 2, 3, 4).forEach { hours ->
                                FilterChip(
                                    selected = intervalHours == hours,
                                    onClick = { onIntervalChange(hours) },
                                    label = { Text("${hours}h") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF3B82F6),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = if (isDarkTheme) Color(0x1AFFFFFF) else Color(0x0D000000),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Theme overriding switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                contentDescription = "Active Theme Icon",
                                tint = if (isDarkTheme) Color(0xFFFACC15) else Color(0xFFCA8A04),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Dark Mode Override",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = if (isDarkTheme) "Dark theme active" else "Light theme active",
                                    fontSize = 11.sp,
                                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onDarkThemeToggle(it) },
                            modifier = Modifier.testTag("theme_switch")
                        )
                    }
                }
            }
        }

        // Section header for Consumption Log list
        item {
            Text(
                text = "Logged Items Today",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (logs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.WaterDrop,
                            contentDescription = "Empty list",
                            tint = if (isDarkTheme) Color(0x33FFFFFF) else Color(0x1A000000),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No logs recorded for this day yet.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            items(logs) { item ->
                WaterLogListItem(
                    log = item,
                    onDelete = { onDeleteLog(item) },
                    isDarkTheme = isDarkTheme
                )
            }
        }

        // Bottom space breathing paddings
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ElevatedHydrationProgressRing(
    percentText: Int,
    totalIntake: Int,
    goal: Int,
    isDarkTheme: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isDarkTheme) Color(0x1AFFFFFF) else Color(0x1A000000),
                RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0x1F1E293B) else Color(0xE6FFFFFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Current Hydration",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .drawBehind {
                        // Background track circle (full circle/almost full circle, like the screenshot)
                        drawArc(
                            color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                            startAngle = -220f,
                            sweepAngle = 260f,
                            useCenter = false,
                            topLeft = Offset(10f, 10f),
                            size = Size(size.width - 20f, size.height - 20f),
                            style = Stroke(width = 16f, cap = StrokeCap.Round)
                        )
                        // Active colored progress arc
                        val sweep = 260f * (percentText / 100f).coerceIn(0f, 1f)
                        drawArc(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF818CF8), Color(0xFF3B82F6))
                            ),
                            startAngle = -220f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = Offset(10f, 10f),
                            size = Size(size.width - 20f, size.height - 20f),
                            style = Stroke(width = 16f, cap = StrokeCap.Round)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$percentText%",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDarkTheme) Color(0xFFC7D2FE) else Color(0xFF2E3252)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = String.format("%,d ml", totalIntake),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF475569)
                    )
                    val remaining = (goal - totalIntake).coerceAtLeast(0)
                    Text(
                        text = if (remaining > 0) "-%,d ml of wetness left 💦".format(remaining) else "Target Met! 🎉 Splashtastic! 🐳",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (remaining > 0) Color(0xFF94A3B8) else Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

@Composable
fun WaterLogListItem(
    log: WaterLog,
    onDelete: () -> Unit,
    isDarkTheme: Boolean
) {
    val timeLabel = remember(log.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .background(
                if (isDarkTheme) Color(0x1B1E293B) else Color.White,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                if (isDarkTheme) Color(0x0FFFFFFF) else Color(0x0A000000),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0x1A2563EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalDrink,
                    contentDescription = "Drop Item",
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "${log.ml} ml",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = "Added at $timeLabel",
                    fontSize = 11.sp,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.testTag("delete_log_button_${log.id}")
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete record",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
