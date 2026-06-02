import React, { useState, useEffect, useRef } from 'react';
import { StyleSheet, Text, View, ScrollView, TouchableOpacity, Animated, Easing, Dimensions } from 'react-native';
import { Play, Pause, Compass, Waves, Flame, Sparkles, Wind, Lightbulb } from 'lucide-react-native';
import { useTheme } from '../hooks/useTheme';
import { haptics } from '../utils/haptics';

interface Soundscape {
  id: string;
  hz: number;
  name: string;
  desc: string;
  icon: any;
  toneColor: string;
  pulseSpeed: number; // in ms
}

const SOUNDSCAPES: Soundscape[] = [
  {
    id: 'cosmic',
    hz: 432,
    name: 'Cosmic Earth Resonance',
    desc: 'Deep earthly grounding tone to align body cells with natural frequencies 🌍',
    icon: Compass,
    toneColor: '#10B981', // Emerald
    pulseSpeed: 1000,
  },
  {
    id: 'miracle',
    hz: 528,
    name: 'DNA Miracle Core',
    desc: 'Golden harmonic vibration for continuous cellular rejuvenation & focus ✨',
    icon: Flame,
    toneColor: '#F59E0B', // Sun gold
    pulseSpeed: 800,
  },
  {
    id: 'ocean',
    hz: 396,
    name: 'Abyssal Deep Ocean',
    desc: 'Soothing waves and low-frequency drone to dissolve anxiety & fear 🌊',
    icon: Waves,
    toneColor: '#3B82F6', // Blue
    pulseSpeed: 1200,
  },
  {
    id: 'wind',
    hz: 639,
    name: 'Harmonic Relationship',
    desc: 'Celestial breeze and warm ambient winds to foster inner connections 🍃',
    icon: Wind,
    toneColor: '#8B5CF6', // Purple
    pulseSpeed: 900,
  },
  {
    id: 'aura',
    hz: 741,
    name: 'Intuitive Clear Mind',
    desc: 'High-frequency crystal chime fields to cleanse brain fog & activate ideas 💡',
    icon: Lightbulb,
    toneColor: '#06B6D4', // Cyan
    pulseSpeed: 700,
  }
];

type BreathPhase = 'inhale' | 'holdFull' | 'exhale' | 'holdEmpty';

