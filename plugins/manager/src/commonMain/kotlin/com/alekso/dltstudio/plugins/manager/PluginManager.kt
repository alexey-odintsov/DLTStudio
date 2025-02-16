package com.alekso.dltstudio.plugins.manager

import com.alekso.dltstudio.model.contract.Formatter
import com.alekso.dltstudio.plugins.contract.DLTStudioPlugin
import com.alekso.dltstudio.plugins.contract.FormatterConsumer
import com.alekso.dltstudio.plugins.contract.MessagesProvider
import com.alekso.dltstudio.plugins.contract.PluginPanel
import com.alekso.logger.Log
import java.io.File
import java.net.URI
import java.net.URL
import java.net.URLClassLoader
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile

private const val DEFAULT_PLUGINS_FOLDER = "/plugins/"

class PluginManager(
    private val formatter: Formatter,
    private val messagesProvider: MessagesProvider,
    private val onProgressUpdate: (Float) -> Unit
) {
    private val predefinedPlugins = mutableListOf<DLTStudioPlugin>()
    internal val plugins = mutableListOf<DLTStudioPlugin>()


    fun registerPredefinedPlugin(plugin: DLTStudioPlugin) {
        if (!predefinedPlugins.contains(plugin)) {
            predefinedPlugins.add(plugin)
        }
    }

    suspend fun loadPlugins() {
        predefinedPlugins.forEach { plugin ->
            plugin.init(
                logs = messagesProvider.getMessages(),
                onProgressUpdate = onProgressUpdate,
            )
            if (plugin is FormatterConsumer) {
                plugin.initFormatter(formatter)
            }
            plugins.add(plugin)
        }
        loadJarPlugins("plugins")
    }

    internal fun loadJarPlugins(path: String? = null) {
        Log.d("Loading Jar plugins")
        val pluginsDir: File = if (path != null) {
            File(path)
        } else {
            val curPath = File("").absolutePath
            File("$curPath$DEFAULT_PLUGINS_FOLDER")
        }

        val files = pluginsDir.listFiles()?.filter { it.name.endsWith(".jar") }

        Log.d("Found ${files?.size} jars")
        files?.forEach { f ->
            Log.d(f.name)
        }

        val jars = arrayListOf<JarFile>()
        val uris = arrayListOf<URL>()

        files?.forEach { file ->
            try {
                jars.add(JarFile(file))
            } catch (e: Exception) {
                Log.e("Failed to load Jar: $e")
            }

            try {
                uris.add(URI("file:$file").toURL())
            } catch (e: Exception) {
                Log.e("Failed to load URI: $e")
            }
        }

        val urlLoader = URLClassLoader(uris.toTypedArray(), this.javaClass.classLoader)

        jars.stream().map<Enumeration<JarEntry>> { jar: JarFile -> jar.entries() }
            .forEach { e: Enumeration<JarEntry> ->
                while (e.hasMoreElements()) {
                    try {
                        val je: JarEntry = e.nextElement() as JarEntry
                        if (je.isDirectory || !je.name.endsWith(".class")) {
                            continue
                        }
                        var className: String = je.name.replace(".class", "")
                        className = className.replace('/', '.')
                        Log.i(className)


                        val pluginClass = urlLoader.loadClass(className)
                        val plugin: DLTStudioPlugin =
                            pluginClass.getConstructor().newInstance() as DLTStudioPlugin
                        val identifyResult = pluginClass.getMethod("pluginName").invoke(plugin)
                        Log.i("Identify: $identifyResult")
                        Log.i("Class: $plugin")
                        Log.i("Methods: ${plugin.javaClass.declaredMethods.map { it.name }}")
                        plugins.add(plugin)

                    } catch (ex: ClassNotFoundException) {
                        Log.e(ex.toString())
                    }
                }
            }
    }

    fun getPluginPanels(): List<PluginPanel> {
        return plugins.filter { it is PluginPanel }.map { it as PluginPanel }
    }

    fun notifyLogsChanged() {
        plugins.forEach { plugin ->
            plugin.onLogsChanged()
        }
    }

}