package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SplashParticle(
    val id: Int,
    val emoji: String,
    val startX: Float, // horizontal position percentage (0.05f to 0.95f)
    val startY: Float, // starting Y offset
    val size: Float,   // Font size sp
    val speed: Float,  // speed multiplier for float-up
    val delayMs: Int   // artificial start delay
)

@Composable
fun CelebrationOverlay(
    totalIntake: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val isGoalMet = goal > 0 && totalIntake >= goal
    if (!isGoalMet) return

    val config = LocalConfiguration.current
    val screenHeight = config.screenHeightDp.dp
    
    // Auto-spawning set of emojis 🐳✨💦
    val emojis = listOf("💦", "🐳", "🌊", "✨", "🐋", "💧", "🐋", "💦", "🫧")
    val particles = remember(totalIntake) {
        List(24) { index ->
            SplashParticle(
                id = index,
                emoji = emojis[index % emojis.size],
                startX = Random.nextFloat() * 0.9f + 0.05f,
                startY = 1.1f, // start slightly offscreen at the bottom
                size = Random.nextFloat() * 20f + 20f, // 20sp to 40sp
                speed = Random.nextFloat() * 0.25f + 0.4f, // random speed multipliers
                delayMs = Random.nextInt(1500) // random stagger starts
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        particles.forEach { particle ->
            var stateY by remember { mutableStateOf(particle.startY) }
            val anim = rememberInfiniteTransition(label = "SplashFloat")
            
            // Generate a side-to-side waving effect using sine wave multiplier
            val waveOffset by anim.animateFloat(
                initialValue = 0f,
                targetValue = 2 * Math.PI.toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = Random.nextInt(1500, 3000), easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "WaveOffset"
            )

            // Staggered launch trigger to animate upwards
            LaunchedEffect(totalIntake) {
                delay(particle.delayMs.toLong())
                val start = System.currentTimeMillis()
                while (stateY > -0.2f) { // Float up until fully off-screen at the top
                    val elapsed = System.currentTimeMillis() - start
                    stateY = particle.startY - (elapsed.toFloat() / 1000f) * particle.speed
                    delay(16) // ~60fps recalculations
                }
            }

            if (stateY < particle.startY) {
                // Floating emoji rendering layer
                val waveX = Math.sin((waveOffset + (particle.id * 1.5)).toDouble()).toFloat() * 60f
                Text(
                    text = particle.emoji,
                    fontSize = particle.size.sp,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = stateY * size.height
                            translationX = (particle.startX * size.width) + waveX
                        }
                )
            }
        }
    }
}
