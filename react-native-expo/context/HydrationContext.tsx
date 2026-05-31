import React, { createContext, useState, useEffect, useContext } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { LiquidThemeName } from '../theme/designTokens';

export interface WaterLog {
  id: string;
  amount: number;
  time: string;
  vessel: string;
}

interface HydrationContextType {
  // Configs
  hydrationGoal: number;
  setHydrationGoal: (goal: number) => void;
  logs: WaterLog[];
  addWater: (amount: number) => void;
  deleteLog: (id: string) => void;
  totalIntake: number;

  // Physiological Targets Adjusters
  isHotWeather: boolean;
  setIsHotWeather: (v: boolean) => void;
  isCoffeeTax: boolean;
  setIsCoffeeTax: (v: boolean) => void;
  isAltBooster: boolean;
  setIsAltBooster: (v: boolean) => void;
  isSodiumTax: boolean;
  setIsSodiumTax: (v: boolean) => void;
  isPregnancyMode: boolean;
  setIsPregnancyMode: (v: boolean) => void;
  isIllnessRecovery: boolean;
  setIsIllnessRecovery: (v: boolean) => void;
  getEffectiveGoal: () => number;

  // Customizer styling variables
  vesselSilhouette: string;
  setVesselSilhouette: (v: string) => void;
  activeBottleTheme: LiquidThemeName;
  setActiveBottleTheme: (t: LiquidThemeName) => void;
  activeSticker: string;
  setActiveSticker: (s: string) => void;
  frostingLevel: number;
  setFrostingLevel: (f: number) => void;

  // Animations sandbox parameters
  isLavaLamp: boolean;
  setIsLavaLamp: (b: boolean) => void;
  isRaindrops: boolean;
  setIsRaindrops: (b: boolean) => void;
  isCoralForest: boolean;
  setIsCoralForest: (b: boolean) => void;

  // Audio frequency parameters
  binauralFrequency: number;
  setBinauralFrequency: (f: number) => void;
  gulpTempo: number;
  setGulpTempo: (t: number) => void;

  // RPG stats
  rpgLevel: number;
  rpgXp: number;
  strength: number;
  intellect: number;
  agility: number;
  mysteryBubbleText: string;
  setMysteryBubbleText: (t: string) => void;
  wardrobeOutfit: string;
  setWardrobeOutfit: (o: string) => void;
  resetAllData: () => Promise<void>;
}

const HydrationContext = createContext<HydrationContextType | undefined>(undefined);

const STORAGE_KEY = '@water_logger_space_v2';

