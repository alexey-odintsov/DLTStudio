package com.alekso.dltstudio

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.alekso.dltstudio.model.SettingsPlugins
import com.alekso.dltstudio.model.SettingsUI
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.settings.SettingsDialog
import com.alekso.dltstudio.theme.ThemeManager
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
    Window(
        onCloseRequest = {
            Log.d("Application closed")
            exitApplication()
        }, state = WindowState(width = 1280.dp, height = 768.dp)
    ) {
        val mainViewModel = remember { DependencyManager.provideMainViewModel() }
        val filePath = mainViewModel.filesPath.collectAsState()
        val settingsUI = mainViewModel.settingsUI.collectAsState()
        val settingsLogs = mainViewModel.settingsLogs.collectAsState()
        val settingsPlugins = mainViewModel.settingsPlugins.collectAsState(SettingsPlugins.Initial)
        val fileDialogState = mainViewModel.fileDialogState.collectAsState()
        val recentColorFiltersFiles = mainViewModel.recentColorFiltersFiles.collectAsState()
        val settingsDialogState = mainViewModel.settingsDialogState.collectAsState()

        window.title = "DLT Studio ${filePath.value}"
        ThemeManager.AppTheme {
            CompositionLocalProvider(
                LocalFormatter provides DependencyManager.provideFormatter(),
                LocalSettingsUI provides settingsUI.value,
            ) {
                if (settingsDialogState.value) {
                    SettingsDialog(
                        visible = settingsDialogState.value,
                        onDialogClosed = { mainViewModel.closeSettingsDialog() },
                        settingsUI = LocalSettingsUI.current,
                        settingsLogs = settingsLogs.value,
                        callbacks = mainViewModel.settingsCallbacks,
                        pluginsCallbacks = mainViewModel.settingsPluginsCallbacks,
                        settingsPlugins = settingsPlugins.value,
                    )
                }

                if (fileDialogState.value.visible) {
                    FileDialog(fileDialogState.value)
                }
                MainMenu(
                    mainViewModel.mainMenuCallbacks,
                    recentColorFiltersFiles.value,
                )
                MainWindow(mainViewModel)
            }
        }
    }
}