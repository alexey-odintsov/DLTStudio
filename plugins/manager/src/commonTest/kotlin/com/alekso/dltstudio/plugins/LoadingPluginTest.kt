package com.alekso.dltstudio.plugins

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.manager.PluginManager
import org.junit.Test
import java.io.File

class PluginTest {

    @Test
    fun `Test Loading Jar test plugin`() {
        val messagesProvider = object : MessagesRepository {
            private val messages = mutableStateListOf<LogMessage>()
            override suspend fun clearMessages() = Unit
            override suspend fun storeMessages(messages: List<LogMessage>) = Unit
            override fun getMessages(): SnapshotStateList<LogMessage> = messages
            override fun getMarkedIds(): SnapshotStateList<Int> = mutableStateListOf()
            override fun getFocusedMarkedIdIndex(): State<Int?> = mutableStateOf(null)
            override fun getSearchResults(): SnapshotStateList<LogMessage> = mutableStateListOf()
            override fun getSelectedMessage(): State<LogMessage?> = mutableStateOf(null)
            override fun updateLogComment(id: Int, comment: String?) = Unit
            override fun toggleMark(id: Int) = Unit
            override fun selectPrevMarkedLog() = Unit
            override fun selectNextMarkedLog() = Unit

            override suspend fun removeMessages(
                progress: (Float) -> Unit,
                predicate: (LogMessage) -> Boolean
            ): Long = 0L

            override suspend fun removeMessage(logMessage: LogMessage) = Unit

            override suspend fun searchMessages(
                progress: (Float) -> Unit,
                predicate: (LogMessage) -> Boolean
            ): Long = 0L

            override fun selectMessage(key: Int)  = Unit
            override fun clearMarks() = Unit
        }
        val pluginManager = PluginManager(
            "${File("").absolutePath}/plugins/",
            Formatter.STUB,
            messagesProvider,
            { _ -> })
        val plugins = pluginManager.loadJarPlugins()
        println("Loaded plugins: ${pluginManager.plugins}")
        assert(plugins.filter { it.pluginName().lowercase().contains("testplugin") }.isNotEmpty())
    }

}