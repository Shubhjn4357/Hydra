package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.R
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun LiquidBottleWave(
    percentage: Float,
    isDarkTheme: Boolean,
    vesselSilhouette: String = "Glass Jar",
    activeTheme: String = "Deep Blue",
    glassFrosting: Float = 0.7f,
    isLavaLampEnabled: Boolean = false,
    isRaindropsEnabled: Boolean = false,
    isCoralForestEnabled: Boolean = false,
    activeSticker: String = "None 🚫",
    modifier: Modifier = Modifier
) {
    var dragStateOffset by remember { mutableStateOf(0f) }
    val animatedSloshOffset by animateFloatAsState(
        targetValue = dragStateOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "LiquidDragSlosh"
    )

    val context = LocalContext.current
    val calmBitmap = remember(context) {
        try {
            // First decode resource with inJustDecodeBounds to find the original size
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            android.graphics.BitmapFactory.decodeResource(context.resources, R.drawable.calm, options)
            
            // We only need a small version of the calm buddy (e.g., max 384 pixels) to prevent OOM
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

    // Animate custom Lava Lamp floating blobs
    val lavaLampFloat1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LavaFloat1"
    )

    val lavaLampFloat2 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LavaFloat2"
    )

    // Compute active theme colors dynamically
    val colors = remember(activeTheme) {
        val themeClean = activeTheme.lowercase()
        when {
            themeClean.contains("cosmic") || themeClean.contains("nebula") -> {
                // Cosmic Nebula ✨: Majestic purple-pink-violet gradients
                Triple(Color(0xFF8B5CF6), Color(0xC0EC4899), Color(0xFF4C1D95))
            }
            themeClean.contains("cyberpunk") || themeClean.contains("grid") -> {
                // Cyberpunk Grid 🌌: Neon Cyan, Neon Magenta, dark indigo
                Triple(Color(0xFF06B6D4), Color(0xC0D946EF), Color(0xFF1E1E38))
            }
            themeClean.contains("chameleon") || themeClean.contains("liquid") -> {
                // Chameleon Liquid 🧪: Vivid Lime, Amber Yellow, dark forest green
                Triple(Color(0xFF84CC16), Color(0xC0EAB308), Color(0xFF14532D))
            }
            themeClean.contains("aurora") || themeClean.contains("borealis") -> {
                // Aurora Borealis 🟢: Emerald Aurora, sky teal, deep space background
                Triple(Color(0xFF10B981), Color(0xC006B6D4), Color(0xFF0B132B))
            }
            themeClean.contains("pixel") -> {
                // Pixel Art 👾: 8-bit sky blue and warning blue
                Triple(Color(0xFF3B82F6), Color(0x9960A5FA), Color(0xFF1D4ED8))
            }
            themeClean.contains("monochrome") || themeClean.contains("slate") -> {
                // Monochrome Slate 🏁: Premium industrial slate gray, silver, charcoal
                Triple(Color(0xFF64748B), Color(0xC294A3B8), Color(0xFF1E293B))
            }
            themeClean.contains("coral") || themeClean.contains("sub-aquatic") -> {
                // Sub-aquatic Coral 🐠: Radiant coral pink, magenta, maroon-red reef
                Triple(Color(0xFFF43F5E), Color(0xC1F472B6), Color(0xFF4C0519))
            }
            else -> {
                // Deep Blue (Classic theme)
                Triple(Color(0xFF2563EB), Color(0x9E60A5FA), Color(0xFF1E3A8A))
            }
        }
    }

    val waterPrimaryColor = colors.first
    val waterSecondaryColor = colors.second
    val waterBottomColor = colors.third
    val bottleOutlineColor = if (isDarkTheme) Color(0x5560A5FA) else Color(0x332563EB)

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        dragStateOffset = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragStateOffset = (dragStateOffset + dragAmount).coerceIn(-120f, 120f)
                    }
                )
            }
    ) {
        val w = size.width
        val h = size.height

        if (w > 0f && h > 0f) {
            // Apply custom container bottle silhouette paths dynamically
            val bottlePath = Path().apply {
                when {
                    vesselSilhouette.contains("Sports") -> {
                        // Sports Thermos silhouette - contoured neck grip
                        val topY = h * 0.05f
                        val neckWidth = w * 0.40f
                        val neckHeight = h * 0.16f
                        val baseCorner = w * 0.12f

                        moveTo((w - neckWidth) / 2, topY)
                        lineTo((w + neckWidth) / 2, topY)
                        lineTo((w + neckWidth) / 2, neckHeight)

                        // Smoothly curved contour grip
                        cubicTo(
                            w * 0.88f, h * 0.35f,
                            w * 0.72f, h * 0.60f,
                            w * 0.92f, h * 0.85f
                        )
                        lineTo(w * 0.92f, h - baseCorner)
                        quadraticTo(w * 0.92f, h, w - baseCorner, h)
                        lineTo(baseCorner, h)
                        quadraticTo(w * 0.08f, h, w * 0.08f, h - baseCorner)
                        lineTo(w * 0.08f, h * 0.85f)
                        cubicTo(
                            w * 0.28f, h * 0.60f,
                            w * 0.12f, h * 0.35f,
                            (w - neckWidth) / 2, neckHeight
                        )
                        close()
                    }
                    vesselSilhouette.contains("Teacup") || vesselSilhouette.contains("Fancy") -> {
                        // Fancy Teacup silhouette - flared wide plate bowl
                        val topY = h * 0.25f
                        val topWidth = w * 0.95f
                        val baseWidth = w * 0.42f
                        val baseCorner = w * 0.18f

                        moveTo((w - topWidth) / 2, topY)
                        lineTo((w + topWidth) / 2, topY)
                        cubicTo(
                            w * 0.98f, h * 0.45f,
                            (w + baseWidth) / 2 + baseCorner, h,
                            (w + baseWidth) / 2, h
                        )
                        lineTo((w - baseWidth) / 2, h)
                        cubicTo(
                            (w - baseWidth) / 2 - baseCorner, h,
                            w * 0.02f, h * 0.45f,
                            (w - topWidth) / 2, topY
                        )
                        close()
                    }
                    vesselSilhouette.contains("Mug") -> {
                        // Stout Classic Mug silhouette - blocky industrial shape
                        val topY = h * 0.14f
                        val sideX = w * 0.10f
                        val borderCorner = w * 0.08f

                        moveTo(sideX, topY)
                        lineTo(w - sideX, topY)
                        lineTo(w - sideX, h - borderCorner)
                        quadraticTo(w - sideX, h, w - sideX - borderCorner, h)
                        lineTo(sideX + borderCorner, h)
                        quadraticTo(sideX, h, sideX, h - borderCorner)
                        close()
                    }
                    else -> {
                        // Standard Glass Jar / Elegant decanter
                        val neckHeight = h * 0.12f
                        val neckWidth = w * 0.45f
                        val shoulderRadius = w * 0.2f
                        val bodyCornerRadius = w * 0.15f
                        
                        moveTo((w - neckWidth) / 2, h * 0.05f)
                        lineTo((w + neckWidth) / 2, h * 0.05f)
                        lineTo((w + neckWidth) / 2, neckHeight)
                        
                        quadraticTo(
                            w * 0.95f, neckHeight,
                            w, neckHeight + shoulderRadius
                        )
                        lineTo(w, h - bodyCornerRadius)
                        quadraticTo(w, h, w - bodyCornerRadius, h)
                        lineTo(bodyCornerRadius, h)
                        quadraticTo(0f, h, 0f, h - bodyCornerRadius)
                        lineTo(0f, neckHeight + shoulderRadius)
                        quadraticTo(
                            w * 0.05f, neckHeight,
                            (w - neckWidth) / 2, neckHeight
                        )
                        close()
                    }
                }
            }

            // Draw side handles for cup-shaped silhouettes outside the clip boundary
            if (vesselSilhouette.contains("Mug")) {
                val handlePath = Path().apply {
                    moveTo(w * 0.90f, h * 0.28f)
                    cubicTo(w * 1.25f, h * 0.28f, w * 1.25f, h * 0.78f, w * 0.90f, h * 0.78f)
                }
                drawPath(
                    path = handlePath,
                    color = bottleOutlineColor,
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )
            } else if (vesselSilhouette.contains("Teacup") || vesselSilhouette.contains("Fancy")) {
                val handlePath = Path().apply {
                    moveTo(w * 0.90f, h * 0.40f)
                    cubicTo(w * 1.22f, h * 0.35f, w * 1.15f, h * 0.85f, w * 0.78f, h * 0.85f)
                }
                drawPath(
                    path = handlePath,
                    color = bottleOutlineColor,
                    style = Stroke(width = 12f, cap = StrokeCap.Round)
                )
            }

            // Clip the internal fluids and elements within the custom bottle silhouette outer boundary
            clipPath(bottlePath) {
                // Paint glass background
                val glassBgColor = if (isDarkTheme) Color(0x151E293B) else Color(0x0A2563EB)
                drawRect(color = glassBgColor)

                // Fill theme-specific custom background effects:
                val activeThemeName = activeTheme.lowercase()
                when {
                    activeThemeName.contains("cosmic") || activeThemeName.contains("nebula") -> {
                        // Shistar cosmic dust background sparkles!
                        drawCircle(Color.White.copy(alpha = 0.6f), radius = 2f, center = Offset(w * 0.3f, h * 0.2f))
                        drawCircle(Color.White.copy(alpha = 0.5f), radius = 3f, center = Offset(w * 0.7f, h * 0.4f))
                        drawCircle(Color.White.copy(alpha = 0.7f), radius = 1.5f, center = Offset(w * 0.8f, h * 0.15f))
                        drawCircle(Color.White.copy(alpha = 0.4f), radius = 2.5f, center = Offset(w * 0.2f, h * 0.65f))
                    }
                    activeThemeName.contains("cyberpunk") || activeThemeName.contains("grid") -> {
                        // Drawing retro-future scanning lines
                        for (yIdx in 1..8) {
                            val lineY = h * (yIdx * 0.12f)
                            drawLine(
                                color = Color(0x1F00F0FF),
                                start = Offset(0f, lineY),
                                end = Offset(w, lineY),
                                strokeWidth = 1f
                            )
                        }
                    }
                    activeThemeName.contains("aurora") -> {
                        // Soft green aurora glowing vertical brush paths
                        val neonB = Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color(0x1410B981), Color.Transparent),
                            start = Offset(w * 0.2f, 0f),
                            end = Offset(w * 0.8f, h)
                        )
                        drawRect(brush = neonB)
                    }
                }

                // Horizontal dashed measuring indicator lines
                for (i in 1..3) {
                    val gridY = h * (i * 0.25f)
                    drawLine(
                        color = if (isDarkTheme) Color(0x12FFFFFF) else Color(0x0E000000),
                        start = Offset(0f, gridY),
                        end = Offset(w, gridY),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                    )
                }

                // Compute precise fluid surface baseline height
                val rawWaterY = h - (percentage * h)
                // Determine the vessel's top rim limit based on shape to prevent overflow
                val vesselMinY = when {
                    vesselSilhouette.contains("Teacup") || vesselSilhouette.contains("Fancy") -> h * 0.25f
                    vesselSilhouette.contains("Mug") -> h * 0.14f
                    vesselSilhouette.contains("Sports") -> h * 0.15f
                    else -> h * 0.12f
                }
                // Offset/scale fluid surface dynamically to match custom container heights perfectly
                val baselineY = (h - percentage * (h - vesselMinY)).coerceIn(vesselMinY, h + 10f)

                // Render moving fluid waves
                if (percentage > 0.01f) {
                    val isSynthActive = AmbientSynthState.isPlayingState.value
                    val multiplier = if (isSynthActive) 2.2f else 1.0f
                    val harmonicPulse = if (isSynthActive) (sin(waveOffset1) * 6f) else 0f

                    // Secondary distant backdrop liquid waves
                    val wavePath2 = Path().apply {
                        moveTo(0f, h + 50f)
                        val points = 24
                        for (i in 0..points) {
                             val x = w * (i.toFloat() / points)
                             val rx = i.toFloat() / points
                             val sloshFactor = (sin((rx * 2 * PI.toFloat()) + waveOffset2 + (animatedSloshOffset * 0.06f)))
                             val waveHeight = (sloshFactor * 16f * multiplier) + (harmonicPulse * 0.5f)
                             lineTo(x, (baselineY + waveHeight).coerceAtLeast(vesselMinY))
                        }
                        lineTo(w, h + 50f)
                        close()
                    }
                    drawPath(
                        path = wavePath2,
                        brush = Brush.verticalGradient(
                            colors = listOf(waterSecondaryColor, waterPrimaryColor.copy(alpha = 0.45f))
                        )
                    )

                    // Primary frontmost liquid waves
                    val wavePath1 = Path().apply {
                        moveTo(0f, h + 50f)
                        val points = 24
                        for (i in 0..points) {
                            val x = w * (i.toFloat() / points)
                            val rx = i.toFloat() / points
                            val sloshFactor = (sin((rx * 2 * PI.toFloat()) + waveOffset1 + (animatedSloshOffset * 0.08f)))
                            val waveHeight = (sloshFactor * 24f * multiplier) + harmonicPulse
                            
                            // Pixelated retro-stepped waves if Pixel Art theme selected
                            val finalY = if (activeThemeName.contains("pixel")) {
                                val stepSize = 15f
                                (Math.round((baselineY + waveHeight) / stepSize) * stepSize)
                            } else {
                                baselineY + waveHeight
                            }
                            lineTo(x, finalY.coerceAtLeast(vesselMinY))
                        }
                        lineTo(w, h + 50f)
                        close()
                    }
                    drawPath(
                        path = wavePath1,
                        brush = Brush.verticalGradient(
                            colors = listOf(waterPrimaryColor, waterBottomColor)
                        )
                    )

                    // Draw sub-aquatic Coral forests at bottom of waves if activated
                    if (isCoralForestEnabled) {
                        val coralPath = Path().apply {
                            moveTo(w * 0.18f, h)
                            quadraticTo(w * 0.18f, h - h * 0.18f, w * 0.25f, h - h * 0.22f)
                            quadraticTo(w * 0.32f, h - h * 0.15f, w * 0.35f, h)
                            
                            moveTo(w * 0.60f, h)
                            quadraticTo(w * 0.68f, h - h * 0.25f, w * 0.74f, h - h * 0.21f)
                            quadraticTo(w * 0.78f, h - h * 0.14f, w * 0.82f, h)
                        }
                        drawPath(
                            path = coralPath,
                            color = Color(0x9CF43F5E), // Coral reef rose
                            style = Stroke(width = 14f, cap = StrokeCap.Round)
                        )
                    }

                    // Render lava lamp elements inside the liquid fluid body region
                    if (isLavaLampEnabled) {
                        val lavaY1 = baselineY + (h - baselineY) * lavaLampFloat1
                        val lavaY2 = baselineY + (h - baselineY) * lavaLampFloat2
                        
                        drawCircle(
                            color = Color(0x9FF43F5E),
                            radius = w * 0.12f,
                            center = Offset(w * 0.34f, lavaY1.coerceIn(baselineY, h))
                        )
                        drawCircle(
                            color = Color(0x9FFA5F0B),
                            radius = w * 0.14f,
                            center = Offset(w * 0.68f, lavaY2.coerceIn(baselineY, h))
                        )
                    } else {
                        // Standard rising hydration mineral micro-bubbles
                        drawCircle(
                            color = Color.White.copy(alpha = 0.45f),
                            radius = 6f,
                            center = Offset(w * 0.28f + animatedSloshOffset * 0.1f, (baselineY + (bubbleY1 * (h - baselineY))).coerceAtLeast(baselineY))
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.35f),
                            radius = 8f,
                            center = Offset(w * 0.72f + animatedSloshOffset * 0.15f, (baselineY + (bubbleY2 * (h - baselineY))).coerceAtLeast(baselineY))
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.5f),
                            radius = 4.5f,
                            center = Offset(w * 0.48f + animatedSloshOffset * 0.05f, (baselineY + (bubbleY2 * 1.3f * (h - baselineY))).coerceAtLeast(baselineY))
                        )
                    }
                }

                // Render falling Zen Raindrops particles across the screen height
                if (isRaindropsEnabled) {
                    val stepScale = waveOffset1 / (2 * PI.toFloat())
                    for (i in 0..6) {
                        val rx = w * (i * 0.18f + 0.05f)
                        val ry = ((h + 50f) * ((stepScale * 1.5f + i * 0.15f) % 1.0f)) - 30f
                        drawLine(
                            color = if (isDarkTheme) Color(0x4060A5FA) else Color(0x502563EB),
                            start = Offset(rx, ry),
                            end = Offset(rx - 5f, ry + 15f),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Overlaid customizable Holographic badge stickers in the upper clear section
                if (activeSticker != "None 🚫" && activeSticker.isNotBlank()) {
                    val stickerIcon = when {
                        activeSticker.contains("Otter") -> "🦦"
                        activeSticker.contains("Planet") -> "🪐"
                        activeSticker.contains("Dino") -> "🦖"
                        activeSticker.contains("Crown") -> "👑"
                        activeSticker.contains("Heart") -> "💖"
                        else -> null
                    }
                    if (stickerIcon != null) {
                        try {
                            val iconPaint = android.graphics.Paint().apply {
                                textSize = w * 0.18f
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                            }
                            drawContext.canvas.nativeCanvas.drawText(
                                stickerIcon,
                                w * 0.50f,
                                h * 0.30f,
                                iconPaint
                            )
                        } catch (e: Exception) {
                            // Safe fallback in case of native rendering issues
                        }
                    }
                }

                // Render standard bobbing character (The buddy!) seated nicely on waves
                // Clamped so that buddy floats beautifully but stays visible even when fully filled and does not go under hood
                val buddyY = if (percentage > 0.01f) {
                    (baselineY + bobbingOffset).coerceIn(vesselMinY + h * 0.04f, h * 0.92f)
                } else {
                    (h * 0.88f + bobbingOffset)
                }
                
                if (calmBitmap != null && calmBitmap.width > 0 && calmBitmap.height > 0) {
                    val buddyWidth = w * 0.38f
                    val aspect = calmBitmap.height.toFloat() / calmBitmap.width.toFloat()
                    val buddyHeight = buddyWidth * aspect

                    if (buddyWidth > 0f && buddyHeight > 0f) {
                        val bLeft = ((w - buddyWidth) / 2f + animatedSloshOffset * 0.2f).coerceIn(12f, w - buddyWidth - 12f)
                        val bTop = buddyY - buddyHeight * 0.85f // sitting on top of current waves

                        drawImage(
                            image = calmBitmap,
                            dstOffset = androidx.compose.ui.unit.IntOffset(bLeft.toInt(), bTop.toInt()),
                            dstSize = androidx.compose.ui.unit.IntSize(buddyWidth.toInt(), buddyHeight.toInt())
                        )
                    }
                } else {
                    // Fallback surfer emoji in case of bitmap loading issues
                    try {
                        val buddyEmoji = "🏄‍♂️"
                        val paint = android.graphics.Paint().apply {
                            textSize = w * 0.26f
                            textAlign = android.graphics.Paint.Align.CENTER
                            color = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            buddyEmoji,
                            w * 0.50f + animatedSloshOffset * 0.2f, // horizontally centered
                            buddyY + (w * 0.08f),
                            paint
                        )
                    } catch (e: Exception) {
                        // Safe fallback in case of native rendering issues
                    }
                }

                // Paint custom dynamic glass frosting texture override
                if (glassFrosting > 0.01f) {
                    drawPath(
                        path = bottlePath,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = glassFrosting * 0.42f),
                                Color.White.copy(alpha = glassFrosting * 0.10f)
                            ),
                            center = Offset(w / 2f, h / 2f),
                            radius = w
                        )
                    )
                }
            }

            // Draw high-fidelity accents depending on silhouette
            if (vesselSilhouette.contains("Mug")) {
                // Symmetrical open top rim lip for the Mug
                drawOval(
                    color = bottleOutlineColor,
                    topLeft = Offset(w * 0.10f, h * 0.12f),
                    size = Size(w * 0.80f, h * 0.04f),
                    style = Stroke(width = 10f)
                )
            } else if (vesselSilhouette.contains("Teacup") || vesselSilhouette.contains("Fancy")) {
                // Symmetrical open top rim lip for the Teacup
                drawOval(
                    color = bottleOutlineColor,
                    topLeft = Offset((w - w * 0.95f) / 2, h * 0.23f),
                    size = Size(w * 0.95f, h * 0.04f),
                    style = Stroke(width = 8f)
                )
                // Draw a beautiful elegant saucer plate at the very bottom base of the teacup
                val saucerPath = Path().apply {
                    moveTo(w * 0.15f, h * 0.97f)
                    lineTo(w * 0.85f, h * 0.97f)
                    quadraticTo(w * 0.92f, h * 0.97f, w * 0.89f, h * 1.01f)
                    lineTo(w * 0.11f, h * 1.01f)
                    quadraticTo(w * 0.08f, h * 0.97f, w * 0.15f, h * 0.97f)
                }
                drawPath(
                    path = saucerPath,
                    color = bottleOutlineColor,
                    style = Stroke(width = 8f, cap = StrokeCap.Round)
                )
            } else {
                // Draw high-fidelity bottle top neck caps for standard bottle silhouettes
                val neckWidth = w * 0.45f
                val capHeight = h * 0.05f
                
                // Draw elegant support band
                drawRoundRect(
                    color = Color(0xFF93C5FD),
                    topLeft = Offset((w - neckWidth * 0.9f) / 2, h * 0.035f),
                    size = Size(neckWidth * 0.9f, h * 0.015f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                
                // Draw primary metallic cap collar (Classic Lavender Slate aura)
                drawRoundRect(
                    color = Color(0xFF7E86FA),
                    topLeft = Offset((w - neckWidth * 1.1f) / 2, h * 0.01f),
                    size = Size(neckWidth * 1.1f, capHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }

            // Draw clean high-fidelity outer glass structural outline stroke
            drawPath(
                path = bottlePath,
                color = bottleOutlineColor,
                style = Stroke(width = 8.5f, cap = StrokeCap.Round)
            )
        }
    }
}
