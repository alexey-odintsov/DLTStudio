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

class PluginManager(
    val pluginsPath: String,
    private val formatter: Formatter,
    private val messagesProvider: MessagesProvider,
    private val onProgressUpdate: (Float) -> Unit
) {
    private val predefinedPlugins = mutableListOf<DLTStudioPlugin>()
    val plugins = mutableListOf<DLTStudioPlugin>()

    fun registerPredefinedPlugin(plugin: DLTStudioPlugin) {
        if (!predefinedPlugins.contains(plugin)) {
            predefinedPlugins.add(plugin)
        }
    }

    suspend fun loadPlugins() {
        val mergedPlugins = mutableListOf<DLTStudioPlugin>()
        mergedPlugins.addAll(predefinedPlugins)
        mergedPlugins.addAll(loadJarPlugins())

        mergedPlugins.forEach { plugin ->
            plugin.init(
                logs = messagesProvider.getMessages(),
                onProgressUpdate = onProgressUpdate,
            )
            if (plugin is FormatterConsumer) {
                plugin.initFormatter(formatter)
            }
            plugins.add(plugin)
        }
    }

    internal fun loadJarPlugins(): List<DLTStudioPlugin> {
        Log.d("Loading Jar plugins")
        val jarPlugins = mutableListOf<DLTStudioPlugin>()
        val pluginsDir = File(pluginsPath)
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
                        var plugin: DLTStudioPlugin? = null
                        try { // todo: Skip non DLTStudioPlugin classes
                            plugin = pluginClass.getConstructor().newInstance() as DLTStudioPlugin
                        } catch (ignored: Exception) {
                        }
                        if (plugin != null) {
                            val identifyResult = pluginClass.getMethod("pluginName").invoke(plugin)
                            Log.i("Identify: $identifyResult")
                            Log.i("Class: $plugin")
                            Log.i("Methods: ${plugin.javaClass.declaredMethods.map { it.name }}")
                            jarPlugins.add(plugin)
                        }
                    } catch (ex: Exception) {
                        Log.e(ex.toString())
                    }
                }
            }
        return jarPlugins
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