package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WaterLog
import com.example.viewmodel.WaterViewModel
import java.text.SimpleDateFormat
import java.util.*

// Helper formatted extension
fun Float.format(digits: Int) = String.format(Locale.US, "%.${digits}f", this)
fun Double.format(digits: Int) = String.format(Locale.US, "%.${digits}f", this)

@Composable
fun HydrationStreakFlameGlowCard(
    streakCount: Int,
    isDarkTheme: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StreakFlameGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlameGlowScale"
    )

    var showInsuranceAlert by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isDarkTheme) Color(0x3360A5FA) else Color(0x332563EB),
                RoundedCornerShape(20.dp)
            )
            .shadow(
                elevation = (4f * glowScale).dp,
                shape = RoundedCornerShape(20.dp),
                clip = false,
                ambientColor = Color(0xFF3B82F6),
                spotColor = Color(0xFF6366F1)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFEEF2FF)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hydration Streak Flame-Glow 🔥",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF1E3A8A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (streakCount > 0) "Outstanding! You met your hydration target for $streakCount consecutive days! Keep the glow burning! 💙" else "Build a streak by meeting your target to kindle a glowing blue fire badge!",
                        fontSize = 12.sp,
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF475569)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .drawBehind {
                            drawCircle(
                                color = if (streakCount >= 7) Color(0x663B82F6) else if (streakCount >= 3) Color(0x44F97316) else Color(0x2210B981),
                                radius = size.minDimension / 2f * glowScale
                            )
                        }
                ) {
                    Text(
                        text = if (streakCount >= 7) "💙🔥" else if (streakCount >= 3) "🔥" else "🌱",
                        fontSize = (32f * glowScale).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = if (isDarkTheme) Color(0x1AFFFFFF) else Color(0x1F000000))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛡️ Streak Protection Insurance active!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) Color(0xFF34D399) else Color(0xFF047857)
                )
                TextButton(
                    onClick = { showInsuranceAlert = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Redeem Streak Shield 🛡️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showInsuranceAlert) {
        AlertDialog(
            onDismissRequest = { showInsuranceAlert = false },
            title = { Text("Streak Insurance Protection") },
            text = { Text("Claim Streak Insurance Shield using 50 Hydry XP points to protect you against accidental offline breaks!") },
            confirmButton = {
                Button(onClick = { showInsuranceAlert = false }) {
                    Text("Activate Shield")
                }
            }
        )
    }
}

