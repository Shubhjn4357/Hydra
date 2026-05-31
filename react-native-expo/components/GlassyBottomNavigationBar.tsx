import React from 'react';
import { StyleSheet, Text, TouchableOpacity, View, Platform } from 'react-native';
import { GlassWater, Zap, BarChart3, Radio } from 'lucide-react-native';
import { useTheme } from '../hooks/useTheme';

interface Props {
  currentScreen: 'log' | 'quick' | 'stats' | 'meditation';
  setCurrentScreen: (screen: 'log' | 'quick' | 'stats' | 'meditation') => void;
}

export function GlassyBottomNavigationBar({ currentScreen, setCurrentScreen }: Props) {
  const { colors, borderRadius } = useTheme();

  return (
    <View style={[styles.navbar, { backgroundColor: colors.cardBg, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
      {/* Tab 1: Hydration Log */}
      <TouchableOpacity
        style={styles.tab}
        onPress={() => setCurrentScreen('log')}
        activeOpacity={0.7}
      >
        <GlassWater
          size={24}
          color={currentScreen === 'log' ? colors.primary : colors.textSecondary}
        />
        <Text
          style={[
            styles.label,
            { color: currentScreen === 'log' ? colors.primary : colors.textSecondary }
          ]}
        >
          Logger
        </Text>
      </TouchableOpacity>

      {/* Tab 2: Quick Logs */}
      <TouchableOpacity
        style={styles.tab}
        onPress={() => setCurrentScreen('quick')}
        activeOpacity={0.7}
      >
        <Zap
          size={24}
          color={currentScreen === 'quick' ? colors.primary : colors.textSecondary}
        />
        <Text
          style={[
            styles.label,
            { color: currentScreen === 'quick' ? colors.primary : colors.textSecondary }
          ]}
        >
          Presets
        </Text>
      </TouchableOpacity>

      {/* Tab 3: Meditation Frequency */}
      <TouchableOpacity
        style={styles.tab}
        onPress={() => setCurrentScreen('meditation')}
        activeOpacity={0.7}
      >
        <Radio
          size={24}
          color={currentScreen === 'meditation' ? colors.primary : colors.textSecondary}
        />
        <Text
          style={[
            styles.label,
            { color: currentScreen === 'meditation' ? colors.primary : colors.textSecondary }
          ]}
        >
          Zen-Freq
        </Text>
      </TouchableOpacity>

      {/* Tab 4: Analytics */}
      <TouchableOpacity
        style={styles.tab}
        onPress={() => setCurrentScreen('stats')}
        activeOpacity={0.7}
      >
        <BarChart3
          size={24}
          color={currentScreen === 'stats' ? colors.primary : colors.textSecondary}
        />
        <Text
          style={[
            styles.label,
            { color: currentScreen === 'stats' ? colors.primary : colors.textSecondary }
          ]}
        >
          Analytics
        </Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  navbar: {
    position: 'absolute',
    bottom: Platform.OS === 'ios' ? 24 : 12,
    left: 16,
    right: 16,
    height: 64,
    borderWidth: 1.5,
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    paddingHorizontal: 12,
    elevation: 8,
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
  },
  tab: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
    height: '100%',
  },
  label: {
    fontSize: 10,
    fontWeight: '700',
    marginTop: 4,
  },
});
