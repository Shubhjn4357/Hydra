package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import com.example.R
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun LiquidBottleWave(
    percentage: Float,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calmBitmap = remember(context) {
        try {
            // First decode resource with inJustDecodeBounds to find the original size
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.calm, options)
            
            // We only need a small version of the calm buddy (e.g., max 384 pixels) to prevent OOM / too large canvas errors 🐳
            val targetSize = 384
            val srcWidth = options.outWidth
            val srcHeight = options.outHeight
            
            var sampleSize = 1
            if (srcWidth > targetSize || srcHeight > targetSize) {
                val halfWidth = srcWidth / 2
                val halfHeight = srcHeight / 2
                while ((halfWidth / sampleSize) >= targetSize && (halfHeight / sampleSize) >= targetSize) {
                    sampleSize *= 2
                }
            }
            
            val decodeOptions = android.graphics.BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
            }
            
            val decoded = android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.calm, decodeOptions)
            if (decoded != null) {
                // If it's still larger than our target, we downscale it exactly!
                if (decoded.width > targetSize || decoded.height > targetSize) {
                    val scale = Math.min(targetSize.toFloat() / decoded.width, targetSize.toFloat() / decoded.height)
                    val scaledW = (decoded.width * scale).toInt().coerceAtLeast(1)
                    val scaledH = (decoded.height * scale).toInt().coerceAtLeast(1)
                    val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(decoded, scaledW, scaledH, true)
                    if (scaledBitmap != decoded) {
                        decoded.recycle()
                    }
                    scaledBitmap.asImageBitmap()
                } else {
                    decoded.asImageBitmap()
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidWaves")
    
    // Wave lateral translations
    val waveOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Wave1Translation"
    )

    val waveOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Wave2Translation"
    )

    // Breathing float animation representing character bobbing on liquid
    val bobbingOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BuddyBobbing"
    )

    val bubbleY1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Bubble1Rise"
    )

    val bubbleY2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Bubble2Rise"
    )

    val waterPrimaryColor = Color(0xFF3B82F6)   // Clean brand-identity sky-blue wave
    val waterSecondaryColor = Color(0x9E60A5FA) // Translucent lighter blue back-wave
    val bottleOutlineColor = if (isDarkTheme) Color(0x5560A5FA) else Color(0x332563EB)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        if (w > 0f && h > 0f) {
            // Define a path for the elegant bottle shape
            val bottlePath = Path().apply {
                val neckHeight = h * 0.12f
                val neckWidth = w * 0.45f
                val shoulderRadius = w * 0.2f
                val bodyCornerRadius = w * 0.15f
                
                // Draw bottle shape
                moveTo((w - neckWidth) / 2, h * 0.05f) // top left neck
                lineTo((w + neckWidth) / 2, h * 0.05f) // top right neck
                lineTo((w + neckWidth) / 2, neckHeight) // bottom right neck
                
                // Shoulder curve (right side)
                quadraticTo(
                    w * 0.95f, neckHeight,
                    w, neckHeight + shoulderRadius
                )
                
                // Body line right side
                lineTo(w, h - bodyCornerRadius)
                
                // Bottom right curve
                quadraticTo(w, h, w - bodyCornerRadius, h)
                
                // Bottom line
                lineTo(bodyCornerRadius, h)
                
                // Bottom left curve
                quadraticTo(0f, h, 0f, h - bodyCornerRadius)
                
                // Body line left side
                lineTo(0f, neckHeight + shoulderRadius)
                
                // Shoulder curve (left side)
                quadraticTo(
                    w * 0.05f, neckHeight,
                    (w - neckWidth) / 2, neckHeight
                )
                
                close()
            }

            // Clip everything inside the bottle shape
            clipPath(bottlePath) {
                
                // 1. Draw solid background reflecting bottle glass light
                val glassBgColor = if (isDarkTheme) Color(0x151E293B) else Color(0x082563EB)
                drawRect(color = glassBgColor)

                // Horizontal grid lines
                for (i in 1..3) {
                    val gridY = h * (i * 0.25f)
                    drawLine(
                        color = if (isDarkTheme) Color(0x1FFFFFFF) else Color(0x11000000),
                        start = Offset(0f, gridY),
                        end = Offset(w, gridY),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                    )
                }

                // Calculate precise vertical position of the water surface baseline
                val targetWaterY = h - (percentage * h)
                val baselineY = targetWaterY.coerceIn(0f, h + 50f)

                if (percentage > 0.01f) {
                    // Secondary background wave path
                    val wavePath2 = Path().apply {
                        moveTo(0f, h)
                        val points = 30
                        for (i in 0..points) {
                            val x = w * (i.toFloat() / points)
                            val relativeX = i.toFloat() / points
                            val waveHeight = 16f * sin((relativeX * 2 * PI.toFloat()) + waveOffset2)
                            lineTo(x, baselineY + waveHeight)
                        }
                        lineTo(w, h)
                        close()
                    }
                    drawPath(
                        path = wavePath2,
                        brush = Brush.verticalGradient(
                            colors = listOf(waterSecondaryColor, waterPrimaryColor.copy(alpha = 0.5f))
                        )
                    )

                    // Primary foreground wave path
                    val wavePath1 = Path().apply {
                        moveTo(0f, h)
                        val points = 30
                        for (i in 0..points) {
                            val x = w * (i.toFloat() / points)
                            val relativeX = i.toFloat() / points
                            val waveHeight = 24f * sin((relativeX * 2 * PI.toFloat()) + waveOffset1)
                            lineTo(x, baselineY + waveHeight)
                        }
                        lineTo(w, h)
                        close()
                    }
                    drawPath(
                        path = wavePath1,
                        brush = Brush.verticalGradient(
                            colors = listOf(waterPrimaryColor.copy(alpha = 0.9f), Color(0xFF1E3A8A))
                        )
                    )

                    // 2. Draw rising bubbles inside liquid
                    drawCircle(
                        color = Color.White.copy(alpha = 0.4f),
                        radius = 5f,
                        center = Offset(w * 0.25f, baselineY + (bubbleY1 * (h - baselineY)))
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.3f),
                        radius = 8f,
                        center = Offset(w * 0.7f, baselineY + (bubbleY2 * (h - baselineY)))
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = 4f,
                        center = Offset(w * 0.45f, baselineY + (bubbleY2 * 1.3f * (h - baselineY)).coerceIn(0f, h - baselineY))
                    )
                }

                // 3. Draw standard bobbing character (The buddy!) on waves or bottle bottom using calm.png
                val buddyY = if (percentage > 0.01f) (baselineY + bobbingOffset) else (h * 0.88f + bobbingOffset)
                if (buddyY > h * 0.12f && buddyY < h * 0.98f) {
                    if (calmBitmap != null && calmBitmap.width > 0 && calmBitmap.height > 0) {
                        val buddyWidth = w * 0.38f
                        val aspect = calmBitmap.height.toFloat() / calmBitmap.width.toFloat()
                        val buddyHeight = buddyWidth * aspect

                        if (buddyWidth > 0f && buddyHeight > 0f) {
                            val bLeft = (w - buddyWidth) / 2
                            val bTop = buddyY - buddyHeight * 0.85f // sitting on top of the waves or the bottle bottom

                            drawImage(
                                image = calmBitmap,
                                dstOffset = androidx.compose.ui.unit.IntOffset(bLeft.toInt(), bTop.toInt()),
                                dstSize = androidx.compose.ui.unit.IntSize(buddyWidth.toInt(), buddyHeight.toInt())
                            )
                        }
                    } else {
                        // Fallback to cute surfer emoji if image is empty or corrupted (prevents crash successfully)
                        val buddyEmoji = "🏄‍♂️"
                        val paint = android.graphics.Paint().apply {
                            textSize = w * 0.28f // beautifully sized relative to the bottle
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            buddyEmoji,
                            w / 2f,
                            buddyY + (w * 0.08f), // offset so he sits perfectly on top of the liquid wave
                            paint
                        )
                    }
                }
            }

            // Draw high-fidelity bottle neck cap and details (lavender/soft blue)
            val neckWidth = w * 0.45f
            val capHeight = h * 0.05f
            
            // Let's draw a nice light-blue neck band
            drawRoundRect(
                color = Color(0xFF93C5FD), // lighter blue neck band
                topLeft = Offset((w - neckWidth * 0.9f) / 2, h * 0.035f),
                size = Size(neckWidth * 0.9f, h * 0.015f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            
            // Draw the cap itself (Beautiful lavender matching the mockup perfectly!)
            drawRoundRect(
                color = Color(0xFF7E86FA), 
                topLeft = Offset((w - neckWidth * 1.1f) / 2, h * 0.01f),
                size = Size(neckWidth * 1.1f, capHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )

            // Draw the outer glass stroke representing the bottle frame borders
            drawPath(
                path = bottlePath,
                color = bottleOutlineColor,
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
        }
    }
}
