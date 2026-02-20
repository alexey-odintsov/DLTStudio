package alexey.odintsov.dltstudio.plugins.loginfoview

import alexey.odintsov.dltstudio.model.contract.LogMessage
import alexey.odintsov.dltstudio.plugins.contract.MessagesRepository
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