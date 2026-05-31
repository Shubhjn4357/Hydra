package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickContainersScreen(
    onLogWater: (Int) -> Unit,
    onShowCustomDialog: () -> Unit,
    onBackClick: () -> Unit,
    isDarkTheme: Boolean,
    onMenuClick: () -> Unit,
    dateLabel: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Aesthetic integrated top bar for containers screen
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
                    contentDescription = "Back to main tracker",
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
                onClick = onMenuClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Settings Menu",
                    tint = if (isDarkTheme) Color.White else Color(0xFF2E3252),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tap a container below to log water! 🥤🥛",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF2E3252),
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp, bottom = 12.dp)
        )

        // Visual layout of direct 2x2 container cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShortcutPill(
                    ml = 250,
                    label = "Glasserrr 🥛",
                    icon = Icons.Filled.WaterDrop,
                    iconBg = if (isDarkTheme) Color(0x333B82F6) else Color(0xFFEFF6FF),
                    iconTint = Color(0xFF3B82F6),
                    onClick = { onLogWater(250) },
                    isDarkTheme = isDarkTheme,
                    customCardBg = if (isDarkTheme) null else Color(0xFFEFF6FF),
                    customCardBorder = if (isDarkTheme) null else Color(0xFFDBEAFE),
                    modifier = Modifier.weight(1f).height(90.dp)
                )
                ShortcutPill(
                    ml = 500,
                    label = "Botlee 🧴",
                    icon = Icons.Filled.LocalDrink,
                    iconBg = if (isDarkTheme) Color(0x3310B981) else Color(0xFFECFDF5),
                    iconTint = Color(0xFF10B981),
                    onClick = { onLogWater(500) },
                    isDarkTheme = isDarkTheme,
                    customCardBg = if (isDarkTheme) null else Color(0xFFECFDF5),
                    customCardBorder = if (isDarkTheme) null else Color(0xFFD1FAE5),
                    modifier = Modifier.weight(1f).height(90.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShortcutPill(
                    ml = 180,
                    label = "Coffeee ☕",
                    icon = Icons.Filled.Coffee,
                    iconBg = if (isDarkTheme) Color(0x33F59E0B) else Color(0xFFFFFBEB),
                    iconTint = Color(0xFFD97706),
                    onClick = { onLogWater(180) },
                    isDarkTheme = isDarkTheme,
                    customCardBg = if (isDarkTheme) null else Color(0xFFFFFBEB),
                    customCardBorder = if (isDarkTheme) null else Color(0xFFFEF3C7),
                    modifier = Modifier.weight(1f).height(90.dp)
                )
                ShortcutPill(
                    ml = 250,
                    label = "Custommm 🏺",
                    icon = Icons.Filled.LocalDrink,
                    iconBg = if (isDarkTheme) Color(0x33FF6B4A) else Color(0xFFFFF2EE),
                    iconTint = Color(0xFFF97316),
                    onClick = onShowCustomDialog,
                    isDarkTheme = isDarkTheme,
                    customCardBg = if (isDarkTheme) null else Color(0xFFFFF2EE),
                    customCardBorder = if (isDarkTheme) null else Color(0xFFFFD5CC),
                    modifier = Modifier.weight(1f).height(90.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cute decorative tip card matching mockup details
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDarkTheme) Color(0x153B82F6) else Color(0xFFEFF6FF), RoundedCornerShape(16.dp))
                .border(1.dp, if (isDarkTheme) Color(0x333B82F6) else Color(0xFFDBEAFE), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "💡 Hydratshun Tip:\nSpreading water intake evenly across your Day keeps metabolism high and focus sharp! Try to sip every 1-2 hourss! 🚀🧠",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF1E3A8A),
                lineHeight = 18.sp
            )
        }
    }
}
