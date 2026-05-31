export const SPACING = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  xxl: 24,
  xxxl: 32,
};

export const BORDER_RADIUS = {
  sm: 8,
  md: 12,
  lg: 16,
  xl: 24,
  round: 9999,
};

export type LiquidThemeName = 'Deep Blue' | 'Emerald Mint' | 'Cosmic Pink' | 'Golden Sun' | 'Lava Orange';

export interface ColorPalette {
  primary: string;
  secondary: string;
  accent: string;
  darkAccent: string;
}

export const LIQUID_THEMES: Record<LiquidThemeName, ColorPalette> = {
  'Deep Blue': {
    primary: '#3B82F6',
    secondary: '#60A5FA',
    accent: '#93C5FD',
    darkAccent: '#1E3A8A',
  },
  'Emerald Mint': {
    primary: '#10B981',
    secondary: '#34D399',
    accent: '#A7F3D0',
    darkAccent: '#064E3B',
  },
  'Cosmic Pink': {
    primary: '#EC4899',
    secondary: '#F472B6',
    accent: '#FBCFE8',
    darkAccent: '#500724',
  },
  'Golden Sun': {
    primary: '#F59E0B',
    secondary: '#FBBF24',
    accent: '#FDE68A',
    darkAccent: '#78350F',
  },
  'Lava Orange': {
    primary: '#EF4444',
    secondary: '#F87171',
    accent: '#FCA5A5',
    darkAccent: '#7F1D1D',
  },
};

export const getThemeColors = (isDarkTheme: boolean) => {
  return {
    background: isDarkTheme ? '#0F172A' : '#F8FAFC',
    cardBg: isDarkTheme ? 'rgba(30, 41, 59, 0.75)' : 'rgba(255, 255, 255, 0.85)',
    cardBgSolid: isDarkTheme ? '#1E293B' : '#FFFFFF',
    textPrimary: isDarkTheme ? '#FFFFFF' : '#1E293B',
    textSecondary: isDarkTheme ? '#94A3B8' : '#64748B',
    border: isDarkTheme ? 'rgba(255, 255, 255, 0.08)' : 'rgba(0, 0, 0, 0.06)',
    activeTabIndicator: isDarkTheme ? 'rgba(59, 130, 246, 0.15)' : 'rgba(59, 130, 246, 0.08)',
  };
};