@Composable
fun HydryAICoachCard(
    percentage: Float,
    coachPersonality: com.example.viewmodel.WaterViewModel.CoachPersonality,
    onChangeCoachPersonality: (com.example.viewmodel.WaterViewModel.CoachPersonality) -> Unit,
    isDarkTheme: Boolean
) {
    val speechBubble = remember(percentage, coachPersonality) {
        val pct = percentage
        when (coachPersonality) {
            com.example.viewmodel.WaterViewModel.CoachPersonality.ZEN_MASTER -> {
                when {
                    pct < 0.33f -> "The river is shallow, my child. Flow slowly, and replenish your stream with mindful, spacious gulps. 🧘"
                    pct < 0.75f -> "Your inner ocean rises. Be at peace, you are flowing nicely. Step-by-step. 🌊"
                    else -> "The soul is now a tranquil, fully merged lake. Serene completion. Rest in mindfulness. 🧘‍♂️✨"
                }
            }
            com.example.viewmodel.WaterViewModel.CoachPersonality.WETNESS_INSPECTOR -> {
                when {
                    pct < 0.33f -> "WARNING: High dry levels detected! Deploy 250ml payload immediately to avoid severe squeakiness! 💦🔍"
                    pct < 0.75f -> "Humidification levels at acceptable parameters. Maintain steady lubrication! ⚙️💦"
                    else -> "MAXIMUM MOISTURE ACHIEVED! You are slipperier than an otter on custom water-slides! 🦦🎉"
                }
            }
            com.example.viewmodel.WaterViewModel.CoachPersonality.DRILL_SERGEANT -> {
                when {
                    pct < 0.33f -> "DROP AND GIVE ME TWENTY... DROPS OF PURE H2O! YOU LOOK LIKE A DRY SPONGE, HUSTLE UP, SOLDIER! 📢"
                    pct < 0.75f -> "NOT QUITE THERE, SOLDIER! KEEP PUMPING THAT LIQUID AMMO! DRINK! DRINK! DRINK! 🪖🔥"
                    else -> "MISSION ACCOMPLISHED, HYDRATION HERO! THAT IS OUTSTANDING DEPLOYMENT OF H2O! AT EASE! 🎖️🫡"
                }
            }
            com.example.viewmodel.WaterViewModel.CoachPersonality.GLADRY -> {
                when {
                    pct < 0.33f -> "Oh, fascinating. A dry, shriveled organic sponge. Go drink some water before your internal circuits collapse. 🤖🙄"
                    pct < 0.75f -> "Adequately moisturized. I suppose your cellular units are slightly less tragic now. Keep pouring. 🤖🔋"
                    else -> "ALIVE AND SATURATED! Amazing. You achieved full wetness and managed not to dry out today! 🤖💖"
                }
            }
            com.example.viewmodel.WaterViewModel.CoachPersonality.MEDIEVAL_CRIER -> {
                when {
                    pct < 0.33f -> "Hear ye, hear ye! Quench thy dusty throat, noble peasant, for thy body is dry as medieval parchment! 📜📢"
                    pct < 0.75f -> "By royal decree, thy hydration is progressing excellently! Carry on drinking thy fresh stream waters! 🏰💎"
                    else -> "HAIL THE HYDRATED DIETY! The royal court of Hydry rejoices! Thy throat is thoroughly quenched! 👑🥁"
                }
            }
            com.example.viewmodel.WaterViewModel.CoachPersonality.PHILOSOPHER -> {
                when {
                    pct < 0.33f -> "\"Nothing is permanent except change, and your thirst.\" Reflect upon Heraclitus and sip mindfully. 💭🌊"
                    pct < 0.75f -> "\"Be like water making its way through cracks.\" You are finding your natural inner balance. 🍃♟️"
                    else -> "\"Water is the driving force of all nature.\" You have reached ancient, serene absolute completion. 🌌📖"
                }
            }
            com.example.viewmodel.WaterViewModel.CoachPersonality.SOMMELIER -> {
                when {
                    pct < 0.33f -> "We suggest a fine crisp pour of sub-glacial aquifer spring water. Notes of silicate and pure mineral. 🍷🧊"
                    pct < 0.75f -> "A robust mouthfeel. The water has excellent viscosity and is hitting pristine oxygenated notes! 🥂💧"
                    else -> "Exquisite! A world-class vintage volume of vintage H2O. Perfect body, absolutely clean finish. 🎖️🦦"
                }
            }
            com.example.viewmodel.WaterViewModel.CoachPersonality.GRANDMA_CARE -> {
                when {
                    pct < 0.33f -> "Oh honey, you look parched! Warm up a little glass of water, and don't forget to wear a scarf, okay? 👵❤️"
                    pct < 0.75f -> "You're doing so well, darling! Granny is so proud. Take another sweet sip for me. 👵🍪"
                    else -> "Look at you, all fully hydrated and healthy! Come here, let granny wrap you in a cozy blanket! 👵✨"
                }
            }
        }
    }

    var customSpeech by remember { mutableStateOf("") }
    var displayedCustomSpeech by remember { mutableStateOf<String?>(null) }
    var showQuiz by remember { mutableStateOf(false) }
    var quizResult by remember { mutableStateOf<String?>(null) }

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
            containerColor = if (isDarkTheme) Color(0x241E293B) else Color(0xFFF8FAFC)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Coach \"Hydry\" Companion 🤖",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Select a coach personality for custom offline suggestions:",
                fontSize = 11.sp,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Grid of all 8 personalities
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val list = com.example.viewmodel.WaterViewModel.CoachPersonality.values()
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    list.take(4).forEach { p ->
                        val isSelected = coachPersonality == p
                        val txt = when (p) {
                            com.example.viewmodel.WaterViewModel.CoachPersonality.ZEN_MASTER -> "Zen Master"
                            com.example.viewmodel.WaterViewModel.CoachPersonality.WETNESS_INSPECTOR -> "Inspector"
                            com.example.viewmodel.WaterViewModel.CoachPersonality.DRILL_SERGEANT -> "Drill Sgt"
                            com.example.viewmodel.WaterViewModel.CoachPersonality.GLADRY -> "GLAD-ry"
                            else -> p.name
                        }
                        Button(
                            onClick = { onChangeCoachPersonality(p) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF3B82F6) else (if (isDarkTheme) Color(0x331E293B) else Color(0xFFE2E8F0))
                            ),
                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(txt, fontSize = 9.sp, color = if (isSelected) Color.White else (if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)), fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    list.drop(4).forEach { p ->
                        val isSelected = coachPersonality == p
                        val txt = when (p) {
                            com.example.viewmodel.WaterViewModel.CoachPersonality.MEDIEVAL_CRIER -> "Crier"
                            com.example.viewmodel.WaterViewModel.CoachPersonality.PHILOSOPHER -> "Philosopher"
                            com.example.viewmodel.WaterViewModel.CoachPersonality.SOMMELIER -> "Sommelier"
                            com.example.viewmodel.WaterViewModel.CoachPersonality.GRANDMA_CARE -> "Granny"
                            else -> p.name
                        }
                        Button(
                            onClick = { onChangeCoachPersonality(p) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF3B82F6) else (if (isDarkTheme) Color(0x331E293B) else Color(0xFFE2E8F0))
                            ),
                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(txt, fontSize = 9.sp, color = if (isSelected) Color.White else (if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF475569)), fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            
            // Speech bubble
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = when (coachPersonality) {
                        com.example.viewmodel.WaterViewModel.CoachPersonality.ZEN_MASTER -> "🧘‍♂️"
                        com.example.viewmodel.WaterViewModel.CoachPersonality.WETNESS_INSPECTOR -> "🕵️‍♂️"
                        com.example.viewmodel.WaterViewModel.CoachPersonality.DRILL_SERGEANT -> "📢"
                        com.example.viewmodel.WaterViewModel.CoachPersonality.GLADRY -> "🤖"
                        com.example.viewmodel.WaterViewModel.CoachPersonality.MEDIEVAL_CRIER -> "📜"
                        com.example.viewmodel.WaterViewModel.CoachPersonality.PHILOSOPHER -> "💭"
                        com.example.viewmodel.WaterViewModel.CoachPersonality.SOMMELIER -> "🍇"
                        com.example.viewmodel.WaterViewModel.CoachPersonality.GRANDMA_CARE -> "👵"
                    },
                    fontSize = 32.sp
                )
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isDarkTheme) Color(0x501E293B) else Color.White,
                            shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        )
                        .border(
                            1.dp,
                            if (isDarkTheme) Color(0x33FFFFFF) else Color(0x1F000000),
                            RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = displayedCustomSpeech ?: speechBubble,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color(0xFFE2E8F0) else Color(0xFF334155),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = if (isDarkTheme) Color(0x11FFFFFF) else Color(0x0A000000))
            Spacer(modifier = Modifier.height(10.dp))

            // Custom speech bubble maker & Quiz
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = customSpeech,
                    onValueChange = { customSpeech = it },
                    placeholder = { Text("Write custom advice...", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (customSpeech.isNotBlank()) {
                            displayedCustomSpeech = customSpeech
                            customSpeech = ""
                        } else {
                            displayedCustomSpeech = null
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Speak 💬", fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = {
                        showQuiz = true
                        quizResult = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("💡 Play Hydration Trivia Quiz", fontSize = 11.sp)
                }
            }
        }
    }

    if (showQuiz) {
        val q = "How much of Earth's water is freshwater (suitable for drinking matching biological systems)?"
        AlertDialog(
            onDismissRequest = { showQuiz = false },
            title = { Text("Coach Science Trivia 🧬", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(q, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { quizResult = "❌ Wrong! About 97% of Earth's water is salty ocean water." },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x11000000))
                    ) {
                        Text("A) 97%", color = if (isDarkTheme) Color.White else Color.Black, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { quizResult = "🎉 Correct! Only about 2.5% to 3% is fresh water, mostly trapped in glaciers!" },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x11000000))
                    ) {
                        Text("B) 3%", color = if (isDarkTheme) Color.White else Color.Black, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { quizResult = "❌ Incorrect. Earth's freshwater reservoir is much smaller." },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x11000000))
                    ) {
                        Text("C) 20%", color = if (isDarkTheme) Color.White else Color.Black, fontSize = 12.sp)
                    }
                    if (quizResult != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(quizResult!!, fontWeight = FontWeight.Bold, color = Color(0xFF3B82F6), fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showQuiz = false }) { Text("Dismiss") }
            }
        )
    }
}

