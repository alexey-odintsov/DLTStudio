package com.alekso.dltstudio.plugins.loginfoview

import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.plugins.contract.MessagesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LogInfoViewViewModel(
    private val messagesRepository: MessagesRepository,
) {
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Main + viewModelJob)



    fun onCommentUpdated(logMessage: LogMessage, comment: String?) {
        viewModelScope.launch(IO) {
            messagesRepository.updateLogComment(logMessage.id, comment)
        }
    }
}