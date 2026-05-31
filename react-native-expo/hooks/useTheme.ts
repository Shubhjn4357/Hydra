import { useColorScheme } from 'react-native';
import { getThemeColors, LIQUID_THEMES, SPACING, BORDER_RADIUS } from '../theme/designTokens';
import { useHydration } from '../context/HydrationContext';

export function useTheme() {
  const systemScheme = useColorScheme();
  const { activeBottleTheme } = useHydration();

  // Determine dark mode
  const isDarkTheme = systemScheme === 'dark';

  // Base colors
  const baseColors = getThemeColors(isDarkTheme);

  // Active liquid palette
  const fluidColors = LIQUID_THEMES[activeBottleTheme] || LIQUID_THEMES['Deep Blue'];

  return {
    isDarkTheme,
    colors: {
      ...baseColors,
      primary: fluidColors.primary,
      secondary: fluidColors.secondary,
      accent: fluidColors.accent,
      darkAccent: fluidColors.darkAccent,
    },
    spacing: SPACING,
    borderRadius: BORDER_RADIUS,
  };
}