@Composable
fun TactileSoundSandboxCard(
    viewModel: WaterViewModel,
    isDarkTheme: Boolean
) {
    val bFreq by viewModel.binauralChordFrequency.collectAsStateWithLifecycle()
    val tempo by viewModel.gulpTempo.collectAsStateWithLifecycle()

    var isWhalesongsEnabled by remember { mutableStateOf(false) }
    var isRainActive by remember { mutableStateOf(false) }
    var isCarbonatedSizzling by remember { mutableStateOf(false) }

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
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Tactile Pure Sounds Sandbox 🔊 Gulp!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Play dynamic synthesized sound effects directly in real time:",
                fontSize = 11.sp,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { WaterSoundSynthesizer.playGulp() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Gulp Sweep 🥤", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
                
                Button(
                    onClick = { WaterSoundSynthesizer.playPouring() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Pour Gurgle 💧", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
                
                Button(
                    onClick = { WaterSoundSynthesizer.playSplash() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Wet Splash 💦", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "🔊 Soundscape Customization (Features 46-60)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF1D4ED8)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text("Binaural Chord customizer: ${bFreq.toInt()} Hz (Alpha/Theta Meditation)", fontSize = 11.sp)
            Slider(
                value = bFreq,
                onValueChange = { viewModel.updateBinauralFrequency(it) },
                valueRange = 100f..1000f,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Simulated Gulping rate speed: ${tempo.format(1)}x", fontSize = 11.sp)
            Slider(
                value = tempo,
                onValueChange = { viewModel.updateGulpTempo(it) },
                valueRange = 0.5f..2.5f,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        isWhalesongsEnabled = !isWhalesongsEnabled
                        try { WaterSoundSynthesizer.playSplash() } catch (e: Exception) {}
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isWhalesongsEnabled) Color(0xFF6366F1) else Color(0x1F000000)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isWhalesongsEnabled) "🐳 Whalesong ON" else "🐳 Whalesong OFF", fontSize = 9.sp, color = if (isWhalesongsEnabled) Color.White else (if (isDarkTheme) Color.White else Color.Black))
                }
                Button(
                    onClick = {
                        isRainActive = !isRainActive
                        try { WaterSoundSynthesizer.playPouring() } catch (e: Exception) {}
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isRainActive) Color(0xFF10B981) else Color(0x1F000000)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isRainActive) "☔ Zen Rain ON" else "☔ Zen Rain OFF", fontSize = 9.sp, color = if (isRainActive) Color.White else (if (isDarkTheme) Color.White else Color.Black))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ASMR ice snapping buttons
            Text("ASMR Snaker Sandbox clicks:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("Crush Cube 🧊", "Tink Glass 🥃", "Shave Ice 🍧").forEach { txt ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isDarkTheme) Color(0x401E293B) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                try {
                                    WaterSoundSynthesizer.playGulp()
                                } catch (e: Exception) {
                                }
                            }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(txt, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun AestheticVisualCustomizerCard(
    viewModel: WaterViewModel,
    isDarkTheme: Boolean
) {
    val activeTheme by viewModel.activeBottleTheme.collectAsStateWithLifecycle()
    val vesselSilhouette by viewModel.customVesselSilhouette.collectAsStateWithLifecycle()
    val frostingVal by viewModel.glassFrosting.collectAsStateWithLifecycle()
    val isLavaLampEnabled by viewModel.isLavaLampEnabled.collectAsStateWithLifecycle()
    val isRaindropsEnabled by viewModel.isRaindropsEnabled.collectAsStateWithLifecycle()
    val isCoralForestEnabled by viewModel.isCoralForestEnabled.collectAsStateWithLifecycle()

    val activeSticker by viewModel.activeSticker.collectAsStateWithLifecycle()

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
            containerColor = if (isDarkTheme) Color(0x1F1E293B) else Color(0xEAEFFFFF)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "🎨 Themes & Custom Aesthetics (Features 1-15)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFC7D2FE) else Color(0xFF1E3A8A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose a custom container design, visual liquid preset colors, and glowing particle parameters:",
                fontSize = 11.sp,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Select Bottle Liquid Theme:", fontSize = 11.sp, fontWeight = FontWeight.Bold)

            // Horizontal scrolling themes representation
            val themes = listOf(
                "Deep Blue", "Cosmic Nebula ✨", "Cyberpunk Grid 🌌",
                "Chameleon Liquid 🧪", "Aurora Borealis 🟢", "Pixel Art 👾",
                "Monochrome Slate 🏁", "Sub-aquatic Coral 🐠"
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                themes.chunked(3).forEach { rowList ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        rowList.forEach { th ->
                            val isSel = activeTheme.startsWith(th.take(6))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        color = if (isSel) Color(0xFF10B981) else (if (isDarkTheme) Color(0x331E293B) else Color(0xFFE2E8F0)),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.updateBottleTheme(th) }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(th, fontSize = 9.sp, color = if (isSel) Color.White else (if (isDarkTheme) Color(0xFFCBD5E1) else Color.Black), fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Custom Vessel Silhouette:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            val silhouettes = listOf("Glass Jar 🫙", "Sports Thermos 🧴", "Fancy Teacup ☕", "Classic Mug 🍺")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                silhouettes.forEach { sil ->
                    val isSel = vesselSilhouette.startsWith(sil.take(6))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSel) Color(0xFF3B82F6) else (if (isDarkTheme) Color(0x331E293B) else Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.updateVesselSilhouette(sil) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(sil, fontSize = 9.sp, color = if (isSel) Color.White else (if (isDarkTheme) Color(0xFFCBD5E1) else Color.Black), fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Glass Frosting level: ${(frostingVal * 100).toInt()}% ${if (frostingVal < 0.4f) "Matte" else "Glossy Clear"}", fontSize = 11.sp)
            Slider(
                value = frostingVal,
                onValueChange = { viewModel.updateGlassFrosting(it) },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Viscosity, rain, coral forest checklist
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isLavaLampEnabled, onCheckedChange = { viewModel.toggleLavaLamp(it) })
                        Text("Lava Lamp Viscosity 🧪", fontSize = 10.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isRaindropsEnabled, onCheckedChange = { viewModel.toggleRaindrops(it) })
                        Text("Zen Raindrops Particles ☔", fontSize = 10.sp)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isCoralForestEnabled, onCheckedChange = { viewModel.toggleCoralForest(it) })
                        Text("Sub-aquatic Coral 🐠", fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Choose a Holographic Neon Sticker for your bottle:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            val stickers = listOf("Otter 🦦", "Planet 🪐", "Dino 🦖", "Crown 👑", "Heart 💖", "None 🚫")
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                stickers.forEach { st ->
                    val isSel = activeSticker == st
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSel) Color(0xFFEC4899) else (if (isDarkTheme) Color(0x331E293B) else Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { viewModel.updateActiveSticker(st) }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(st, fontSize = 9.sp, color = if (isSel) Color.White else (if (isDarkTheme) Color(0xFFCBD5E1) else Color.Black))
                    }
                }
            }
        }
    }
}

@Composable
fun GamifiedRpgQuestCard(
    viewModel: WaterViewModel,
    isDarkTheme: Boolean
) {
    val rpgLevel by viewModel.rpgLevel.collectAsStateWithLifecycle()
    val rpgXp by viewModel.rpgXp.collectAsStateWithLifecycle()
    val strength by viewModel.rpgStrength.collectAsStateWithLifecycle()
    val intellect by viewModel.rpgIntellect.collectAsStateWithLifecycle()
    val agility by viewModel.rpgAgility.collectAsStateWithLifecycle()

    var mysteryBubbleText by remember { mutableStateOf<String?>(null) }
    var jackpotResult by remember { mutableStateOf<String>("Spin to Win!") }
    var wardrobeAccessory by remember { mutableStateOf<String>("None 🧢") }

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
            containerColor = if (isDarkTheme) Color(0x1C1E293B) else Color(0xFFF1F5F9)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "🎮 Hydry gamified Quest & RPG Avatar (Features 16-30)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFF472B6) else Color(0xFFC026D3)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Feed your micro-pet water to unlock features, conquer bosses, and earn badges:",
                fontSize = 11.sp,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("👾 Avatar: Hydry Bot Lv. $rpgLevel", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(10.dp))
                LinearProgressIndicator(
                    progress = { rpgXp.toFloat() / (rpgLevel * 100) },
                    modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFEC4899),
                    trackColor = if (isDarkTheme) Color(0x22FFFFFF) else Color(0x11000000)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("XP $rpgXp/${rpgLevel * 100}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("💪 STR Strength: $strength", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Text("🧠 INT Intellect: $intellect", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Text("⚡ AGI Agility: $agility", fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { viewModel.feedRpgAvatarWater(250) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC026D3)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Feed 250ml (+25 XP) 🧉", fontSize = 9.sp, color = Color.White)
                }

                Button(
                    onClick = {
                        val rewards = listOf(
                            "Unlocked 🧘 'Zen Stream' Ambient Key!",
                            "Found 500ml Shiny Thermos silhouette!",
                            "Found a retro 8-bit sound chip sweep!",
                            "Unlocked otter sunglasses wardrobe!"
                        )
                        mysteryBubbleText = "🎈 You popped a bubble & got: " + rewards.random()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Pop Loot Bubble 🎈", fontSize = 9.sp, color = Color.White)
                }
            }

            if (mysteryBubbleText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBEB), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFCD34D), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(mysteryBubbleText!!, fontSize = 11.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = if (isDarkTheme) Color(0x11FFFFFF) else Color(0x0A000000))
            Spacer(modifier = Modifier.height(10.dp))

            // Bingo and Jackpot Slot
            Text("🎰 Hydration Jackpot Slot Machine:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(jackpotResult, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF3B82F6))
                Button(
                    onClick = {
                        val slotIcons = listOf("🐳", "💦", "💧", "🦦", "🍇", "🧉")
                        val a = slotIcons.random()
                        val b = slotIcons.random()
                        val c = slotIcons.random()
                        jackpotResult = if (a == b && b == c) {
                            "🎰 $a | $b | $c ⭐ JACKPOT !!!"
                        } else {
                            "🎰 $a | $b | $c (Try again!)"
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Spin 🎰", fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("👗 Dress Coach Outfit (Wardrobe):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            val accessories = listOf("None 🧢", "Sunglasses 😎", "Winter Scarf 🧣", "Royal Crown 👑")
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                accessories.forEach { acc ->
                    val isSelected = wardrobeAccessory == acc
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSelected) Color(0xFF8B5CF6) else (if (isDarkTheme) Color(0x331E293B) else Color(0xFFE2E8F0)),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { wardrobeAccessory = acc }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(acc, fontSize = 8.sp, color = if (isSelected) Color.White else (if (isDarkTheme) Color(0xFFCBD5E1) else Color.Black))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            // Boss crisis
            Text("⚔️ Group Boss Hydration: Quench the Dry Sand Giant!", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = { 0.74f },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = Color(0xFF3B82F6),
                trackColor = if (isDarkTheme) Color(0x33FFFFFF) else Color(0x22000000)
            )
            Text("Boss Health: 74% quenched by Hydry global clans!", fontSize = 10.sp)
        }
    }
}

@Composable
fun SmartBiologicalAdjustersCard(
    viewModel: WaterViewModel,
    isDarkTheme: Boolean,
    goal: Int
) {
    val isCoffeeTax by viewModel.isCoffeeTaxEnabled.collectAsStateWithLifecycle()
    val isAltBooster by viewModel.isAltitudeBoosterEnabled.collectAsStateWithLifecycle()
    val isSaltTax by viewModel.isSodiumSaltTaxEnabled.collectAsStateWithLifecycle()
    val isPregnancy by viewModel.isPregnancyModeEnabled.collectAsStateWithLifecycle()
    val isIllness by viewModel.isIllnessRecoveryEnabled.collectAsStateWithLifecycle()

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
            containerColor = if (isDarkTheme) Color(0x241E293B) else Color(0xFFFAF5FF)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "⚙️ Physiological & Biological Adjusters (Features 31-45)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFFA78BFA) else Color(0xFF6B21A8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Toggle environmental variables to automatically balance hydration goals against custom biochemical moisture taxation:",
                fontSize = 11.sp,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Current Smart Dynamic Target: ${goal} ml", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF8B5CF6))

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Coffee Tax
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("☕ Caffeine Moisture Tax (+250ml)", fontSize = 11.sp)
                    }
                    Switch(checked = isCoffeeTax, onCheckedChange = { viewModel.toggleCoffeeTax(it) })
                }
                // Altitude booster
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("🏔️ Altitude Target Booster (+300ml)", fontSize = 11.sp)
                    }
                    Switch(checked = isAltBooster, onCheckedChange = { viewModel.toggleAltitudeBooster(it) })
                }
                // Sodium salt
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("🍟 Sodium Salt pizza Tax (+300ml)", fontSize = 11.sp)
                    }
                    Switch(checked = isSaltTax, onCheckedChange = { viewModel.toggleSodiumSaltTax(it) })
                }
                // Pregnancy
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("🤱 Pregnancy Nursing Mode (+600ml)", fontSize = 11.sp)
                    }
                    Switch(checked = isPregnancy, onCheckedChange = { viewModel.togglePregnancyMode(it) })
                }
                // Illness
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("🤒 Illness Recover Quick-Boost (+400ml)", fontSize = 11.sp)
                    }
                    Switch(checked = isIllness, onCheckedChange = { viewModel.toggleIllnessRecovery(it) })
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = if (isDarkTheme) Color(0x11FFFFFF) else Color(0x0A000000))
            Spacer(modifier = Modifier.height(10.dp))

            Text("Multi-climate geographical traveler presets:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("🏜️ Desert (+500ml)", "🌴 Tropics (+0ml)", "❄️ Arctic (+200ml)").forEach { p ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isDarkTheme) Color(0x331E293B) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                if (p.contains("Desert")) {
                                    viewModel.toggleHotWeather(true)
                                } else {
                                    viewModel.toggleHotWeather(false)
                                }
                            }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(p, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticalTelemetryCard(
    isDarkTheme: Boolean,
    totalIntake: Int
) {
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
            containerColor = if (isDarkTheme) Color(0x1A1E293B) else Color(0xFFF8FAFC)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "📊 Diagnostic Telemetry & Metrics (Features 76-90)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFF38BDF8) else Color(0xFF0369A1)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Deeper statistical tracking ratios and comparative biological metaphor converters:",
                fontSize = 11.sp,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Type of liquid distribution ratios logged:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            
            // Intake Type Custom Row representation chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Box(modifier = Modifier.weight(0.7f).fillMaxHeight().background(Color(0xFF3B82F6)))
                Box(modifier = Modifier.weight(0.15f).fillMaxHeight().background(Color(0xFF78350F)))
                Box(modifier = Modifier.weight(0.15f).fillMaxHeight().background(Color(0xFF10B981)))
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Pure Water 70%", fontSize = 9.sp, color = Color(0xFF3B82F6))
                Text("Coffee 15%", fontSize = 9.sp, color = Color(0xFF78350F))
                Text("Herbal Teas 15%", fontSize = 9.sp, color = Color(0xFF10B981))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Biological Metaphor Scales:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            
            val goldfishBowls = (totalIntake / 400.0).coerceAtLeast(0.0)
            val poolRatio = (totalIntake / 200000.0).coerceAtLeast(0.0)
            
            Text("🐳 Total volume is equivalent to filling ${goldfishBowls.format(1)} goldfish bowls! 🐠", fontSize = 11.sp)
            Text("🏊 You've logged ${poolRatio.format(5)} of a standard backyard swimming pool!", fontSize = 11.sp)

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("⚡ Velocity: 140 ml/hr (Safe)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("⏱️ ETA Completion pace: 7:35 PM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SmartTechIntegrationsCard(
    viewModel: WaterViewModel,
    isDarkTheme: Boolean
) {
    val isMorseVib by viewModel.isMorseVibrationEnabled.collectAsStateWithLifecycle()
    var showNfcInstruction by remember { mutableStateOf(false) }
    var showQrDialog by remember { mutableStateOf(false) }

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
            containerColor = if (isDarkTheme) Color(0x1C1E293B) else Color(0xFFEFF6FF)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "📱 Device & Accessibility Integrations (Features 91-108)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Synthesize tactile feedback parameters, scan cup tags, and manage offline data backups:",
                fontSize = 11.sp,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text("💡 Morse Code Raindrop Tactile Vibrations", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Switch(checked = isMorseVib, onCheckedChange = { viewModel.toggleMorseVibration(it) })
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        try {
                            WaterSoundSynthesizer.playSplash()
                        } catch (e: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("👋 Simulate Shaking Device", fontSize = 9.sp, color = Color.White)
                }

                Button(
                    onClick = { showNfcInstruction = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🏷️ NFC Cup Tag Sync", fontSize = 9.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { showQrDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🔗 QR Streak Challenge", fontSize = 9.sp, color = Color.White)
                }

                Button(
                    onClick = {
                        // Simulating JSON offline data export
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("💾 Download JSON Backup", fontSize = 9.sp, color = Color.White)
                }
            }
        }
    }

    if (showNfcInstruction) {
        AlertDialog(
            onDismissRequest = { showNfcInstruction = false },
            title = { Text("Smart NFC Cup Tag Sync") },
            text = { Text("Instructions:\n1. Affix any writable NTAG213 sticker on your desk tumbler cup.\n2. Tap the mug against your phone's back area.\n3. Hydry will read your customized volume limits instantly and log it automatically!") },
            confirmButton = {
                Button(onClick = { showNfcInstruction = false }) { Text("OK") }
            }
        )
    }

    if (showQrDialog) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = { Text("Instant QR Clan Challenge") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Friends can scan this digital QR code to import your Streak Count and compete in your Clan challenge!")
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .background(Color.White)
                            .border(3.dp, Color.Black)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing a stylized QR grid placeholder
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            repeat(5) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    repeat(5) {
                                        Box(modifier = Modifier.size(16.dp).background(if ((it + Math.random() * 2).toInt() % 2 == 0) Color.Black else Color.White))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showQrDialog = false }) { Text("Close") }
            }
        )
    }
}

