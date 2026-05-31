package com.example.ui

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.WaterViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WaterLoggerApp(
    viewModel: WaterViewModel,
    isDarkTheme: Boolean,
    onDarkThemeToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(AppTab.LOG) }

    // State bindings
    val selectedDateLabel by viewModel.selectedDateLabel.collectAsStateWithLifecycle()
    val logs by viewModel.logsForSelectedDay.collectAsStateWithLifecycle()
    val totalIntake by viewModel.totalIntakeForDay.collectAsStateWithLifecycle()
    val goal by viewModel.hydrationGoal.collectAsStateWithLifecycle()
    val remindersEnabled by viewModel.remindersEnabled.collectAsStateWithLifecycle()
    val intervalHours by viewModel.reminderIntervalHours.collectAsStateWithLifecycle()

    var showGoalDialog by remember { mutableStateOf(false) }
    var showCustomLogDialog by remember { mutableStateOf(false) }

    // Launcher for Notification Permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleReminders(true)
            Toast.makeText(context, "Water alerts scheduling activated! 💧", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.toggleReminders(false)
            Toast.makeText(context, "Notification permission is required for water alerts.", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Aesthetic subtle gradient background
                val color1 = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF1F5F9)
                val color2 = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                drawRect(
                    brush = Brush.verticalGradient(listOf(color1, color2))
                )
            },
        containerColor = Color.Transparent,
        bottomBar = {
            GlassyBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it },
                isDarkTheme = isDarkTheme
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Animated Screen content switching
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    val targetOrder = when (targetState) {
                        AppTab.LOG -> 0
                        AppTab.CONTAINERS -> 1
                        AppTab.STATS -> 2
                    }
                    val initialOrder = when (initialState) {
                        AppTab.LOG -> 0
                        AppTab.CONTAINERS -> 1
                        AppTab.STATS -> 2
                    }
                    if (targetOrder > initialOrder) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                modifier = Modifier.weight(1f),
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    AppTab.LOG -> {
                        HydrationLogScreen(
                            totalIntake = totalIntake,
                            goal = goal,
                            onLogWater = { viewModel.addWaterLog(it) },
                            onShowCustomDialog = { showCustomLogDialog = true },
                            onMenuClick = { showGoalDialog = true },
                            onDarkThemeToggle = onDarkThemeToggle,
                            isDarkTheme = isDarkTheme,
                            dateLabel = selectedDateLabel,
                            onPreviousDay = { viewModel.selectPreviousDay() },
                            onNextDay = { viewModel.selectNextDay() }
                        )
                    }
                    AppTab.CONTAINERS -> {
                        QuickContainersScreen(
                            onLogWater = { viewModel.addWaterLog(it) },
                            onShowCustomDialog = { showCustomLogDialog = true },
                            onBackClick = { currentTab = AppTab.LOG },
                            isDarkTheme = isDarkTheme,
                            onMenuClick = { showGoalDialog = true },
                            dateLabel = selectedDateLabel,
                            onPreviousDay = { viewModel.selectPreviousDay() },
                            onNextDay = { viewModel.selectNextDay() }
                        )
                    }
                    AppTab.STATS -> {
                        statsScreenView(
                            totalIntake = totalIntake,
                            goal = goal,
                            logs = logs,
                            onLogWater = { viewModel.addWaterLog(it) },
                            onShowCustomDialog = { showCustomLogDialog = true },
                            onDeleteLog = { viewModel.deleteWaterLog(it) },
                            showGoalDialog = { showGoalDialog = true },
                            onBackClick = { currentTab = AppTab.LOG },
                            remindersEnabled = remindersEnabled,
                            intervalHours = intervalHours,
                            onToggleReminders = { enable ->
                                if (enable) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        val status = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                        if (status == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                            viewModel.toggleReminders(true)
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    } else {
                                        viewModel.toggleReminders(true)
                                    }
                                } else {
                                    viewModel.toggleReminders(false)
                                }
                            },
                            onIntervalChange = { viewModel.updateReminderInterval(it) },
                            isDarkTheme = isDarkTheme,
                            onDarkThemeToggle = onDarkThemeToggle,
                            dateLabel = selectedDateLabel,
                            onPreviousDay = { viewModel.selectPreviousDay() },
                            onNextDay = { viewModel.selectNextDay() }
                        )
                    }
                }
            }
        }
    }

    // Goal Configuration Alert Dialog
    if (showGoalDialog) {
        GoalTargetSettingsDialog(
            currentGoal = goal,
            onDismiss = { showGoalDialog = false },
            onConfirm = { newGoal ->
                viewModel.changeGoal(newGoal)
                showGoalDialog = false
            }
        )
    }

    // Custom ML Logger Alert Dialog
    if (showCustomLogDialog) {
        CustomWaterLoggerDialog(
            onDismiss = { showCustomLogDialog = false },
            onConfirm = { customMs ->
                viewModel.addWaterLog(customMs)
                showCustomLogDialog = false
            }
        )
    }
}
