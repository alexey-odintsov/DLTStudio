package com.alekso.dltstudio

object Env {
    private const val APP_FOLDER = "dltstudio"
    private const val LOG_FILE = "dltstudio.log"
    private const val PREFERENCES_FILE = "preferences.txt"
    private const val DB_FOLDER = "db"
    private const val DB_FILE = "dlt_studio.db"
    private const val PLUGINS_FOLDER = "plugins"

    private fun getAppPath() = "${getUserHome()}/$APP_FOLDER"
    private fun getUserHome() = System.getProperty("user.home")

    fun getDbPath() = "${getAppPath()}/$DB_FOLDER/$DB_FILE"
    fun getPreferencesPath() = "${getAppPath()}/$PREFERENCES_FILE"
    fun getLogsPath() = "${getAppPath()}/$LOG_FILE"
    fun getPluginsPath() = "${getAppPath()}/$PLUGINS_FOLDER"
}