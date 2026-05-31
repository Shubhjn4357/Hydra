# 🌊 Hydra — Premium Aesthetic Hydration Tracker & Respiration Sanctuary 🐳✨

**Hydra** is a beautifully responsive, modern, offline-first Water Intake Logger & Mindful Sanctuary for Android. Configured under Jetpack Compose and Material 3 design systems, it elevates daily water logging into a delightful, gamified wellness experience.

---

## 🎨 Visual Identity & Dynamic Theme

- **Frosted-Glass Aesthetic**: Symmetrical container card alignments, beautiful rounded borders, and dynamic glowing background gradients.
- **Sensory-Symmetrical Bottom Bar**: A custom, floating, glass-morphic bottom tab layout mapping **Logger 💧**, **Containers 🥤**, **Meditation 🧘‍♂️**, and **Analytics 📊**.
- **Edge-to-Edge Canvas**: Deep-slate dark design themes and vibrant sky-blue accents designed as modern dark and light layouts.

---

## 🚀 Key Production Capabilities & Unique Additions

### 1. 🐳 Splashtastic Goal Celebration Overlay
- Spawns a floating stream of animated emojis (`💦`, `🐳`, `🌊`, `✨`, `🐋`, `💧`, `🫧`) rising dynamically from the bottom of the screen upon reaching or exceeding 100% of your daily hydration goal. Perfect celebrate of milestone success!

### 2. 🥃 Personality Preset Container Tiles
- Four humorous preset containers with delightful titles:
  - **50 ml** ➔ *Teaspoon of dew* 💧
  - **250 ml** ➔ *Moderate glug-glug* 🥛
  - **500 ml** ➔ *Absolute hydrator* 🫙
  - **1000 ml** ➔ *Submarine gulp* 🐳
- A unified **Custom volume dialog 🧪** card is also included to easily log precise measurements.

### 3. 📳 Sensory Haptic Squishes
- Satisfying haptic feedback patterns (dual local click feedback combined with immediate mechanical short vibrations using the system `Vibrator`) when clicking on container buttons, making you feel the literal splash inside your phone.

### 4. 🧘‍♂️ Mindfulness Respiration Sanctum & Offline Sound Synthesis
- **Pranayama Box Breathing (4-4-4-4)**: Symmetrical visual respirator that guides your inhalation, holding pattern, exhalation, and stillness hold with smooth gradient shift animations.
- **Generative Chord Synthesizer**: A built-in, lightweight real-time audio wave generator using Kotlin's low-level `AudioTrack`. It synthesizes lush, soothing perfect-fifth root drone sounds (110Hz A2 + 165Hz E3 perfect fifth + 220Hz Octave A3 with dual sinusoidal LFO swelling) to induce instant calm. No heavy MP3 files, fully offline!

### 5. 🗄️ Room Database Persistence
- Tracks real-time log entries with sub-millisecond precision, displaying comprehensive analytics, day-by-day historic log breakdowns, and responsive stats cards.

---

## 🛠️ Architecture Summary

- **Architecture Pattern**: Modern MVVM (Model-View-ViewModel) + Single Source of Truth Repository pattern.
- **Development Tooling**: Kotlin, Jetpack Compose, Coroutines, Flow, Kotlin Symbol Processing (KSP), and the Material 3 UI design toolkit.
- **State Flow Engine**: Standard `MutableStateFlow` structures collected with type-safe `collectAsStateWithLifecycle` to prevent visual state leaks during system changes.
