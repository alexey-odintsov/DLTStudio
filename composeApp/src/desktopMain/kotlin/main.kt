package com.alekso.dltstudio

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.alekso.dltstudio.com.alekso.dltstudio.MainMenu
import com.alekso.dltstudio.com.alekso.dltstudio.MainMenuCallbacks
import com.alekso.dltstudio.plugins.DependencyManager
import com.alekso.dltstudio.preferences.Preferences
import com.alekso.dltstudio.ui.MainWindow
import com.alekso.logger.Log
import kotlinx.datetime.TimeZone
import java.io.File


val CurrentTimeZone = compositionLocalOf { TimeFormatter.timeZone }

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
        val currentTimeZone: TimeZone = TimeZone.currentSystemDefault()

        AppTheme {
            CompositionLocalProvider(CurrentTimeZone provides currentTimeZone) {
                val mainViewModel = remember { DependencyManager.getMainViewModel() }

                val mainMenuCallbacks = remember { object : MainMenuCallbacks {
                    override fun onOpenDLTFiles(files: List<File>) {
                        mainViewModel.parseFile(files)
                    }

                    override fun onLoadColorFiltersFile(file: File) {
                        mainViewModel.loadColorFilters(file)
                    }

                    override fun onSaveColorFiltersFile(file: File) {
                        mainViewModel.saveColorFilters(file)
                    }

                    override fun onLoadTimelineFiltersFile(file: File) {
                        mainViewModel.loadTimeLineFilters(file)
                    }

                    override fun onSaveTimelineFiltersFile(file: File) {
                        mainViewModel.saveTimeLineFilters(file)
                    }

                    override fun onClearColorFilters() {
                        mainViewModel.clearColorFilters()
                    }

                    override fun onClearTimelineFilters() {
                        mainViewModel.clearColorFilters()
                    }

                } }
                MainMenu(mainMenuCallbacks)
                MainWindow(mainViewModel)
            }
        }
    }
}