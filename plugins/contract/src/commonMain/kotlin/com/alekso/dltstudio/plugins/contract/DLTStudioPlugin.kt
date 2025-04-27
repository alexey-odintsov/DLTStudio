package com.alekso.dltstudio.plugins.contract

/**
 * Base plugin interface. All plugins should implement it.
 */
interface DLTStudioPlugin {
    /**
     * Name of the plugin
     */
    fun pluginName(): String

    /**
     * Name of the plugin directory
     */
    fun pluginDirectoryName(): String

    /**
     * Version of the plugin. Could be in any format.
     */
    fun pluginVersion(): String

    /**
     * Plugin class full name that will be used to identify plugin in app settings like enabled/disabled, tabs order etc.
     */
    fun pluginClassName(): String

    /**
     * Method that initializes plugin with necessary dependencies
     * @param messagesRepository – repository to work with logs
     * @param onProgressUpdate – callback to update progress indicator
     * @param pluginDirectory - name of directory where plugin can store files, like database or cache files.
     */
    fun init(messagesRepository: MessagesRepository, onProgressUpdate: (Float) -> Unit, pluginDirectory: String)

    /**
     * Callback which is triggered when new logs were loaded. Plugin should clean all current state that depends on logs.
     */
    fun onLogsChanged()
}