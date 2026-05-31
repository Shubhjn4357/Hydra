package com.example.ui

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

class AmbientSynth {
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var synthThread: Thread? = null

    fun start() {
        if (isPlaying) return
        isPlaying = true
        
        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(1024)
        
        try {
            audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
            )

            synthThread = Thread {
                try {
                    audioTrack?.play()
                } catch (e: Exception) {
                    return@Thread
                }
                
                val buffer = ShortArray(bufferSize)
                var phase1 = 0.0
                var phase2 = 0.0
                var phase3 = 0.0
                val freq1 = 110.0 // A2 Root
                val freq2 = 165.0 // E3 Perfect Fifth
                val freq3 = 220.0 // A3 Octave
                
                // Slow LFO phase to create a gentle swelling/breathing movement in the sound
                var lfoPhase = 0.0
                val lfoFreq = 0.1 // 10 second cycle

                while (isPlaying) {
                    val lfoVol = (sin(lfoPhase * 2.0 * Math.PI) * 0.2 + 0.8) // oscillates between 0.6 and 1.0
                    
                    for (i in buffer.indices) {
                        val angle1 = phase1 * 2.0 * Math.PI
                        val angle2 = phase2 * 2.0 * Math.PI
                        val angle3 = phase3 * 2.0 * Math.PI
                        
                        // Mix frequencies beautifully for a heavenly, rich organ pad sound 🐳🎹
                        val mix = (sin(angle1) + sin(angle2) * 0.6 + sin(angle3) * 0.3)
                        val sample = (mix * 0.12 * lfoVol * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
                        buffer[i] = sample
                        
                        phase1 += freq1 / sampleRate
                        if (phase1 > 1.0) phase1 -= 1.0
                        phase2 += freq2 / sampleRate
                        if (phase2 > 1.0) phase2 -= 1.0
                        phase3 += freq3 / sampleRate
                        if (phase3 > 1.0) phase3 -= 1.0
                    }
                    
                    lfoPhase += lfoFreq / (sampleRate.toDouble() / bufferSize.toDouble())
                    if (lfoPhase > 1.0) lfoPhase -= 1.0
                    
                    audioTrack?.write(buffer, 0, buffer.size)
                }
            }
            synthThread?.start()
        } catch (e: Exception) {
            // Prevent crashes in testing platforms
        }
    }

    fun stop() {
        isPlaying = false
        try {
            synthThread?.join(500)
        } catch (e: Exception) {
            // Safe ignore
        }
        synthThread = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: java.lang.Exception) {
            // Safe ignore
        }
        audioTrack = null
    }
}

enum class BreathPhase {
    INHALE, HOLD_IN, EXHALE, HOLD_OUT
}

