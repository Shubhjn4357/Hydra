package com.example.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun GlassyBottomNavigationBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    isDarkTheme: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp, start = 24.dp, end = 24.dp, top = 8.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                clip = false
            )
            .background(
                color = if (isDarkTheme) Color(0xE01E293B) else Color(0xE0FFFFFF),
                shape = RoundedCornerShape(32.dp)
            )
            .border(
                width = 1.dp,
                color = if (isDarkTheme) Color(0x22FFFFFF) else Color(0x1F000000),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Water Logger Screen
            IconButton(
                onClick = { onTabSelected(AppTab.LOG) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (currentTab == AppTab.LOG) Icons.Filled.WaterDrop else Icons.Outlined.WaterDrop,
                    contentDescription = "Logger Screen 💧",
                    tint = if (currentTab == AppTab.LOG) Color(0xFF6F79FE) else (if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Tab 2: Quick Predefined Containers Screen
            IconButton(
                onClick = { onTabSelected(AppTab.CONTAINERS) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (currentTab == AppTab.CONTAINERS) Icons.Filled.LocalDrink else Icons.Outlined.LocalDrink,
                    contentDescription = "Containers list 🥤",
                    tint = if (currentTab == AppTab.CONTAINERS) Color(0xFF557CFC) else (if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Tab 3: Meditation and Calming Screen
            IconButton(
                onClick = { onTabSelected(AppTab.MEDITATION) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (currentTab == AppTab.MEDITATION) Icons.Filled.SelfImprovement else Icons.Outlined.SelfImprovement,
                    contentDescription = "Meditation Screen 🧘‍♂️",
                    tint = if (currentTab == AppTab.MEDITATION) Color(0xFF10B981) else (if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Tab 4: Detailed Analytics / Stats Screen
            IconButton(
                onClick = { onTabSelected(AppTab.STATS) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (currentTab == AppTab.STATS) Icons.Filled.GridView else Icons.Outlined.GridView,
                    contentDescription = "Dashboard Screen 📊",
                    tint = if (currentTab == AppTab.STATS) Color(0xFF6F79FE) else (if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    isSelected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    testTag: String
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "TabScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                )
                .background(
                    if (isSelected) {
                        if (isDarkTheme) Color(0xFF1D4ED8) else Color(0xFFDBEAFE)
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 18.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) activeIcon else inactiveIcon,
                contentDescription = label,
                tint = if (isSelected) {
                    if (isDarkTheme) Color.White else Color(0xFF1D4ED8)
                } else {
                    if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8)
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
