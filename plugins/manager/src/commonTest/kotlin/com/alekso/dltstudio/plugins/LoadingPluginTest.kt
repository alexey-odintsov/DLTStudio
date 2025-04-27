package com.alekso.dltstudio.plugins

import androidx.compose.runtime.mutableStateListOf
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
            override fun clearMessages() = Unit

            override fun storeMessages(logMessages: List<LogMessage>) = Unit

            override fun getMessages(): SnapshotStateList<LogMessage> {
                return messages
            }

            override fun getMessageByIndex(index: Int): LogMessage {
                return messages[0]
            }

            override fun updateLogComment(key: String, comment: String?) = Unit
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