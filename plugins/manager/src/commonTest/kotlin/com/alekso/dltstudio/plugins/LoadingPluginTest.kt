package com.alekso.dltstudio.plugins

import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import com.alekso.dltstudio.plugins.manager.PluginManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Test
import java.io.File

class PluginTest {

    @Test
    fun `Test Loading Jar test plugin`() {
        val messagesProvider = object : MessagesRepository {
            private val messages = MutableStateFlow<List<LogMessage>>(emptyList())
            override suspend fun clearMessages() = Unit
            override suspend fun storeMessages(messages: List<LogMessage>) = Unit
            override fun getMessages(): MutableStateFlow<List<LogMessage>> = messages
            override fun getMarkedIds(): StateFlow<List<Int>> = MutableStateFlow(emptyList())
            override fun getFocusedMarkedIdIndex(): StateFlow<Int?> = MutableStateFlow(null)
            override fun getSearchResults(): MutableStateFlow<List<LogMessage>> = MutableStateFlow(emptyList())
            override fun getSelectedMessage(): StateFlow<LogMessage?> = MutableStateFlow(null)
            override fun updateLogComment(id: Int, comment: String?) = Unit
            override fun getComments(): StateFlow<Map<Int, String>> = MutableStateFlow(emptyMap())

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

            override fun selectMessage(id: Int)  = Unit
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