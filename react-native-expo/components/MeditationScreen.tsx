import React, { useState, useEffect, useRef } from 'react';
import { StyleSheet, Text, View, ScrollView, TouchableOpacity, Animated } from 'react-native';
import { Play, Pause, Radio } from 'lucide-react-native';
import { useTheme } from '../hooks/useTheme';

export function MeditationScreen() {
  const { colors, spacing, borderRadius } = useTheme();

  const [isPlaying, setIsPlaying] = useState(false);
  const [activeFrequency, setActiveFrequency] = useState(432);
  const scaleAnim = useRef(new Animated.Value(1)).current;

  useEffect(() => {
    let animation: Animated.CompositeAnimation | null = null;
    
    if (isPlaying) {
      animation = Animated.loop(
        Animated.sequence([
          Animated.timing(scaleAnim, {
            toValue: 1.25,
            duration: 1800,
            useNativeDriver: true,
          }),
          Animated.timing(scaleAnim, {
            toValue: 1.0,
            duration: 1800,
            useNativeDriver: true,
          })
        ])
      );
      animation.start();
    } else {
      scaleAnim.setValue(1);
    }

    return () => {
      if (animation) animation.stop();
    };
  }, [isPlaying]);

  return (
    <ScrollView contentContainerStyle={[styles.scroll, { padding: spacing.lg }]}>
      <Text style={[styles.mainHeading, { color: colors.textPrimary }]}>Binaural Solfeggio Meditation</Text>
      <Text style={[styles.subHeading, { color: colors.textSecondary }]}>
        Tune relaxing atmospheric focus waves and hear high-vibration brain-balancing frequencies.
      </Text>

      {/* Pulsating meditation focal orb */}
      <View style={[styles.orbCard, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
        <View style={styles.orbCenter}>
          <Animated.View style={[
            styles.pulseOrb,
            {
              backgroundColor: colors.activeTabIndicator,
              transform: [{ scale: scaleAnim }],
              borderColor: colors.primary,
            }
          ]} />
          
          <TouchableOpacity
            style={[styles.playBtn, { backgroundColor: colors.primary }]}
            onPress={() => setIsPlaying(!isPlaying)}
            activeOpacity={0.8}
          >
            {isPlaying ? (
              <Pause size={32} color="#FFF" />
            ) : (
              <Play size={32} color="#FFF" style={{ marginLeft: 4 }} />
            )}
          </TouchableOpacity>
        </View>

        <Text style={[styles.playbackState, { color: colors.textPrimary }]}>
          {isPlaying ? `🔴 Transmitting ${activeFrequency} Hz Brainwave Focus...` : '⏸️ Zen Atmospheric Solfeggio Idle'}
        </Text>
      </View>

      {/* Choose solfeggio focus frequency */}
      <View style={[styles.detailCard, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
        <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>Select Solfeggio Scale Channel:</Text>
        
        {[
          { hz: 174, label: '174 Hz - Dynamic Pain & Stress relief Solfeggio' },
          { hz: 285, label: '285 Hz - Tissue Healing & Rejuvenation tone' },
          { hz: 432, label: '432 Hz - Healing Earth Harmonic frequency' },
          { hz: 528, label: '528 Hz - Miracle DNA Transformation tone' },
        ].map((freqSpec) => (
          <TouchableOpacity
            key={freqSpec.hz}
            style={[
              styles.freqLine,
              {
                backgroundColor: activeFrequency === freqSpec.hz ? colors.activeTabIndicator : 'transparent',
                borderColor: activeFrequency === freqSpec.hz ? colors.primary : 'transparent',
                borderRadius: borderRadius.md
              }
            ]}
            onPress={() => setActiveFrequency(freqSpec.hz)}
          >
            <Radio size={18} color={activeFrequency === freqSpec.hz ? colors.primary : colors.textSecondary} />
            <Text style={[
              styles.freqLineText,
              {
                color: activeFrequency === freqSpec.hz ? colors.primary : colors.textPrimary,
                fontWeight: activeFrequency === freqSpec.hz ? '800' : '600'
              }
            ]}>
              {freqSpec.label}
            </Text>
          </TouchableOpacity>
        ))}
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
    padding: 32,
    borderWidth: 1.5,
    alignItems: 'center',
    marginBottom: 16,
    elevation: 2,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 5,
    shadowOffset: { width: 0, height: 2 },
  },
  orbCenter: {
    width: 180,
    height: 180,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 20,
    position: 'relative',
  },
  pulseOrb: {
    position: 'absolute',
    width: 140,
    height: 140,
    borderRadius: 70,
    borderWidth: 2.5,
    borderStyle: 'dashed',
  },
  playBtn: {
    width: 80,
    height: 80,
    borderRadius: 40,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 4,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 3 },
  },
  playbackState: {
    fontSize: 13,
    fontWeight: '800',
    textAlign: 'center',
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
  freqLine: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    padding: 12,
    borderWidth: 1.5,
    marginBottom: 8,
  },
  freqLineText: {
    fontSize: 12,
  }
});