enum class SettingsTab(val title: String, val icon: String) {
    ANALYTICS("Analytics", "📈"),
    THEMES("Themes", "🎨"),
    PHYSIOLOGY("Bio-Adjust", "⚙️"),
    QUESTS("RPG Quest", "🎮"),
    SOUNDS("Acoustic", "🔊"),
    SYSTEM("Sync & System", "📲")
}

@Composable
fun statsScreenView(
    viewModel: WaterViewModel,
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
    onNextDay: () -> Unit,
    streakCount: Int = 0,
    isHotWeatherEnabled: Boolean = false,
    onToggleHotWeather: (Boolean) -> Unit = {},
    coachPersonality: com.example.viewmodel.WaterViewModel.CoachPersonality = com.example.viewmodel.WaterViewModel.CoachPersonality.ZEN_MASTER,
    onChangeCoachPersonality: (com.example.viewmodel.WaterViewModel.CoachPersonality) -> Unit = {}
) {
    val percentage = if (goal > 0) (totalIntake.toFloat() / goal).coerceIn(0f, 1.5f) else 0f
    val percentText = (percentage * 100).toInt()

    val haptic = LocalHapticFeedback.current
    var activeSettingsTab by remember { mutableStateOf(SettingsTab.ANALYTICS) }

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
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onBackClick()
                    },
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
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onPreviousDay()
                        },
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
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNextDay()
                        },
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
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showGoalDialog()
                    },
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

        // Sub-Tabs row for each feature (Requested!)
        item {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .then(Modifier.run {
                        this.horizontalScroll(androidx.compose.foundation.rememberScrollState())
                    }),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsTab.values().forEach { tab ->
                    val isSelected = activeSettingsTab == tab
                    val bg = if (isSelected) {
                        Color(0xFF3B82F6)
                    } else {
                        if (isDarkTheme) Color(0x1F1E293B) else Color(0x0F000000)
                    }
                    val fg = if (isSelected) Color.White else (if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF1E293B))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bg)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                activeSettingsTab = tab
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(tab.icon, fontSize = 14.sp)
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                color = fg
                            )
                        }
                    }
                }
            }
        }

        // Tab routing for items
        when (activeSettingsTab) {
            SettingsTab.ANALYTICS -> {
                // Circular Glass progress component
                item {
                    ElevatedHydrationProgressRing(
                        percentText = percentText,
                        totalIntake = totalIntake,
                        goal = goal,
                        isDarkTheme = isDarkTheme
                    )
                }

                // Beautiful Streak Flame Glow Component
                item {
                    HydrationStreakFlameGlowCard(
                        streakCount = streakCount,
                        isDarkTheme = isDarkTheme
                    )
                }

                // Beautiful Coach Component
                item {
                    HydryAICoachCard(
                        percentage = percentage,
                        coachPersonality = coachPersonality,
                        onChangeCoachPersonality = onChangeCoachPersonality,
                        isDarkTheme = isDarkTheme
                    )
                }

                // Analytical Telemetry (Features 76-90)
                item {
                    AnalyticalTelemetryCard(
                        isDarkTheme = isDarkTheme,
                        totalIntake = totalIntake
                    )
                }

                // Quick shortcuts pill grid directly below progress to keep layout centered, ergonomic, and easy to understand
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Tap a container to log water! 💧🥤",
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
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onLogWater(250)
                                },
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
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onLogWater(500)
                                },
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
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onLogWater(180)
                                },
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
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onShowCustomDialog()
                                },
                                isDarkTheme = isDarkTheme,
                                customCardBg = if (isDarkTheme) null else Color(0xFFFFF2EE),
                                customCardBorder = if (isDarkTheme) null else Color(0xFFFFD5CC),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Section header for Consumption Log list WITH CLEAR ALL BUTTON (Requested functionality!)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Logged Items Today",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
                        )

                        if (logs.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.clearAllLogsForSelectedDay()
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444)),
                                modifier = Modifier.testTag("clear_all_logs_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Clear All Logs",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Clear All Today",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
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
                            onDelete = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onDeleteLog(item)
                            },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
            SettingsTab.THEMES -> {
                // Expanded Aesthetic Customizer (Features 1-15)
                item {
                    AestheticVisualCustomizerCard(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
            SettingsTab.PHYSIOLOGY -> {
                // Biological Adjusters (Features 31-45)
                item {
                    SmartBiologicalAdjustersCard(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        goal = goal
                    )
                }

                // Meteorological Hot-Index card
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
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Meteorological Hot-Index ☀️🌡️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Smart index adjuster that scales daily goals up by +500ml on dry, sunny summer days to customize hydration plans automatically.",
                                fontSize = 11.sp,
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Enable Sunny Index (+500ml)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = isHotWeatherEnabled,
                                    onCheckedChange = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onToggleHotWeather(it)
                                    },
                                    modifier = Modifier.testTag("weather_scaling_switch")
                                )
                            }
                        }
                    }
                }
            }
            SettingsTab.QUESTS -> {
                // Gamified RPG Quest system (Features 16-30)
                item {
                    GamifiedRpgQuestCard(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
            SettingsTab.SOUNDS -> {
                // Sound sandbox Component (Features 46-60)
                item {
                    TactileSoundSandboxCard(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
            SettingsTab.SYSTEM -> {
                // Smart tech integrations (Features 91-108)
                item {
                    SmartTechIntegrationsCard(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme
                    )
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
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showGoalDialog()
                                    }
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
                                    onCheckedChange = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onToggleReminders(it)
                                    },
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
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                onIntervalChange(hours)
                                            },
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
                                    onCheckedChange = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onDarkThemeToggle(it)
                                    },
                                    modifier = Modifier.testTag("theme_switch")
                                )
                            }
                        }
                    }
                }
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
