package com.example.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ShortcutPill(
    ml: Int,
    label: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    customCardBg: Color? = null,
    customCardBorder: Color? = null
) {
    val animScale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    
    // Satisfying Android high-fidelity haptic vibration squishes 📳
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val vibrator = remember(context) {
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    val bg = customCardBg ?: (if (isDarkTheme) Color(0x1A1E293B) else Color.White)
    val borderCol = customCardBorder ?: (if (isDarkTheme) Color(0x15FFFFFF) else Color(0x0F000000))

    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .background(bg, RoundedCornerShape(24.dp))
            .border(2.dp, borderCol, RoundedCornerShape(24.dp))
            .graphicsLayer(scaleX = animScale.value, scaleY = animScale.value)
            .clickable {
                // Trigger tactile haptic click standard first
                try {
                    hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                } catch (e: Exception) {}
                
                // Trigger secondary satisfying short squish vibe pattern 🐳💦
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(45)
                    }
                } catch (e: Exception) {}

                coroutineScope.launch {
                    animScale.animateTo(0.92f, spring(stiffness = Spring.StiffnessHigh))
                    animScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
                onClick()
            }
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (ml > 0) "$ml ml" else "Custom",
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}
