package com.example.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.WaterViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HydrationLogScreen(
    viewModel: WaterViewModel,
    totalIntake: Int,
    goal: Int,
    onLogWater: (Int) -> Unit,
    onShowCustomDialog: () -> Unit,
    onMenuClick: () -> Unit,
    onDarkThemeToggle: (Boolean) -> Unit,
    isDarkTheme: Boolean,
    dateLabel: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val vesselSilhouette by viewModel.customVesselSilhouette.collectAsStateWithLifecycle()
    val activeTheme by viewModel.activeBottleTheme.collectAsStateWithLifecycle()
    val glassFrosting by viewModel.glassFrosting.collectAsStateWithLifecycle()
    val isLavaLampEnabled by viewModel.isLavaLampEnabled.collectAsStateWithLifecycle()
    val isRaindropsEnabled by viewModel.isRaindropsEnabled.collectAsStateWithLifecycle()
    val isCoralForestEnabled by viewModel.isCoralForestEnabled.collectAsStateWithLifecycle()
    val activeSticker by viewModel.activeSticker.collectAsStateWithLifecycle()

    val percent = if (goal > 0) (totalIntake.toFloat() / goal).coerceIn(0f, 1.5f) else 0f
    val remaining = (goal - totalIntake).coerceAtLeast(0)

    // Smoothly animate the fill percentage to support the responsive fill state
    val animatedPercent by animateFloatAsState(
        targetValue = percent,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "LiquidFillPercent"
    )

    // Smooth running count animation for total logged water ticker
    val animatedTotalIntake by animateIntAsState(
        targetValue = totalIntake,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "LoggedIntakeTicker"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Aesthetic header with integrated dark/light switcher, unified Date Selector, and goal setting action button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onDarkThemeToggle(!isDarkTheme) },
                modifier = Modifier.size(48.dp)
            ) {
                Text(
                    text = if (isDarkTheme) "☀️" else "🌙",
                    fontSize = 24.sp
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

        Spacer(modifier = Modifier.height(12.dp))

        // Large high-fidelity bottle containing text metrics, meditating buddy, and floating addition icon
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("bottle_wave_container"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(310.dp)
                    .aspectRatio(0.65f),
                contentAlignment = Alignment.Center
            ) {
                // 1. Draw solid custom glass outer shape and moving liquid / buddy
                LiquidBottleWave(
                    percentage = animatedPercent,
                    isDarkTheme = isDarkTheme,
                    vesselSilhouette = vesselSilhouette,
                    activeTheme = activeTheme,
                    glassFrosting = glassFrosting,
                    isLavaLampEnabled = isLavaLampEnabled,
                    isRaindropsEnabled = isRaindropsEnabled,
                    isCoralForestEnabled = isCoralForestEnabled,
                    activeSticker = activeSticker,
                    modifier = Modifier.fillMaxSize()
                )

                // 2. Telemetry and statistics texts overlayed on the upper clear pane of the bottle
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(92.dp))
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 38.sp, fontWeight = FontWeight.Black)) {
                                append(String.format("%,d", animatedTotalIntake))
                            }
                            withStyle(SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF3B82F6))) {
                                append(" / ")
                                append(String.format("%,d", goal))
                                append(" ml")
                            }
                        },
                        color = if (isDarkTheme) Color.White else Color(0xFF2A2E50),
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.testTag("current_hydration_text")
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = if (remaining > 0) "Remaining ${remaining} ml 💦" else "Target Met! 🐳🎉",
                        fontSize = 12.sp,
                        color = if (remaining > 0) (if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF2563EB)) else Color(0xFF059669),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.testTag("remaining_hydration_text")
                    )
                }

                // 3. Blue "+" Action button overlayed near the bottom of the bottle liquid
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 44.dp)
                        .size(54.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            clip = false
                        )
                        .clip(CircleShape)
                        .background(Color.White)
                        .combinedClickable(
                            onClick = { onLogWater(250) },
                            onLongClick = onShowCustomDialog
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Log 250ml",
                        tint = Color(0xFF557CFC),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
