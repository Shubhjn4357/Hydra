import { Vibration, Platform } from 'react-native';

/**
 * Universal high-grade haptics and vibration engines for the Hydry app.
 * Utilizes standard React Native Vibration with custom delay arrays
 * to provide crisp tactile feedback across Android and iOS devices.
 */
export const haptics = {
  // Light select feedback
  light: () => {
    Vibration.vibrate(Platform.OS === 'ios' ? 10 : 35);
  },

  // Elegant feedback when logging standard receptacles
  medium: () => {
    Vibration.vibrate(Platform.OS === 'ios' ? 25 : 65);
  },

  // Double pulse (heartbeat) for heavy custom logs, target modifications or resets
  doublePulse: () => {
    Vibration.vibrate([0, 40, 80, 45]);
  },

  // Strong attention alert or successful compliance completion
  successAccent: () => {
    Vibration.vibrate([0, 40, 50, 40, 30, 80]);
  },

  // Soft breathing focus transition pulse (inhale / hold / exhale cue indicators)
  breathCue: () => {
    Vibration.vibrate(Platform.OS === 'ios' ? 15 : 45);
  },
};
