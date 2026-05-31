import React, { useEffect, useState } from 'react';
import { StyleSheet, View, Text } from 'react-native';
import Svg, { Path, Circle, G } from 'react-native-svg';
import { useTheme } from '../hooks/useTheme';
import { useHydration } from '../context/HydrationContext';

interface Props {
  percentage: number;
}

export function LiquidBottleWave({ percentage }: Props) {
  const { colors, isDarkTheme } = useTheme();
  const {
    vesselSilhouette,
    activeSticker,
    isLavaLamp,
    isRaindrops,
    isCoralForest,
  } = useHydration();

  // Animate fluid level waves
  const [waveOffset, setWaveOffset] = useState(0);

  useEffect(() => {
    let animationId: number;
    const animate = () => {
      setWaveOffset((prev) => (prev + 0.08) % (2 * Math.PI));
      animationId = requestAnimationFrame(animate);
    };
    animate();
    return () => cancelAnimationFrame(animationId);
  }, []);

  // Bottled Symmetrical workspace sizes (width 260, height 400)
  const w = 260;
  const h = 400;

  // Compute fluid level baseline Y
  const rimLimit = vesselSilhouette.includes('Teacup') || vesselSilhouette.includes('Fancy')
    ? h * 0.28
    : vesselSilhouette.includes('Mug')
    ? h * 0.18
    : h * 0.14;

  const baselineY = h - (percentage * (h - rimLimit - 12));
  const clampedBaselineY = Math.max(rimLimit, Math.min(baselineY, h - 8));

  const strokeColor = isDarkTheme ? '#475569' : '#334155';
  const containerBg = isDarkTheme ? 'rgba(30, 41, 59, 0.4)' : 'rgba(255, 255, 255, 0.35)';

  // Build the wave paths for double layering effect
  const buildWavePath = (offsetMultiplier: number, waveHeight: number) => {
    let path = `M 0 ${h} L 0 ${clampedBaselineY}`;
    const points = 16;
    for (let i = 0; i <= points; i++) {
      const rx = i / points;
      const x = w * rx;
      const slosh = Math.sin(rx * 2 * Math.PI + waveOffset * offsetMultiplier);
      const y = clampedBaselineY + slosh * waveHeight * (percentage > 0.95 ? 0.2 : 1.0);
      path += ` L ${x} ${Math.max(rimLimit, y)}`;
    }
    path += ` L ${w} ${h} Z`;
    return path;
  };

  const mainWave = buildWavePath(1.5, 8);
  const secondaryWave = buildWavePath(-1.2, 5);

  // Bobbing float motion
  const bobbing = Math.sin(waveOffset * 1.5) * 4;
  const buddyY = percentage > 0.01 
    ? Math.max(rimLimit + 25, Math.min(clampedBaselineY + bobbing - 20, h - 50))
    : h - 55 + bobbing;

  const renderSticker = () => {
    if (activeSticker === 'None' || activeSticker === 'None 🚫') return null;
    const stickerText = activeSticker.split(' ')[0] || '🦦';
    return (
      <View style={[styles.sticker, { top: h * 0.52, left: w * 0.5 - 24 }]}>
        <Text style={{ fontSize: 32 }}>{stickerText}</Text>
      </View>
    );
  };

  const renderContainerVisuals = () => {
    if (vesselSilhouette.includes('Mug')) {
      return (
        <Svg height={h} width={w} style={StyleSheet.absoluteFill}>
          <Path
            d={`M ${w * 0.85} ${h * 0.3} Q ${w * 1.15} ${h * 0.5} ${w * 0.85} ${h * 0.75}`}
            fill="none"
            stroke={strokeColor}
            strokeWidth={14}
            strokeLinecap="round"
          />
        </Svg>
      );
    } else if (vesselSilhouette.includes('Teacup') || vesselSilhouette.includes('Fancy')) {
      return (
        <Svg height={h} width={w} style={StyleSheet.absoluteFill}>
          <Path
            d={`M ${w * 0.88} ${h * 0.45} Q ${w * 1.16} ${h * 0.6} ${w * 0.88} ${h * 0.75}`}
            fill="none"
            stroke={strokeColor}
            strokeWidth={10}
            strokeLinecap="round"
          />
          <Path
            d={`M ${w * 0.12} ${h * 0.96} Q ${w * 0.5} ${h * 1.02} ${w * 0.88} ${h * 0.96}`}
            fill="none"
            stroke={strokeColor}
            strokeWidth={10}
            strokeLinecap="round"
          />
        </Svg>
      );
    }
    return null;
  };

  return (
    <View style={[styles.container, { width: w, height: h }]}>
      {renderContainerVisuals()}

      <View style={[
        styles.silhouettedClip,
        {
          borderColor: strokeColor,
          backgroundColor: containerBg,
          borderRadius: vesselSilhouette.includes('Thermos') ? 50 : vesselSilhouette.includes('Teacup') ? 110 : 30,
          borderTopRightRadius: vesselSilhouette.includes('Thermos') ? 25 : vesselSilhouette.includes('Teacup') ? 110 : 30,
          borderTopLeftRadius: vesselSilhouette.includes('Thermos') ? 25 : vesselSilhouette.includes('Teacup') ? 110 : 30,
        }
      ]}>
        
        <Svg height="100%" width="100%" style={StyleSheet.absoluteFill}>
          {percentage > 0.01 && (
            <G>
              <Path d={secondaryWave} fill={colors.secondary} opacity={0.65} />
              <Path d={mainWave} fill={colors.primary} />
            </G>
          )}

          {isCoralForest && percentage > 0.15 && (
            <G opacity={0.4}>
              <Path d={`M ${w * 0.2} ${h} Q ${w * 0.22} ${h - 40} ${w * 0.18} ${h - 60}`} stroke="#34D399" strokeWidth={5} fill="none" />
              <Path d={`M ${w * 0.8} ${h} Q ${w * 0.78} ${h - 50} ${w * 0.82} ${h - 85}`} stroke="#6EE7B7" strokeWidth={6} fill="none" />
              <Circle cx={w * 0.3} cy={h - 35} r={3} fill="#fff" />
              <Circle cx={w * 0.7} cy={h - 55} r={4} fill="#fff" />
            </G>
          )}

          {isLavaLamp && percentage > 0.2 && (
            <G opacity={0.7}>
              <Circle cx={w * 0.4} cy={clampedBaselineY + 45 + Math.sin(waveOffset * 0.8) * 15} r={10} fill="#fff" />
              <Circle cx={w * 0.65} cy={clampedBaselineY + 85 + Math.cos(waveOffset * 0.6) * 18} r={16} fill="rgba(255,255,255,0.4)" />
              <Circle cx={w * 0.25} cy={clampedBaselineY + 115 + Math.sin(waveOffset * 1.1) * 12} r={8} fill="rgba(255,255,255,0.7)" />
            </G>
          )}

          {isRaindrops && (
            <G opacity={0.6}>
              <Path d={`M ${w * 0.3} ${15 + (waveOffset * 35) % 110} L ${w * 0.3} ${28 + (waveOffset * 35) % 110}`} stroke={colors.secondary} strokeWidth={2.5} strokeLinecap="round" />
              <Path d={`M ${w * 0.6} ${45 + (waveOffset * 40) % 100} L ${w * 0.6} ${58 + (waveOffset * 40) % 100}`} stroke={colors.secondary} strokeWidth={2.5} strokeLinecap="round" />
              <Path d={`M ${w * 0.8} ${5 + (waveOffset * 45) % 95} L ${w * 0.8} ${18 + (waveOffset * 45) % 95}`} stroke={colors.secondary} strokeWidth={2.5} strokeLinecap="round" />
            </G>
          )}
        </Svg>

        <View style={[styles.buddyContainer, { top: buddyY, left: w * 0.5 - 28 }]}>
          <Text style={styles.buddyEmoji}>
            {vesselSilhouette.includes('Teacup') ? '🍵' : '🏄‍♂️'}
          </Text>
        </View>

        {renderSticker()}
      </View>

      {!vesselSilhouette.includes('Teacup') && !vesselSilhouette.includes('Mug') && (
        <View style={[styles.bottleNeck, { width: w * 0.45, left: w * 0.275, borderColor: strokeColor }]}>
          <View style={[styles.bottleCap, { backgroundColor: strokeColor }]} />
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignSelf: 'center',
    justifyContent: 'center',
    marginVertical: 12,
  },
  silhouettedClip: {
    flex: 1,
    borderWidth: 5,
    overflow: 'hidden',
    position: 'relative',
  },
  bottleNeck: {
    position: 'absolute',
    top: -12,
    height: 18,
    borderWidth: 4,
    borderBottomWidth: 0,
    borderTopLeftRadius: 6,
    borderTopRightRadius: 6,
    backgroundColor: 'rgba(255,255,255,0.7)',
    zIndex: 10,
  },
  bottleCap: {
    position: 'absolute',
    top: -6,
    left: '10%',
    width: '80%',
    height: 10,
    borderRadius: 3,
  },
  buddyContainer: {
    position: 'absolute',
    width: 56,
    height: 56,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 5,
  },
  buddyEmoji: {
    fontSize: 34,
  },
  sticker: {
    position: 'absolute',
    width: 48,
    height: 48,
    borderColor: 'rgba(255,255,255,0.8)',
    borderWidth: 1.5,
    borderRadius: 24,
    backgroundColor: 'rgba(255,255,255,0.15)',
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#fff',
    shadowOpacity: 0.6,
    shadowRadius: 10,
    elevation: 3,
  }
});
