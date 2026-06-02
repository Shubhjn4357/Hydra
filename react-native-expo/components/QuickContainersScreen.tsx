import React, { useState } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, ScrollView, TextInput, Keyboard } from 'react-native';
import { CupSoda, GlassWater, Coffee, Compass } from 'lucide-react-native';
import { useHydration } from '../context/HydrationContext';
import { useTheme } from '../hooks/useTheme';
import { haptics } from '../utils/haptics';

export function QuickContainersScreen() {
  const { addWater } = useHydration();
  const { colors, spacing, borderRadius } = useTheme();

  const [customVal, setCustomVal] = useState('');

  const presets = [
    { title: 'Delicate Sip', amount: 150, icon: Coffee, desc: 'Dainty Fancy Teacup ☕', color: '#10B981', bg: colors.background === '#0F172A' ? 'rgba(16, 185, 129, 0.15)' : '#ECFDF5' },
    { title: 'Standard Glug', amount: 250, icon: CupSoda, desc: 'Classic Office Mug 🍺', color: colors.primary, bg: colors.background === '#0F172A' ? 'rgba(59, 130, 246, 0.15)' : '#EFF6FF' },
    { title: 'Sports Quench', amount: 500, icon: GlassWater, desc: 'Gym Sports Thermos 🧴', color: '#8B5CF6', bg: colors.background === '#0F172A' ? 'rgba(139, 92, 246, 0.15)' : '#F5F3FF' },
    { title: 'Giant Gulp', amount: 1000, icon: Compass, desc: 'Ocean Submarine Gulp 🐳', color: '#F59E0B', bg: colors.background === '#0F172A' ? 'rgba(245, 158, 11, 0.15)' : '#FFFBEB' },
  ];

  const handlePresetPress = (amount: number) => {
    haptics.medium();
    addWater(amount);
  };

  const handleCustomSubmit = () => {
    const val = parseInt(customVal, 10);
    if (!isNaN(val) && val > 0) {
      haptics.doublePulse();
      addWater(val);
      setCustomVal('');
      Keyboard.dismiss();
    }
  };

  return (
    <ScrollView contentContainerStyle={[styles.scroll, { padding: spacing.lg }]}>
      <Text style={[styles.mainHeading, { color: colors.textPrimary }]}>
        Quick Log Presets
      </Text>
      <Text style={[styles.subHeading, { color: colors.textSecondary }]}>
        Tap any preset receptacle below to immediately increment your daily logs!
      </Text>

      {/* Dynamic Grid Layout */}
      <View style={styles.grid}>
        {presets.map((item, idx) => {
          const Icon = item.icon;
          return (
            <TouchableOpacity
              key={idx}
              style={[
                styles.presetCard,
                { 
                  backgroundColor: colors.cardBgSolid, 
                  borderColor: colors.border,
                  borderRadius: borderRadius.xl
                }
              ]}
              onPress={() => handlePresetPress(item.amount)}
              activeOpacity={0.8}
            >
              <View style={[styles.iconContainer, { backgroundColor: item.bg }]}>
                <Icon size={28} color={item.color} />
              </View>
              <Text style={[styles.presetTitle, { color: colors.textPrimary }]}>{item.title}</Text>
              <Text style={[styles.presetMl, { color: item.color }]}>+{item.amount} ml</Text>
              <Text style={[styles.presetDesc, { color: colors.textSecondary }]}>{item.desc}</Text>
            </TouchableOpacity>
          );
        })}
      </View>

      {/* Core Custom Entry Container form */}
      <View style={[
        styles.customSection, 
        { 
          backgroundColor: colors.cardBgSolid, 
          borderColor: colors.border,
          borderRadius: borderRadius.xl
        }
      ]}>
        <Text style={[styles.customHeading, { color: colors.textPrimary }]}>
          Log Custom Amount Receptacle 🧪
        </Text>
        <Text style={[styles.customDesc, { color: colors.textSecondary }]}>
          Enter any custom amount of liquid in milliliters to add to your daily logger score.
        </Text>

        <View style={styles.inputRow}>
          <TextInput
            style={[
              styles.input, 
              { 
                borderColor: colors.border, 
                color: colors.textPrimary,
                borderRadius: borderRadius.md
              }
            ]}
            placeholder="e.g. 350"
            placeholderTextColor={colors.textSecondary}
            keyboardType="numeric"
            value={customVal}
            onChangeText={setCustomVal}
          />
          <TouchableOpacity
            style={[styles.submitBtn, { backgroundColor: colors.primary, borderRadius: borderRadius.md }]}
            onPress={handleCustomSubmit}
            activeOpacity={0.8}
          >
            <Text style={styles.submitBtnText}>Add 💦</Text>
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
  mainHeading: {
    fontSize: 24,
    fontWeight: '900',
    letterSpacing: -0.5,
  },
  subHeading: {
    fontSize: 12,
    fontWeight: '600',
    marginTop: 4,
    marginBottom: 18,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
    marginBottom: 20,
  },
  presetCard: {
    width: '48%',
    borderWidth: 1.5,
    padding: 14,
    alignItems: 'center',
    elevation: 2,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 5,
    shadowOffset: { width: 0, height: 2 },
  },
  iconContainer: {
    width: 60,
    height: 60,
    borderRadius: 30,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 10,
  },
  presetTitle: {
    fontSize: 14,
    fontWeight: '800',
  },
  presetMl: {
    fontSize: 18,
    fontWeight: '900',
    marginVertical: 4,
  },
  presetDesc: {
    fontSize: 9,
    fontWeight: '700',
    textAlign: 'center',
  },
  customSection: {
    padding: 16,
    borderWidth: 1.5,
    elevation: 2,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 5,
    shadowOffset: { width: 0, height: 2 },
  },
  customHeading: {
    fontSize: 15,
    fontWeight: '800',
    marginBottom: 4,
  },
  customDesc: {
    fontSize: 11,
    fontWeight: '600',
    marginBottom: 12,
  },
  inputRow: {
    flexDirection: 'row',
    gap: 10,
  },
  input: {
    flex: 1,
    height: 48,
    borderWidth: 1.5,
    paddingHorizontal: 16,
    fontSize: 15,
    fontWeight: '700',
  },
  submitBtn: {
    width: 100,
    height: 48,
    alignItems: 'center',
    justifyContent: 'center',
  },
  submitBtnText: {
    color: '#FFF',
    fontSize: 14,
    fontWeight: '800',
  },
});
