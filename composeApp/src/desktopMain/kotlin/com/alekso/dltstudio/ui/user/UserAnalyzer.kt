package com.alekso.dltstudio.ui.user

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.VerbosePayload


/**
 * https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/am/UserState.java;l=112?q=User%20%22state%20changed%22
 */
enum class UserState {
    BOOTING,
    RUNNING_LOCKED,
    RUNNING_UNLOCKING,
    RUNNING_UNLOCKED,
    STOPPING,
    SHUTDOWN
}

data class UserStateEntry(
    val index: Int,
    val timestamp: Long,
    val uid: Int,
    val oldState: UserState,
    val newState: UserState
)

object UserAnalyzer {
    fun analyzeUserStateChanges(index: Int, dltMessage: DLTMessage): UserStateEntry {
        val payload = (dltMessage.payload as VerbosePayload).asText()

        val values = payload.split("ActivityManager[", "]:", "User", "state changed from", "to")
        val uid = values[3].trim().toInt()
        val oldState = UserState.valueOf(values[4].trim())
        val newState = UserState.valueOf(values[5].trim())

        return UserStateEntry(index, dltMessage.getTimeStamp(), uid, oldState, newState)
    }

}