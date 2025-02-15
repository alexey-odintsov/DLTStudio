package com.alekso.dltstudio

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.dltstudio.ui.MainWindow
import com.alekso.logger.Log


val LocalFormatter = staticCompositionLocalOf { Formatter.STUB }

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
            CompositionLocalProvider(
                LocalFormatter provides DependencyManager.provideFormatter(),
            ) {
                val mainViewModel = remember { DependencyManager.provideMainViewModel() }

                MainMenu(mainViewModel.mainMenuCallbacks)
                MainWindow(mainViewModel)
            }
        }
    }
}