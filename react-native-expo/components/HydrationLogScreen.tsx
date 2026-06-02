import React from 'react';
import { StyleSheet, Text, View, TouchableOpacity, ScrollView } from 'react-native';
import { LiquidBottleWave } from './LiquidBottleWave';
import { Plus, Flame } from 'lucide-react-native';
import { useHydration } from '../context/HydrationContext';
import { useTheme } from '../hooks/useTheme';
import { haptics } from '../utils/haptics';

export function HydrationLogScreen() {
  const { totalIntake, getEffectiveGoal, addWater } = useHydration();
  const { colors, spacing, borderRadius } = useTheme();

  const goal = getEffectiveGoal();
  const percentage = goal > 0 ? Math.min(totalIntake / goal, 1.2) : 0;
  const percentageDisplay = Math.round(percentage * 100);

  const handleQuickLog = (amount: number) => {
    haptics.medium();
    addWater(amount);
  };

  return (
    <ScrollView contentContainerStyle={[styles.scroll, { padding: spacing.lg }]}>
      {/* Header Info */}
      <View style={styles.header}>
        <View style={styles.headerTitleGroup}>
          <Text style={[styles.mainHeading, { color: colors.textPrimary }]}>
            Current Hydration
          </Text>
          <Text style={[styles.subHeading, { color: colors.textSecondary }]}>
            Tap options below or log presets to hydrate!
          </Text>
        </View>
        <TouchableOpacity style={[
          styles.badge, 
          { 
            backgroundColor: colors.activeTabIndicator, 
            borderColor: colors.primary 
          }
        ]}>
          <Flame size={16} color={colors.primary} />
          <Text style={[styles.badgeText, { color: colors.primary }]}>5 Day Streak</Text>
        </TouchableOpacity>
      </View>

      {/* Numerical Metrics Summary Block */}
      <View style={[styles.metricCard, { backgroundColor: colors.cardBg, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
        <View style={styles.metricRow}>
          <View>
            <Text style={[styles.metricLabel, { color: colors.textSecondary }]}>Consumed</Text>
            <View style={styles.volumeGroup}>
              <Text style={[styles.metricMain, { color: colors.primary }]}>
                {totalIntake.toLocaleString()}
              </Text>
              <Text style={[styles.metricUnit, { color: colors.textSecondary }]}> ml</Text>
            </View>
          </View>

          <View style={styles.divider} />

          <View>
            <Text style={[styles.metricLabel, { color: colors.textSecondary }]}>Goal</Text>
            <View style={styles.volumeGroup}>
              <Text style={[styles.metricMain, { color: colors.textPrimary }]}>
                {goal.toLocaleString()}
              </Text>
              <Text style={[styles.metricUnit, { color: colors.textSecondary }]}> ml</Text>
            </View>
          </View>

          <View style={styles.divider} />

          <View style={styles.percentAlign}>
            <Text style={[styles.metricLabel, { color: colors.textSecondary }]}>Reached</Text>
            <View style={[styles.percentBadge, { backgroundColor: colors.primary, borderRadius: borderRadius.md }]}>
              <Text style={styles.percentText}>{percentageDisplay}%</Text>
            </View>
          </View>
        </View>
      </View>

      {/* Main visual bottled water levels */}
      <View style={styles.bottleContainer}>
        <LiquidBottleWave percentage={percentage} />
      </View>

      {/* Fast Shortcuts logging actions */}
      <View style={styles.quickBarContainer}>
        <Text style={[styles.sectionTitle, { color: colors.textPrimary, marginBottom: spacing.sm }]}>
          Current receptacle quick logs:
        </Text>
        <View style={styles.shortcutRow}>
          <TouchableOpacity
            style={[styles.shortcutBtn, { backgroundColor: colors.primary, borderRadius: borderRadius.lg }]}
            onPress={() => handleQuickLog(250)}
            activeOpacity={0.8}
          >
            <Plus size={20} color="#fff" />
            <Text style={styles.shortcutBtnText}>+250ml Cup 🥛</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.shortcutBtn, { backgroundColor: '#10B981', borderRadius: borderRadius.lg }]}
            onPress={() => handleQuickLog(500)}
            activeOpacity={0.8}
          >
            <Plus size={20} color="#fff" />
            <Text style={styles.shortcutBtnText}>+500ml Bottle 🧴</Text>
          </TouchableOpacity>
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scroll: {
    paddingBottom: 40,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 16,
  },
  headerTitleGroup: {
    maxWidth: '65%',
  },
  mainHeading: {
    fontSize: 26,
    fontWeight: '900',
    letterSpacing: -0.5,
  },
  subHeading: {
    fontSize: 12,
    fontWeight: '600',
    marginTop: 2,
  },
  badge: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderWidth: 1.5,
    borderRadius: 14,
  },
  badgeText: {
    fontSize: 10,
    fontWeight: '900',
    marginLeft: 4,
  },
  metricCard: {
    padding: 16,
    borderWidth: 1.5,
    marginBottom: 12,
    elevation: 3,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 2 },
  },
  metricRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  metricLabel: {
    fontSize: 9,
    fontWeight: '800',
    textTransform: 'uppercase',
    letterSpacing: 0.5,
    marginBottom: 4,
  },
  volumeGroup: {
    flexDirection: 'row',
    alignItems: 'baseline',
  },
  metricMain: {
    fontSize: 22,
    fontWeight: '900',
    letterSpacing: -0.5,
  },
  metricUnit: {
    fontSize: 12,
    fontWeight: '700',
  },
  divider: {
    width: 1,
    height: 32,
    backgroundColor: 'rgba(100, 116, 139, 0.15)',
  },
  percentAlign: {
    alignItems: 'center',
  },
  percentBadge: {
    paddingHorizontal: 10,
    paddingVertical: 4,
  },
  percentText: {
    color: '#FFF',
    fontSize: 12,
    fontWeight: '900',
  },
  bottleContainer: {
    alignItems: 'center',
    marginVertical: 4,
  },
  quickBarContainer: {
    marginTop: 12,
  },
  sectionTitle: {
    fontSize: 13,
    fontWeight: '800',
    letterSpacing: -0.2,
  },
  shortcutRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 12,
  },
  shortcutBtn: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    height: 48,
    elevation: 2,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 5,
    shadowOffset: { width: 0, height: 2 },
  },
  shortcutBtnText: {
    color: '#FFF',
    fontSize: 13,
    fontWeight: '800',
    marginLeft: 6,
  },
});
