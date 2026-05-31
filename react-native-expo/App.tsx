import React, { useState } from 'react';
import { StyleSheet, SafeAreaView, StatusBar, View } from 'react-native';
import { HydrationProvider } from './context/HydrationContext';
import { useTheme } from './hooks/useTheme';
import { GlassyBottomNavigationBar } from './components/GlassyBottomNavigationBar';
import { HydrationLogScreen } from './components/HydrationLogScreen';
import { QuickContainersScreen } from './components/QuickContainersScreen';
import { StatsScreen } from './components/StatsScreen';
import { MeditationScreen } from './components/MeditationScreen';

type ScreenName = 'log' | 'quick' | 'stats' | 'meditation';

function MainLayout() {
  const { colors, isDarkTheme } = useTheme();
  const [currentScreen, setCurrentScreen] = useState<ScreenName>('log');

  const renderScreen = () => {
    switch (currentScreen) {
      case 'quick':
        return <QuickContainersScreen />;
      case 'stats':
        return <StatsScreen />;
      case 'meditation':
        return <MeditationScreen />;
      default:
        return <HydrationLogScreen />;
    }
  };

  return (
    <SafeAreaView style={[styles.container, { backgroundColor: colors.background }]}>
      <StatusBar barStyle={isDarkTheme ? 'light-content' : 'dark-content'} />
      
      {/* Prime Body Screen content height offset */}
      <View style={styles.content}>
        {renderScreen()}
      </View>

      {/* Dynamic bottom navigation bar custom component */}
      <GlassyBottomNavigationBar
        currentScreen={currentScreen}
        setCurrentScreen={setCurrentScreen}
      />
    </SafeAreaView>
  );
}

export default function App() {
  return (
    <HydrationProvider>
      <MainLayout />
    </HydrationProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  content: {
    flex: 1,
    paddingBottom: 78, // margin offset for custom floating bottom navigation tabs bar height
  },
});