@Composable
fun MeditationScreen(
    onBackClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    var isMute by remember { mutableStateOf(false) }
    var isBreathingActive by remember { mutableStateOf(true) }
    
    // Synthesizer instantiation
    val synth = remember { AmbientSynth() }

    // Start synthesizer when screen opens and muting is unchecked
    DisposableEffect(isMute) {
        if (!isMute) {
            synth.start()
        } else {
            synth.stop()
        }
        onDispose {
            synth.stop()
        }
    }

    // Set up Box Breathing phase state variables
    var breathPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var durationCounter by remember { mutableStateOf(4) } // Counts down from 4s for box breathing

    // Loop through box breathing phases (Inhale, Hold, Exhale, Hold)
    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            while (true) {
                for (phase in BreathPhase.values()) {
                    breathPhase = phase
                    for (i in 4 downTo 1) {
                        durationCounter = i
                        delay(1000)
                    }
                }
            }
        }
    }

    // Gentle rotation for the spiritual meditation compass
    val infiniteTransition = rememberInfiniteTransition(label = "CompassSpin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotate"
    )

    // Animated breathing radius multiplier based on phase
    val breathingTargetScale = when (breathPhase) {
        BreathPhase.INHALE -> 1.5f
        BreathPhase.HOLD_IN -> 1.5f
        BreathPhase.EXHALE -> 0.85f
        BreathPhase.HOLD_OUT -> 0.85f
    }
    
    // Animate smoothly to match inhalation/exhalation pacing (4000ms duration per line)
    val breathingAnimatedScale by animateFloatAsState(
        targetValue = breathingTargetScale,
        animationSpec = tween(
            durationMillis = 4000,
            easing = if (breathPhase == BreathPhase.INHALE || breathPhase == BreathPhase.EXHALE) {
                FastOutSlowInEasing
            } else {
                LinearEasing
            }
        ),
        label = "BreathingScale"
    )

    val instructionText = when (breathPhase) {
        BreathPhase.INHALE -> "Inhale wetness energy... 🌬️"
        BreathPhase.HOLD_IN -> "Hold the focus... 😌"
        BreathPhase.EXHALE -> "Release all tension... 💨"
        BreathPhase.HOLD_OUT -> "Relax in stillness... 🧘‍♂️"
    }

    val bubbleGradientColors = when (breathPhase) {
        BreathPhase.INHALE -> listOf(Color(0xFF6F79FE), Color(0xFF06B6D4))
        BreathPhase.HOLD_IN -> listOf(Color(0xFF059669), Color(0xFF10B981))
        BreathPhase.EXHALE -> listOf(Color(0xFFF59E0B), Color(0xFFF43F5E))
        BreathPhase.HOLD_OUT -> listOf(Color(0xFF7C3AED), Color(0xFFC084FC))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header / Navigation block
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
                    contentDescription = "Back",
                    tint = if (isDarkTheme) Color.White else Color(0xFF2E3252),
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Hydra Respiration 🐳✨",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = if (isDarkTheme) Color.White else Color(0xFF2E3252),
                textAlign = TextAlign.Center
            )

            // Mute / Unmute dynamic synthesiser chord button
            IconButton(
                onClick = { isMute = !isMute },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isMute) Icons.Default.MusicOff else Icons.Default.MusicNote,
                    contentDescription = "Toggle Ambient Drone Soundscape 🎵",
                    tint = if (isMute) Color.Gray else Color(0xFF6F79FE),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Screen intro description
        Text(
            text = "Pranayama / Box Breathing",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDarkTheme) Color.White else Color(0xFF1E3A8A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Harmonize water intake with mindful respiration patterns. Tap below to start or pause training.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Center visual breathing animation structure
        Box(
            modifier = Modifier
                .size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            // Rotating outer decorative mandala compass
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotationAngle)
            ) {
                val cen = center
                val r = size.minDimension / 2.3f
                // Draw 18 symmetrical dots forming a beautiful sacred geometry circle matching custom design lines
                for (s in 0 until 18) {
                    val angle = (s * (2 * PI) / 18).toFloat()
                    val dx = cen.x + r * sin(angle)
                    val dy = cen.y + r * kotlin.math.cos(angle)
                    
                    drawCircle(
                        color = if (isDarkTheme) Color(0x33FFFFFF) else Color(0x221E3A8A),
                        radius = 5f,
                        center = Offset(dx, dy)
                    )
                }
                
                // Fine inner mandala guideline stroke
                drawCircle(
                    color = if (isDarkTheme) Color(0x1F94A3B8) else Color(0x1F2E3252),
                    radius = r,
                    center = cen,
                    style = Stroke(width = 1.5f)
                )
            }

            // Pulsing center sphere containing the actual instruction text
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        scaleX = breathingAnimatedScale
                        scaleY = breathingAnimatedScale
                    }
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        clip = false
                    )
                    .background(
                        brush = Brush.radialGradient(bubbleGradientColors),
                        shape = CircleShape
                    )
                    .border(
                        width = 4.dp,
                        color = Color.White.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Large visual countdown counter
                    Text(
                        text = "$durationCounter",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "seconds",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.4f))

        // Responsive dynamic status prompt cards
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDarkTheme) Color(0x156F79FE) else Color(0xFFF1F5F9),
                    RoundedCornerShape(20.dp)
                )
                .border(
                    1.dp,
                    if (isDarkTheme) Color(0x336F79FE) else Color(0xFFE2E8F0),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = instructionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Phase: ${breathPhase.name.replace("_", " ")}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Breathing active toggle/controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { isBreathingActive = !isBreathingActive },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBreathingActive) Color(0xFFF43F5E) else Color(0xFF059669)
                )
            ) {
                Icon(
                    imageVector = if (isBreathingActive) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                    contentDescription = if (isBreathingActive) "Stop training" else "Start training",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBreathingActive) "Pause Meditation" else "Resume Breathing",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.weight(0.6f))
    }
}