export const HydrationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Setup all hooks states
  const [hydrationGoal, setHydrationGoal] = useState(2500);
  const [logs, setLogs] = useState<WaterLog[]>([
    { id: '1', amount: 250, time: '09:15 AM', vessel: 'Classic Mug' },
    { id: '2', amount: 500, time: '12:30 PM', vessel: 'Glass Jar' },
  ]);

  const [isHotWeather, setIsHotWeather] = useState(false);
  const [isCoffeeTax, setIsCoffeeTax] = useState(false);
  const [isAltBooster, setIsAltBooster] = useState(false);
  const [isSodiumTax, setIsSodiumTax] = useState(false);
  const [isPregnancyMode, setIsPregnancyMode] = useState(false);
  const [isIllnessRecovery, setIsIllnessRecovery] = useState(false);

  const [vesselSilhouette, setVesselSilhouette] = useState('Glass Jar');
  const [activeBottleTheme, setActiveBottleTheme] = useState<LiquidThemeName>('Deep Blue');
  const [activeSticker, setActiveSticker] = useState('Otter 🦦');
  const [frostingLevel, setFrostingLevel] = useState(0.85);

  const [isLavaLamp, setIsLavaLamp] = useState(false);
  const [isRaindrops, setIsRaindrops] = useState(false);
  const [isCoralForest, setIsCoralForest] = useState(false);

  const [binauralFrequency, setBinauralFrequency] = useState(432);
  const [gulpTempo, setGulpTempo] = useState(1.0);

  const [rpgLevel, setRpgLevel] = useState(1);
  const [rpgXp, setRpgXp] = useState(25);
  const [strength, setStrength] = useState(10);
  const [intellect, setIntellect] = useState(10);
  const [agility, setAgility] = useState(10);
  const [mysteryBubbleText, setMysteryBubbleText] = useState('Ready for focus meditation? Press Spin or drink!');
  const [wardrobeOutfit, setWardrobeOutfit] = useState('None');

  const [isLoaded, setIsLoaded] = useState(false);

  // Load Saved parameters from storage cache space at startup
  useEffect(() => {
    const loadSavedState = async () => {
      try {
        const value = await AsyncStorage.getItem(STORAGE_KEY);
        if (value) {
          const parsed = JSON.parse(value);
          if (parsed.hydrationGoal !== undefined) setHydrationGoal(parsed.hydrationGoal);
          if (parsed.logs !== undefined) setLogs(parsed.logs);
          if (parsed.isHotWeather !== undefined) setIsHotWeather(parsed.isHotWeather);
          if (parsed.isCoffeeTax !== undefined) setIsCoffeeTax(parsed.isCoffeeTax);
          if (parsed.isAltBooster !== undefined) setIsAltBooster(parsed.isAltBooster);
          if (parsed.isSodiumTax !== undefined) setIsSodiumTax(parsed.isSodiumTax);
          if (parsed.isPregnancyMode !== undefined) setIsPregnancyMode(parsed.isPregnancyMode);
          if (parsed.isIllnessRecovery !== undefined) setIsIllnessRecovery(parsed.isIllnessRecovery);
          if (parsed.vesselSilhouette !== undefined) setVesselSilhouette(parsed.vesselSilhouette);
          if (parsed.activeBottleTheme !== undefined) setActiveBottleTheme(parsed.activeBottleTheme);
          if (parsed.activeSticker !== undefined) setActiveSticker(parsed.activeSticker);
          if (parsed.frostingLevel !== undefined) setFrostingLevel(parsed.frostingLevel);
          if (parsed.isLavaLamp !== undefined) setIsLavaLamp(parsed.isLavaLamp);
          if (parsed.isRaindrops !== undefined) setIsRaindrops(parsed.isRaindrops);
          if (parsed.isCoralForest !== undefined) setIsCoralForest(parsed.isCoralForest);
          if (parsed.binauralFrequency !== undefined) setBinauralFrequency(parsed.binauralFrequency);
          if (parsed.gulpTempo !== undefined) setGulpTempo(parsed.gulpTempo);
          if (parsed.rpgLevel !== undefined) setRpgLevel(parsed.rpgLevel);
          if (parsed.rpgXp !== undefined) setRpgXp(parsed.rpgXp);
          if (parsed.strength !== undefined) setStrength(parsed.strength);
          if (parsed.intellect !== undefined) setIntellect(parsed.intellect);
          if (parsed.agility !== undefined) setAgility(parsed.agility);
          if (parsed.mysteryBubbleText !== undefined) setMysteryBubbleText(parsed.mysteryBubbleText);
          if (parsed.wardrobeOutfit !== undefined) setWardrobeOutfit(parsed.wardrobeOutfit);
        }
      } catch (e) {
        console.error('Failed to load storage values:', e);
      } finally {
        setIsLoaded(true);
      }
    };
    loadSavedState();
  }, []);

  // Write changes back to AsyncStorage
  useEffect(() => {
    if (!isLoaded) return;
    const saveState = async () => {
      try {
        const stateToSave = {
          hydrationGoal,
          logs,
          isHotWeather,
          isCoffeeTax,
          isAltBooster,
          isSodiumTax,
          isPregnancyMode,
          isIllnessRecovery,
          vesselSilhouette,
          activeBottleTheme,
          activeSticker,
          frostingLevel,
          isLavaLamp,
          isRaindrops,
          isCoralForest,
          binauralFrequency,
          gulpTempo,
          rpgLevel,
          rpgXp,
          strength,
          intellect,
          agility,
          mysteryBubbleText,
          wardrobeOutfit,
        };
        await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(stateToSave));
      } catch (e) {
        console.error('Failed to save state caching:', e);
      }
    };
    saveState();
  }, [
    hydrationGoal,
    logs,
    isHotWeather,
    isCoffeeTax,
    isAltBooster,
    isSodiumTax,
    isPregnancyMode,
    isIllnessRecovery,
    vesselSilhouette,
    activeBottleTheme,
    activeSticker,
    frostingLevel,
    isLavaLamp,
    isRaindrops,
    isCoralForest,
    binauralFrequency,
    gulpTempo,
    rpgLevel,
    rpgXp,
    strength,
    intellect,
    agility,
    mysteryBubbleText,
    wardrobeOutfit,
    isLoaded,
  ]);

  const totalIntake = logs.reduce((acc, curr) => acc + curr.amount, 0);

  const getEffectiveGoal = () => {
    let goal = hydrationGoal;
    if (isHotWeather) goal += 500;
    if (isCoffeeTax) goal += 250;
    if (isAltBooster) goal += 300;
    if (isSodiumTax) goal += 300;
    if (isPregnancyMode) goal += 600;
    if (isIllnessRecovery) goal += 400;
    return goal;
  };

  const addWater = (amount: number) => {
    const timeNow = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const newLog: WaterLog = {
      id: Math.random().toString(36).substring(2, 9),
      amount,
      time: timeNow,
      vessel: vesselSilhouette,
    };
    setLogs((prev) => [newLog, ...prev]);

    // RPG XP addition rules
    const gainedXp = Math.floor(amount / 5);
    let nextXp = rpgXp + gainedXp;
    let nextLevel = rpgLevel;
    let nextStrength = strength;
    let nextIntellect = intellect;
    let nextAgility = agility;

    const threshold = nextLevel * 100;
    if (nextXp >= threshold) {
      nextXp -= threshold;
      nextLevel += 1;
      nextStrength += 2;
      nextIntellect += 3;
      nextAgility += 1;
      setMysteryBubbleText(`🎉 LEVEL UP! Status levels up to Level ${nextLevel}! Attributes upgraded!`);
    } else {
      setMysteryBubbleText(`Delicious gulp logged! Gained +${gainedXp} XP 🧠`);
    }

    setRpgXp(nextXp);
    setRpgLevel(nextLevel);
    setStrength(nextStrength);
    setIntellect(nextIntellect);
    setAgility(nextAgility);
  };

  const deleteLog = (id: string) => {
    setLogs((prev) => prev.filter((log) => log.id !== id));
    setMysteryBubbleText('Deleted drink log. Recalculated total.');
  };

  const resetAllData = async () => {
    try {
      await AsyncStorage.removeItem(STORAGE_KEY);
      setHydrationGoal(2500);
      setLogs([]);
      setIsHotWeather(false);
      setIsCoffeeTax(false);
      setIsAltBooster(false);
      setIsSodiumTax(false);
      setIsPregnancyMode(false);
      setIsIllnessRecovery(false);
      setVesselSilhouette('Glass Jar');
      setActiveBottleTheme('Deep Blue');
      setActiveSticker('Otter 🦦');
      setRpgLevel(1);
      setRpgXp(25);
      setStrength(10);
      setIntellect(10);
      setAgility(10);
      setMysteryBubbleText('System cache cleared! Restarted metrics.');
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <HydrationContext.Provider
      value={{
        hydrationGoal,
        setHydrationGoal,
        logs,
        addWater,
        deleteLog,
        totalIntake,
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
        getEffectiveGoal,
        vesselSilhouette,
        setVesselSilhouette,
        activeBottleTheme,
        setActiveBottleTheme,
        activeSticker,
        setActiveSticker,
        frostingLevel,
        setFrostingLevel,
        isLavaLamp,
        setIsLavaLamp,
        isRaindrops,
        setIsRaindrops,
        isCoralForest,
        setIsCoralForest,
        binauralFrequency,
        setBinauralFrequency,
        gulpTempo,
        setGulpTempo,
        rpgLevel,
        rpgXp,
        strength,
        intellect,
        agility,
        mysteryBubbleText,
        setMysteryBubbleText,
        wardrobeOutfit,
        setWardrobeOutfit,
        resetAllData,
      }}
    >
      {children}
    </HydrationContext.Provider>
  );
};

export const useHydration = () => {
  const context = useContext(HydrationContext);
  if (!context) {
    throw new Error('useHydration must be used inside a HydrationProvider');
  }
  return context;
};
