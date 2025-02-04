package com.alekso.dltstudio.plugins.deviceplugin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DeviceAnalyzeViewModel(onProgressUpdate: (Float) -> Unit) {
    private var _analyzeState = mutableStateListOf("No data")
    val analyzeState: SnapshotStateList<String> = _analyzeState

    private var cmdJob: Job? = null

    fun executeCommand(cmd: String) {
        _analyzeState.clear()
        _analyzeState.add(cmd)

        cmdJob = CoroutineScope(Dispatchers.IO).launch {
            val firstProcess = ProcessBuilder(cmd.split(" ")).start()
            val firstError = firstProcess.errorStream.readBytes().decodeToString()
            if (!firstError.isEmpty()) {
                _analyzeState.add(firstError)
            }
            val firstResult = firstProcess.inputStream.readBytes().decodeToString()
            _analyzeState.add(firstResult)

        }
    }
}