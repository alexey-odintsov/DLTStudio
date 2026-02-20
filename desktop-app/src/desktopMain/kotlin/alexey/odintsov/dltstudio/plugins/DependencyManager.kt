package alexey.odintsov.dltstudio.plugins

import alexey.odintsov.dltstudio.AppFormatter
import alexey.odintsov.dltstudio.Env
import alexey.odintsov.dltstudio.MainViewModel
import alexey.odintsov.dltstudio.db.DBFactory
import alexey.odintsov.dltstudio.db.preferences.PreferencesRepositoryImpl
import alexey.odintsov.dltstudio.db.settings.SettingsRepositoryImpl
import alexey.odintsov.dltstudio.logs.MessagesRepositoryImpl
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import alexey.odintsov.dltparser.DLTParserV2
import alexey.odintsov.dltstudio.model.contract.Formatter
import alexey.odintsov.dltstudio.plugins.contract.MessagesRepository
import alexey.odintsov.dltstudio.plugins.manager.PluginManager
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