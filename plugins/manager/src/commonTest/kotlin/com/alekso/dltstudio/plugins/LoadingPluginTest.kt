package com.alekso.dltstudio.plugins

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesProvider
import org.junit.Test

class PluginTest {

    @Test
    fun `Test Loading Jar test plugin`() {
        val messagesProvider = object : MessagesProvider {
            override fun getMessages(): SnapshotStateList<LogMessage> {
                return mutableStateListOf()
            }
        }
        val pluginManager = PluginManager(Formatter.STUB, messagesProvider, { _ -> })
        pluginManager.loadJarPlugins()
        println("Loaded plugins: ${pluginManager.plugins}")
        assert(pluginManager.plugins.size > 0)
    }

}