export function MeditationScreen() {
  const { colors, spacing, borderRadius, isDarkTheme } = useTheme();

  const [isPlaying, setIsPlaying] = useState(false);
  const [activeSound, setActiveSound] = useState<Soundscape>(SOUNDSCAPES[0]);
  const [breathPhase, setBreathPhase] = useState<BreathPhase>('holdEmpty');
  const [timeLeft, setTimeLeft] = useState(4); // 4-second cycles

  // Animation values
  const scaleAnim = useRef(new Animated.Value(1)).current;
  const rotateAnim = useRef(new Animated.Value(0)).current;

  // Track state for transitions
  const isPlayingRef = useRef(isPlaying);
  isPlayingRef.current = isPlaying;

  const activeSoundRef = useRef(activeSound);
  activeSoundRef.current = activeSound;

  const breathPhaseRef = useRef(breathPhase);
  breathPhaseRef.current = breathPhase;

  // 1. Continuous rotation of outer ring
  useEffect(() => {
    let rotationLoop: Animated.CompositeAnimation | null = null;
    
    if (isPlaying) {
      rotationLoop = Animated.loop(
        Animated.timing(rotateAnim, {
          toValue: 1,
          duration: 10000,
          easing: Easing.linear,
          useNativeDriver: true,
        })
      );
      rotationLoop.start();
    } else {
      rotateAnim.setValue(0);
    }

    return () => {
      if (rotationLoop) {
        rotationLoop.stop();
      }
    };
  }, [isPlaying]);

  // 2. Breath Cycle countdown and transition trigger
  useEffect(() => {
    let interval: NodeJS.Timeout | null = null;

    if (isPlaying) {
      // Promptly start breathing from "inhale" upon play pressing!
      setBreathPhase('inhale');
      setTimeLeft(4);
      haptics.breathCue();

      interval = setInterval(() => {
        setTimeLeft((prev) => {
          if (prev <= 1) {
            // Move to next breath phase
            const current = breathPhaseRef.current;
            let next: BreathPhase = 'inhale';
            
            if (current === 'inhale') {
              next = 'holdFull';
            } else if (current === 'holdFull') {
              next = 'exhale';
            } else if (current === 'exhale') {
              next = 'holdEmpty';
            } else if (current === 'holdEmpty') {
              next = 'inhale';
            }

            setBreathPhase(next);
            haptics.breathCue();
            return 4; // Reset to 4s
          }
          return prev - 1;
        });
      }, 1000);
    } else {
      setBreathPhase('holdEmpty');
      setTimeLeft(4);
    }

    return () => {
      if (interval) {
        clearInterval(interval);
      }
    };
  }, [isPlaying]);

  // 3. Scale animations linked to the current Breath Phase
  useEffect(() => {
    if (!isPlaying) {
      Animated.spring(scaleAnim, {
        toValue: 1,
        tension: 40,
        friction: 8,
        useNativeDriver: true,
      }).start();
      return;
    }

    let targetScale = 1.0;
    let duration = 4000;

    switch (breathPhase) {
      case 'inhale':
        targetScale = 1.45;
        duration = 4000;
        break;
      case 'holdFull':
        targetScale = 1.45;
        duration = 4000;
        break;
      case 'exhale':
        targetScale = 1.0;
        duration = 4000;
        break;
      case 'holdEmpty':
        targetScale = 1.0;
        duration = 4000;
        break;
    }

    Animated.timing(scaleAnim, {
      toValue: targetScale,
      duration: duration,
      easing: Easing.bezier(0.25, 1, 0.5, 1),
      useNativeDriver: true,
    }).start();

  }, [breathPhase, isPlaying]);

  const handlePlayToggle = () => {
    haptics.doublePulse();
    setIsPlaying(!isPlaying);
  };

  const selectSoundscape = (sound: Soundscape) => {
    haptics.light();
    setActiveSound(sound);
  };

  // Interpolate continuous rotation values
  const rotateStr = rotateAnim.interpolate({
    inputRange: [0, 1],
    outputRange: ['0deg', '360deg'],
  });

  // Dynamic colors & text based on current breathing state
  const getPhaseText = () => {
    if (!isPlaying) return 'Atmospheric Zen Soundscape Idle';
    switch (breathPhase) {
      case 'inhale': return 'Inhale Deeply... 🌊';
      case 'holdFull': return 'Hold with Clarity... 🧘';
      case 'exhale': return 'Exhale Tension... 💨';
      case 'holdEmpty': return 'Rest and Clear... ✨';
    }
  };

  const getPhaseColor = () => {
    if (!isPlaying) return colors.primary;
    switch (breathPhase) {
      case 'inhale': return activeSound.toneColor;
      case 'holdFull': return '#10B981'; // Green energy
      case 'exhale': return colors.primary; // Custom theme primary
      case 'holdEmpty': return '#8B5CF6'; // Spiritual purple
    }
  };

  return (
    <ScrollView contentContainerStyle={[styles.scroll, { padding: spacing.lg }]}>
      <Text style={[styles.mainHeading, { color: colors.textPrimary }]}>Mindfulness Respiration</Text>
      <Text style={[styles.subHeading, { color: colors.textSecondary }]}>
        Synchronize your breathing cycle with high-vibrational solfeggio sound fields.
      </Text>

      {/* RETAINED RESPIRATION COMPONENT */}
      <View style={[styles.orbCard, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
        
        {/* Simple & Robust Nested Rotating Ring System */}
        <View style={styles.ringUniverse}>
          
          {/* Outer rotating decorative dashes ring */}
          <Animated.View style={[
            styles.outerRotatingRing,
            {
              borderColor: getPhaseColor(),
              transform: [{ rotate: rotateStr }],
              opacity: isPlaying ? 0.85 : 0.45,
            }
          ]} />

          {/* Middle pulsating resonance ring */}
          <Animated.View style={[
            styles.middlePulsingRing,
            {
              borderColor: getPhaseColor(),
              transform: [{ scale: scaleAnim }],
              backgroundColor: isDarkTheme ? 'rgba(30, 41, 59, 0.25)' : 'rgba(226, 232, 240, 0.35)',
              opacity: isPlaying ? 0.9 : 0.4,
            }
          ]} />

          {/* Inner breathing state container with tactile play control */}
          <View style={[styles.coreInteractiveCircle, { backgroundColor: isDarkTheme ? '#1E293B' : '#FFFFFF' }]}>
            <View style={styles.centerLabels}>
              {isPlaying ? (
                <>
                  <Text style={[styles.countdownSec, { color: getPhaseColor() }]}>{timeLeft}s</Text>
                  <Text style={[styles.phaseName, { color: colors.textPrimary }]}>
                    {breathPhase === 'inhale' && 'INHALE'}
                    {breathPhase === 'holdFull' && 'HOLD'}
                    {breathPhase === 'exhale' && 'EXHALE'}
                    {breathPhase === 'holdEmpty' && 'REST'}
                  </Text>
                </>
              ) : (
                <Text style={[styles.idleHint, { color: colors.textSecondary }]}>Ready</Text>
              )}
            </View>

            <TouchableOpacity
              style={[styles.playBtn, { backgroundColor: getPhaseColor() }]}
              onPress={handlePlayToggle}
              activeOpacity={0.85}
            >
              {isPlaying ? (
                <Pause size={28} color="#FFF" />
              ) : (
                <Play size={28} color="#FFF" style={{ marginLeft: 3 }} />
              )}
            </TouchableOpacity>
          </View>
        </View>

        {/* Live Active Status Banner */}
        <View style={styles.statusBanner}>
          <Text style={[styles.playbackState, { color: colors.textPrimary }]}>
            {getPhaseText()}
          </Text>
          <Text style={[styles.activeSolfeggioTone, { color: colors.textSecondary }]}>
            Current frequency wave: <Text style={{ color: getPhaseColor(), fontWeight: '900' }}>{activeSound.hz} Hz</Text>
          </Text>
        </View>
      </View>

      {/* SOLFEGGIO WAVE LISTENER SELECTION */}
      <View style={[styles.detailCard, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
        <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>Select Solfeggio Soundscape Wave:</Text>
        
        {SOUNDSCAPES.map((sound) => {
          const SoundIcon = sound.icon;
          const isSelected = activeSound.id === sound.id;
          return (
            <TouchableOpacity
              key={sound.id}
              style={[
                styles.soundscapeLine,
                {
                  backgroundColor: isSelected ? 'rgba(148, 163, 184, 0.08)' : 'transparent',
                  borderColor: isSelected ? sound.toneColor : 'transparent',
                  borderRadius: borderRadius.md,
                }
              ]}
              onPress={() => selectSoundscape(sound)}
            >
              <View style={[styles.iconBox, { backgroundColor: isSelected ? sound.toneColor : 'rgba(148, 163, 184, 0.12)' }]}>
                <SoundIcon size={18} color={isSelected ? '#FFF' : colors.textPrimary} />
              </View>
              <View style={styles.soundInfo}>
                <View style={styles.toneMetaGroup}>
                  <Text style={[
                    styles.soundName,
                    {
                      color: colors.textPrimary,
                      fontWeight: isSelected ? '800' : '600'
                    }
                  ]}>
                    {sound.name}
                  </Text>
                  <View style={[styles.hzBadge, { backgroundColor: sound.toneColor }]}>
                    <Text style={styles.hzBadgeText}>{sound.hz}Hz</Text>
                  </View>
                </View>
                <Text style={[styles.soundDesc, { color: colors.textSecondary }]}>
                  {sound.desc}
                </Text>
              </View>
            </TouchableOpacity>
          );
        })}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll: {
    paddingBottom: 40,
  },
  mainHeading: {
    fontSize: 22,
    fontWeight: '900',
    letterSpacing: -0.5,
  },
  subHeading: {
    fontSize: 12,
    fontWeight: '600',
    marginTop: 4,
    marginBottom: 20,
  },
  orbCard: {
    paddingVertical: 32,
    paddingHorizontal: 16,
    borderWidth: 1.5,
    alignItems: 'center',
    marginBottom: 16,
    elevation: 2,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 5,
    shadowOffset: { width: 0, height: 2 },
  },
  ringUniverse: {
    width: 210,
    height: 210,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 20,
    position: 'relative',
  },
  outerRotatingRing: {
    position: 'absolute',
    width: 200,
    height: 200,
    borderRadius: 100,
    borderWidth: 2,
    borderStyle: 'dashed',
  },
  middlePulsingRing: {
    position: 'absolute',
    width: 140,
    height: 140,
    borderRadius: 70,
    borderWidth: 3,
  },
  coreInteractiveCircle: {
    width: 100,
    height: 100,
    borderRadius: 50,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 4,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 6,
    shadowOffset: { width: 0, height: 3 },
    zIndex: 10,
    position: 'relative',
  },
  centerLabels: {
    alignItems: 'center',
    marginBottom: 4,
  },
  countdownSec: {
    fontSize: 24,
    fontWeight: '900',
    letterSpacing: -1,
  },
  phaseName: {
    fontSize: 8,
    fontWeight: '800',
    letterSpacing: 1,
    marginTop: -2,
  },
  idleHint: {
    fontSize: 16,
    fontWeight: '800',
  },
  playBtn: {
    width: 44,
    height: 44,
    borderRadius: 22,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 3,
    shadowColor: '#000',
    shadowOpacity: 0.15,
    shadowRadius: 4,
    shadowOffset: { width: 0, height: 2 },
  },
  statusBanner: {
    alignItems: 'center',
    marginTop: 8,
  },
  playbackState: {
    fontSize: 13,
    fontWeight: '800',
    textAlign: 'center',
    marginBottom: 2,
  },
  activeSolfeggioTone: {
    fontSize: 10,
    fontWeight: '600',
  },
  detailCard: {
    padding: 16,
    borderWidth: 1.5,
  },
  cardTitle: {
    fontSize: 14,
    fontWeight: '800',
    marginBottom: 12,
  },
  soundscapeLine: {
    flexDirection: 'row',
    alignItems: 'center',
    alignSelf: 'stretch',
    padding: 10,
    borderWidth: 1.5,
    marginBottom: 8,
    gap: 12,
  },
  iconBox: {
    width: 38,
    height: 38,
    borderRadius: 10,
    alignItems: 'center',
    justifyContent: 'center',
  },
  soundInfo: {
    flex: 1,
  },
  toneMetaGroup: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 2,
  },
  soundName: {
    fontSize: 12,
  },
  hzBadge: {
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 6,
  },
  hzBadgeText: {
    color: '#FFF',
    fontSize: 8,
    fontWeight: '900',
  },
  soundDesc: {
    fontSize: 9,
    fontWeight: '600',
    lineHeight: 12,
  }
});
