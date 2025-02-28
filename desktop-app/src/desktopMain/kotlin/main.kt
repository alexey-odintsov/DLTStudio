package com.alekso.dltstudio

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.alekso.dltstudio.model.SettingsUI
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.dltstudio.settings.SettingsDialog
import com.alekso.dltstudio.ui.MainWindow
import com.alekso.dltstudio.uicomponents.dialogs.FileDialog
import com.alekso.logger.Log


val LocalFormatter = staticCompositionLocalOf { Formatter.STUB }
val LocalSettingsUI = staticCompositionLocalOf { SettingsUI.Default }

fun main() = application {
    Log.init(Env.getLogsPath())
    Log.r("===================")
    Log.d("Application started")

    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        Log.e("Uncaught exception occurred in $thread: $throwable\n${throwable.stackTraceToString()}")
    }
    Preferences.loadFromFile()
    Window(
        onCloseRequest = {
            Preferences.saveToFile()
            Log.d("Application closed")
            exitApplication()
        }, title = "DLT Studio", state = WindowState(width = 1280.dp, height = 768.dp)
    ) {
        AppTheme {
            val mainViewModel = remember { DependencyManager.provideMainViewModel() }
            val settingsUI = mainViewModel.settingsUI.collectAsState()
            val settingsLogs = mainViewModel.settingsLogs.collectAsState()
            CompositionLocalProvider(
                LocalFormatter provides DependencyManager.provideFormatter(),
                LocalSettingsUI provides settingsUI.value,
            ) {

                if (mainViewModel.settingsDialogState) {
                    SettingsDialog(
                        visible = mainViewModel.settingsDialogState,
                        onDialogClosed = { mainViewModel.closeSettingsDialog() },
                        settingsUI = LocalSettingsUI.current,
                        settingsLogs = settingsLogs.value,
                        callbacks = mainViewModel.settingsCallbacks,
                    )
                }

                AppMenu(mainViewModel.menuItems)
                FileDialog(mainViewModel.fileDialogState)
                MainWindow(mainViewModel)
            }
        }
    }
}