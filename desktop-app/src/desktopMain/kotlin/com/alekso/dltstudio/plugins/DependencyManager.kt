package com.alekso.dltstudio.plugins

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.alekso.dltparser.DLTParserV2
import com.alekso.dltstudio.AppFormatter
import com.alekso.dltstudio.Env
import com.alekso.dltstudio.MainViewModel
import com.alekso.dltstudio.com.alekso.dltstudio.logs.MessagesRepositoryImpl
import com.alekso.dltstudio.db.DBFactory
import com.alekso.dltstudio.db.preferences.PreferencesRepositoryImpl
import com.alekso.dltstudio.db.settings.SettingsRepositoryImpl
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.manager.PluginManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object DependencyManager {
    private var _progress = mutableStateOf(0f)
    val progress: State<Float>
        get() = _progress

    val onProgressUpdate = { p: Float ->
        _progress.value = p
    }

    private val database by lazy {
        DBFactory().createDatabase()
    }

    private val formatter by lazy {
        AppFormatter()
    }

    private val pluginsManager by lazy {
        PluginManager(
            pluginsPath = Env.getPluginsPath(),
            formatter = formatter,
            messagesRepository = messagesRepository,
            onProgressUpdate = onProgressUpdate,
        )
    }

    private val messagesRepository by lazy {
        MessagesRepositoryImpl()
    }

    private val settingsRepository by lazy {
        SettingsRepositoryImpl(
            database = database,
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        )
    }

    private val preferencesRepository by lazy {
        PreferencesRepositoryImpl(
            database = database,
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        )
    }

    private val mainViewModel = MainViewModel(
        dltParser = DLTParserV2(),
        messagesRepository = messagesRepository,
        pluginManager = providePluginsManager(),
        settingsRepository = settingsRepository,
        preferencesRepository = preferencesRepository,
        formatter = formatter,
        onProgressChanged = onProgressUpdate,
    )

    fun provideMessageRepository(): MessagesRepository {
        return messagesRepository
    }

    fun provideMainViewModel(): MainViewModel {
        return mainViewModel
    }

    fun providePluginsManager(): PluginManager {
        return pluginsManager
    }

    fun provideFormatter(): Formatter {
        return formatter
    }
}