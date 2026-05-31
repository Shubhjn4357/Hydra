import React, { useState } from 'react';
import { StyleSheet, Text, View, ScrollView, TouchableOpacity, Switch, Dimensions } from 'react-native';
import Svg, { Circle } from 'react-native-svg';
import { Sparkles, Dumbbell, Glasses } from 'lucide-react-native';
import { useHydration } from '../context/HydrationContext';
import { useTheme } from '../hooks/useTheme';
import { LiquidThemeName } from '../theme/designTokens';

export function StatsScreen() {
  const {
    logs,
    deleteLog,
    totalIntake,
    getEffectiveGoal,
    rpgLevel,
    rpgXp,
    strength,
    intellect,
    agility,
    mysteryBubbleText,
    setMysteryBubbleText,
    wardrobeOutfit,
    setWardrobeOutfit,
    vesselSilhouette,
    setVesselSilhouette,
    activeBottleTheme,
    setActiveBottleTheme,
    isLavaLamp,
    setIsLavaLamp,
    isRaindrops,
    setIsRaindrops,
    isCoralForest,
    setIsCoralForest,
    isHotWeather,
    setIsHotWeather,
    isCoffeeTax,
    setIsCoffeeTax,
    isAltBooster,
    setIsAltBooster,
    isSodiumTax,
    setIsSodiumTax,
    isPregnancyMode,
    setIsPregnancyMode,
    isIllnessRecovery,
    setIsIllnessRecovery,
    activeSticker,
    setActiveSticker,
    resetAllData,
  } = useHydration();

  const { colors, spacing, borderRadius, isDarkTheme } = useTheme();

  // Active sub-tab state
  const [activeTab, setActiveTab] = useState<'analytics' | 'customization' | 'rpg' | 'physiology'>('analytics');
  const [jackpotResult, setJackpotResult] = useState('💦 💦 💦');

  const goal = getEffectiveGoal();
  const percentage = goal > 0 ? totalIntake / goal : 0;
  const percentText = Math.min(Math.round(percentage * 100), 999);

  // Math for compliance ring
  const radius = 80;
  const strokeWidth = 14;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - circumference * Math.min(percentage, 1.0);

  const handleSlotSpin = () => {
    const symbols = ['🐳', '💦', '💧', '🦦', '💖', '👑'];
    const s1 = symbols[Math.floor(Math.random() * symbols.length)];
    const s2 = symbols[Math.floor(Math.random() * symbols.length)];
    const s3 = symbols[Math.floor(Math.random() * symbols.length)];
    setJackpotResult(`${s1} ${s2} ${s3}`);
    if (s1 === s2 && s2 === s3) {
      setMysteryBubbleText('🎰 ULTRA HYDRO JACKPOT WINNER! Earned +500ml XP bonus! 🐳');
    } else {
      setMysteryBubbleText('Slots spun! Maintain high hydration compliance levels.');
    }
  };

  const renderAnalytics = () => {
    return (
      <View style={styles.tabContent}>
        {/* Compliance ring visualization */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary, textAlign: 'center' }]}>Compliance metrics</Text>
          
          <View style={styles.ringContainer}>
            <Svg height="180" width="180" style={styles.svgRing}>
              {/* Back track */}
              <Circle
                cx="90"
                cy="90"
                r={radius}
                fill="none"
                stroke={isDarkTheme ? '#334155' : '#E2E8F0'}
                strokeWidth={strokeWidth}
              />
              {/* Colored compliance wave fill */}
              <Circle
                cx="90"
                cy="90"
                r={radius}
                fill="none"
                stroke={colors.primary}
                strokeWidth={strokeWidth}
                strokeDasharray={circumference}
                strokeDashoffset={strokeDashoffset}
                strokeLinecap="round"
                transform="rotate(-90 90 90)"
              />
            </Svg>

            <View style={styles.ringLabelContainer}>
              <Text style={[styles.ringPercentText, { color: colors.primary }]}>{percentText}%</Text>
              <Text style={[styles.ringGoalText, { color: colors.textSecondary }]}>
                {totalIntake} / {goal} ml
              </Text>
            </View>
          </View>
        </View>

        {/* Daily Drink History List */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>Drink Logging History:</Text>
          {logs.length === 0 ? (
            <Text style={{ fontSize: 13, fontStyle: 'italic', color: colors.textSecondary }}>No logs recorded for today.</Text>
          ) : (
            logs.map((log) => (
              <View key={log.id} style={[styles.logItem, { borderColor: colors.border }]}>
                <View>
                  <Text style={[styles.logAmount, { color: colors.textPrimary }]}>{log.amount} ml Gulp</Text>
                  <Text style={[styles.logMeta, { color: colors.textSecondary }]}>Receptacle: {log.vessel} • Time: {log.time}</Text>
                </View>
                <TouchableOpacity style={styles.deleteBtn} onPress={() => deleteLog(log.id)}>
                  <Text style={styles.deleteBtnText}>Delete 🚫</Text>
                </TouchableOpacity>
              </View>
            ))
          )}
        </View>

        {/* Reset button sandbox control */}
        <TouchableOpacity 
          style={[styles.resetBtn, { borderRadius: borderRadius.lg }]} 
          onPress={resetAllData}
          activeOpacity={0.8}
        >
          <Text style={styles.resetBtnText}>Clear Cache & Start Clean 🧹</Text>
        </TouchableOpacity>
      </View>
    );
  };

  const renderCustomization = () => {
    return (
      <View style={styles.tabContent}>
        {/* Vessel outline choices */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>Glass Vessel Silhouette:</Text>
          <View style={styles.choiceRow}>
            {['Glass Jar', 'Sports Thermos', 'Fancy Teacup', 'Classic Mug'].map((v) => (
              <TouchableOpacity
                key={v}
                style={[
                  styles.choiceBtn,
                  {
                    backgroundColor: vesselSilhouette === v ? colors.primary : colors.background,
                    borderColor: colors.border,
                    borderRadius: borderRadius.md
                  }
                ]}
                onPress={() => setVesselSilhouette(v)}
              >
                <Text style={[styles.choiceBtnText, { color: vesselSilhouette === v ? '#FFF' : colors.textPrimary }]}>
                  {v}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Bottle Liquid coloring themes */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>Bottle Liquid Color Scheme:</Text>
          <View style={styles.choiceRow}>
            {['Deep Blue', 'Emerald Mint', 'Cosmic Pink', 'Golden Sun', 'Lava Orange'].map((t) => (
              <TouchableOpacity
                key={t}
                style={[
                  styles.choiceBtn,
                  {
                    backgroundColor: activeBottleTheme === t ? colors.primary : colors.background,
                    borderColor: colors.border,
                    borderRadius: borderRadius.md
                  }
                ]}
                onPress={() => setActiveBottleTheme(t as LiquidThemeName)}
              >
                <Text style={[styles.choiceBtnText, { color: activeBottleTheme === t ? '#FFF' : colors.textPrimary }]}>
                  {t}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Dynamic decorative visual sandboxes (lava bubbles, rain) */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>Environment VFX Layer Toggle:</Text>
          
          <View style={styles.switchRow}>
            <View>
              <Text style={[styles.switchLabel, { color: colors.textPrimary }]}>Lava Lamp Floating Bubbles 🌋</Text>
            </View>
            <Switch value={isLavaLamp} onValueChange={setIsLavaLamp} />
          </View>

          <View style={styles.switchRow}>
            <View>
              <Text style={[styles.switchLabel, { color: colors.textPrimary }]}>Solfeggio Zen Raindrops 🌧️</Text>
            </View>
            <Switch value={isRaindrops} onValueChange={setIsRaindrops} />
          </View>

          <View style={styles.switchRow}>
            <View>
              <Text style={[styles.switchLabel, { color: colors.textPrimary }]}>Coral Reef Forest Flora 🌿</Text>
            </View>
            <Switch value={isCoralForest} onValueChange={setIsCoralForest} />
          </View>
        </View>

        {/* Neo Sticker selection list */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>Receptacle Hologram Stickers:</Text>
          <View style={styles.choiceRow}>
            {['Otter 🦦', 'Dino 🦖', 'None 🚫'].map((st) => (
              <TouchableOpacity
                key={st}
                style={[
                  styles.choiceBtn,
                  {
                    backgroundColor: activeSticker === st ? colors.primary : colors.background,
                    borderColor: colors.border,
                    borderRadius: borderRadius.md
                  }
                ]}
                onPress={() => setActiveSticker(st)}
              >
                <Text style={[styles.choiceBtnText, { color: activeSticker === st ? '#FFF' : colors.textPrimary }]}>
                  {st}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </View>
    );
  };

  const renderRpg = () => {
    return (
      <View style={styles.tabContent}>
        {/* Virtual RPG elements */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <View style={styles.rpgRow}>
            <Text style={[styles.rpgAvatarText, { color: colors.textPrimary }]}>👾 Hydry Bot Lv. {rpgLevel}</Text>
            <Text style={[styles.rpgMeta, { color: colors.textSecondary }]}>XP: {rpgXp}/{rpgLevel * 100}</Text>
          </View>
          <View style={styles.xpBarContainer}>
            <View style={[styles.xpBarFilled, { backgroundColor: colors.primary, width: `${(rpgXp / (rpgLevel * 100)) * 100}%` }]} />
          </View>

          {/* Core attributes list */}
          <View style={styles.statsSpecRow}>
            <View style={styles.statsBox}>
              <Dumbbell size={16} color="#3B82F6" />
              <Text style={[styles.statsBoxTitle, { color: colors.textPrimary }]}>STR Strength</Text>
              <Text style={[styles.statsBoxNum, { color: colors.textPrimary }]}>{strength}</Text>
            </View>
            <View style={styles.statsBox}>
              <Glasses size={16} color="#10B981" />
              <Text style={[styles.statsBoxTitle, { color: colors.textPrimary }]}>INT Intellect</Text>
              <Text style={[styles.statsBoxNum, { color: colors.textPrimary }]}>{intellect}</Text>
            </View>
            <View style={styles.statsBox}>
              <Sparkles size={16} color="#F59E0B" />
              <Text style={[styles.statsBoxTitle, { color: colors.textPrimary }]}>AGI Agility</Text>
              <Text style={[styles.statsBoxNum, { color: colors.textPrimary }]}>{agility}</Text>
            </View>
          </View>

          <Text style={[styles.balloonSpeech, { backgroundColor: isDarkTheme ? '#1E1B4B' : '#EEF2FF', color: colors.textPrimary, borderRadius: borderRadius.md }]}>
            💬 "{mysteryBubbleText}"
          </Text>
        </View>

        {/* Spin machine games */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>🎰 Slots Spin bonus:</Text>
          <View style={styles.slotRow}>
            <Text style={styles.slotResultText}>{jackpotResult}</Text>
            <TouchableOpacity style={[styles.spinButton, { borderRadius: borderRadius.md }]} onPress={handleSlotSpin}>
              <Text style={styles.spinButtonText}>Spin 🎰</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* Bot accessory outfit choosing */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>👗 Wardrobe Wearable Accessory:</Text>
          <View style={styles.choiceRow}>
            {['None', 'Sunglasses 😎', 'Royal Crown 👑'].map((item) => (
              <TouchableOpacity
                key={item}
                style={[
                  styles.choiceBtn,
                  {
                    backgroundColor: wardrobeOutfit === item ? '#10B981' : colors.background,
                    borderColor: colors.border,
                    borderRadius: borderRadius.md
                  }
                ]}
                onPress={() => setWardrobeOutfit(item)}
              >
                <Text style={[styles.choiceBtnText, { color: wardrobeOutfit === item ? '#FFF' : colors.textPrimary }]}>
                  {item}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </View>
    );
  };

  const renderPhysiology = () => {
    return (
      <View style={styles.tabContent}>
        {/* Physiological environment adjusters checkboxes */}
        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>Meteorological weather targets:</Text>
          
          <View style={styles.switchRow}>
            <View>
              <Text style={[styles.switchLabel, { color: colors.textPrimary }]}>Enable Sunny Index 🏜️</Text>
              <Text style={[styles.switchSub, { color: colors.textSecondary }]}>Adds +500ml target due to sunshine risk</Text>
            </View>
            <Switch value={isHotWeather} onValueChange={setIsHotWeather} />
          </View>
        </View>

        <View style={[styles.card, { backgroundColor: colors.cardBgSolid, borderColor: colors.border, borderRadius: borderRadius.xl }]}>
          <Text style={[styles.cardTitle, { color: colors.textPrimary }]}>Activity & Biology Target Modifiers:</Text>
          
          <View style={styles.switchRow}>
            <View>
              <Text style={[styles.switchLabel, { color: colors.textPrimary }]}>Caffeine target tax (+250ml) ☕</Text>
              <Text style={[styles.switchSub, { color: colors.textSecondary }]}>Replenish moisture lost from tea/coffees</Text>
            </View>
            <Switch value={isCoffeeTax} onValueChange={setIsCoffeeTax} />
          </View>

          <View style={styles.switchRow}>
            <View>
              <Text style={[styles.switchLabel, { color: colors.textPrimary }]}>Altitude Target Booster (+300ml) 🏔️</Text>
              <Text style={[styles.switchSub, { color: colors.textSecondary }]}>Add buffer for thin, dry high altitudes</Text>
            </View>
            <Switch value={isAltBooster} onValueChange={setIsAltBooster} />
          </View>

          <View style={styles.switchRow}>
            <View>
              <Text style={[styles.switchLabel, { color: colors.textPrimary }]}>Sodium salt pizza tax (+300ml) 🍟</Text>
              <Text style={[styles.switchSub, { color: colors.textSecondary }]}>Rebalance cellular salt content limits</Text>
            </View>
            <Switch value={isSodiumTax} onValueChange={setIsSodiumTax} />
          </View>

          <View style={styles.switchRow}>
            <View>
              <Text style={[styles.switchLabel, { color: colors.textPrimary }]}>Pregnancy Nursing target (+600ml) 🤱</Text>
              <Text style={[styles.switchSub, { color: colors.textSecondary }]}>Provide essential nursing fluids safety</Text>
            </View>
            <Switch value={isPregnancyMode} onValueChange={setIsPregnancyMode} />
          </View>

          <View style={styles.switchRow}>
            <View>
              <Text style={[styles.switchLabel, { color: colors.textPrimary }]}>Illness Recovering (+400ml) 🤒</Text>
              <Text style={[styles.switchSub, { color: colors.textSecondary }]}>Extra moisture to clear quick infections</Text>
            </View>
            <Switch value={isIllnessRecovery} onValueChange={setIsIllnessRecovery} />
          </View>
        </View>
      </View>
    );
  };

  return (
    <ScrollView contentContainerStyle={[styles.scroll, { padding: spacing.lg }]}>
      <Text style={[styles.mainHeading, { color: colors.textPrimary }]}>Analytics & Control</Text>
      <Text style={[styles.subHeading, { color: colors.textSecondary }]}>
        Track compliancy ring records, change bottle graphics, toggle weather taxes.
      </Text>

      {/* Tabs list view */}
      <View style={[styles.tabsStrip, { borderRadius: borderRadius.lg }]}>
        {(['analytics', 'customization', 'rpg', 'physiology'] as const).map((tab) => (
          <TouchableOpacity
            key={tab}
            style={[
              styles.tabBtn,
              { 
                backgroundColor: activeTab === tab ? colors.primary : 'transparent',
                borderRadius: borderRadius.md
              }
            ]}
            onPress={() => setActiveTab(tab)}
          >
            <Text style={[styles.tabBtnText, { color: activeTab === tab ? '#FFF' : colors.textSecondary }]}>
              {tab.charAt(0).toUpperCase() + tab.slice(1)}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Dynamic sub tab layout container */}
      <View style={styles.tabsContentContainer}>
        {activeTab === 'analytics' && renderAnalytics()}
        {activeTab === 'customization' && renderCustomization()}
        {activeTab === 'rpg' && renderRpg()}
        {activeTab === 'physiology' && renderPhysiology()}
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
    marginBottom: 16,
  },
  tabsStrip: {
    flexDirection: 'row',
    backgroundColor: 'rgba(148, 163, 184, 0.1)',
    padding: 4,
    marginBottom: 18,
    justifyContent: 'space-between',
  },
  tabBtn: {
    flex: 1,
    paddingVertical: 10,
    alignItems: 'center',
  },
  tabBtnText: {
    fontSize: 10,
    fontWeight: '900',
  },
  tabsContentContainer: {
    gap: 12,
  },
  tabContent: {
    gap: 12,
  },
  card: {
    padding: 16,
    borderWidth: 1.5,
  },
  cardTitle: {
    fontSize: 14,
    fontWeight: '800',
    marginBottom: 12,
  },
  ringContainer: {
    height: 190,
    width: 190,
    alignSelf: 'center',
    position: 'relative',
    alignItems: 'center',
    justifyContent: 'center',
  },
  svgRing: {
    position: 'absolute',
  },
  ringLabelContainer: {
    position: 'absolute',
    alignItems: 'center',
    justifyContent: 'center',
  },
  ringPercentText: {
    fontSize: 38,
    fontWeight: '900',
    letterSpacing: -1,
  },
  ringGoalText: {
    fontSize: 11,
    fontWeight: '700',
    marginTop: 2,
  },
  logItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 10,
    borderBottomWidth: 1,
  },
  logAmount: {
    fontSize: 14,
    fontWeight: '800',
  },
  logMeta: {
    fontSize: 10,
    fontWeight: '600',
    marginTop: 2,
  },
  deleteBtn: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 10,
    backgroundColor: 'rgba(239, 68, 68, 0.11)',
  },
  deleteBtnText: {
    color: '#EF4444',
    fontSize: 11,
    fontWeight: '800',
  },
  choiceRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  choiceBtn: {
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderWidth: 1.5,
  },
  choiceBtnText: {
    fontSize: 11,
    fontWeight: '800',
  },
  rpgRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  rpgAvatarText: {
    fontSize: 15,
    fontWeight: '900',
  },
  rpgMeta: {
    fontSize: 12,
    fontWeight: '700',
  },
  xpBarContainer: {
    height: 10,
    backgroundColor: 'rgba(148, 163, 184, 0.15)',
    borderRadius: 5,
    overflow: 'hidden',
    marginBottom: 16,
  },
  xpBarFilled: {
    height: '100%',
  },
  statsSpecRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 8,
    marginBottom: 14,
  },
  statsBox: {
    flex: 1,
    alignItems: 'center',
    padding: 10,
    borderRadius: 12,
    backgroundColor: 'rgba(148, 163, 184, 0.08)',
  },
  statsBoxTitle: {
    fontSize: 9,
    fontWeight: '800',
    marginTop: 4,
  },
  statsBoxNum: {
    fontSize: 16,
    fontWeight: '900',
    marginTop: 2,
  },
  balloonSpeech: {
    padding: 12,
    fontSize: 12,
    fontWeight: '700',
    lineHeight: 16,
  },
  slotRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  slotResultText: {
    fontSize: 22,
    fontWeight: '900',
  },
  spinButton: {
    paddingHorizontal: 16,
    paddingVertical: 10,
    backgroundColor: '#10B981',
  },
  spinButtonText: {
    color: '#FFF',
    fontSize: 12,
    fontWeight: '800',
  },
  switchRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: 'rgba(148, 163, 184, 0.08)',
  },
  switchLabel: {
    fontSize: 13,
    fontWeight: '800',
  },
  switchSub: {
    fontSize: 9,
    fontWeight: '600',
    marginTop: 2,
    maxWidth: Dimensions.get('window').width * 0.62,
  },
  resetBtn: {
    marginTop: 10,
    height: 48,
    backgroundColor: 'rgba(239, 68, 68, 0.08)',
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: 'rgba(239, 68, 68, 0.25)',
  },
  resetBtnText: {
    color: '#EF4444',
    fontSize: 13,
    fontWeight: '800',
  }
});
