package com.example.ui

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.sin

object WaterSoundSynthesizer {

    fun playGulp() {
        thread {
            val sampleRate = 22050
            val durationMs = 350
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)
            
            // Generate a gulp: a quick upward pitch sweep in a sine wave, with a decay envelope.
            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                val progress = i.toFloat() / numSamples
                
                // Frequency starts at 180Hz and sweeps up to 320Hz, then drops
                val freq = 180.0 + sin(progress * Math.PI) * 140.0
                val phase = t * freq * 2.0 * Math.PI
                
                // Volume envelope: smooth fade in, sudden rise, quick decay
                val envelope = if (progress < 0.2f) {
                    progress / 0.2f
                } else {
                    1.0f - (progress - 0.2f) / 0.8f
                }
                
                val sampleVal = (sin(phase) * 30000.0 * envelope).toInt().coerceIn(-32768, 32767).toShort()
                buffer[i] = sampleVal
            }
            
            playSound(buffer, sampleRate)
        }
    }

    fun playSplash() {
        thread {
            val sampleRate = 22050
            val durationMs = 400
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)
            
            // Splash: combines high-pitched noise drops with decaying pitch
            val random = java.util.Random()
            
            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                
                // White noise representing droplets
                val noise = random.nextFloat() * 2f - 1f
                
                // Sine wave frequency going down (bubble bursting)
                val freq = 450.0 * (1.0 - progress) + 80.0
                val t = i.toFloat() / sampleRate
                val waterTone = sin(t * freq * 2.0 * Math.PI)
                
                // Envelope decay
                val envelope = (1.0f - progress) * (1.0f - progress)
                val mixed = (noise * 0.4f + waterTone * 0.6f) * envelope
                
                val sampleVal = (mixed * 25000.0).toInt().coerceIn(-32768, 32767).toShort()
                buffer[i] = sampleVal
            }
            
            playSound(buffer, sampleRate)
        }
    }

    fun playPouring() {
        thread {
            val sampleRate = 22050
            val durationMs = 800
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)
            
            // Pouring: multiple overlapping bubble gurgles at varying frequencies
            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                val progress = i.toFloat() / numSamples
                
                // Overlapping bubble voices
                var mixed = 0.0
                
                // Voice 1 - slow gurgles
                val phase1 = (i % (sampleRate / 4)) / (sampleRate / 4.0)
                val freq1 = 200.0 + phase1 * 300.0
                mixed += sin(phase1 * freq1 * 0.05) * 0.4
                
                // Voice 2 - fast bubbles
                val phase2 = (i % (sampleRate / 8)) / (sampleRate / 8.0)
                val freq2 = 350.0 + phase2 * 450.0
                mixed += sin(phase2 * freq2 * 0.03) * 0.3
                
                // Voice 3 - random high drops
                val phase3 = (i % (sampleRate / 6)) / (sampleRate / 6.0)
                val freq3 = 500.0 + phase3 * 600.0
                mixed += sin(phase3 * freq3 * 0.02) * 0.2
                
                val envelope = if (progress < 0.1f) {
                    progress / 0.1f
                } else if (progress > 0.8f) {
                    1.5f * (1.0f - progress)
                } else {
                    1.0f
                }
                
                val sampleVal = (mixed * 22000.0 * envelope).toInt().coerceIn(-32768, 32767).toShort()
                buffer[i] = sampleVal
            }
            
            playSound(buffer, sampleRate)
        }
    }

    private fun playSound(buffer: ShortArray, sampleRate: Int) {
        try {
            val bufferSize = buffer.size * 2
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STATIC
            )
            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            
            // Release after completion
            thread {
                val durationMs = (buffer.size.toFloat() / sampleRate * 1000).toLong()
                Thread.sleep(durationMs + 150)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (e: Exception) {}
            }
        } catch (e: Exception) {
            // Safe ignore
        }
    }
}
