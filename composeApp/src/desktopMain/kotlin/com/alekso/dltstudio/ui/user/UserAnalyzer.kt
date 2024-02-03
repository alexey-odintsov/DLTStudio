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

enum class ParseType {
    SPLIT,
    INDEX_OF
}

object UserAnalyzer {
    fun analyzeUserStateChanges(
        index: Int,
        dltMessage: DLTMessage,
        parseType: ParseType = ParseType.INDEX_OF
    ): UserStateEntry {
        val payload = (dltMessage.payload as VerbosePayload).asText()

        return when (parseType) {
            ParseType.SPLIT -> parseUserStateSplit(index, dltMessage.getTimeStamp(), payload)
            ParseType.INDEX_OF -> parseUserStateIndexOf(index, dltMessage.getTimeStamp(), payload)
        }
    }

    fun parseUserStateSplit(index: Int, timestamp: Long, payload: String): UserStateEntry {
        val values = payload.split("ActivityManager[", "]:", "User", "state changed from", "to")
        val uid: Int = values[3].trim().toInt()
        val oldState: UserState = UserState.valueOf(values[4].trim())
        val newState: UserState = UserState.valueOf(values[5].trim())
        return UserStateEntry(index, timestamp, uid, oldState, newState)
    }

    fun parseUserStateIndexOf(index: Int, timestamp: Long, payload: String): UserStateEntry {
        val uid: Int = payload.substring(
            payload.indexOf(": User ") + ": User ".length..payload.indexOf(" state changed from")
        ).trim().toInt()
        val oldState: UserState = UserState.valueOf(
            payload.substring(
                payload.indexOf("changed from ") + "changed from ".length..payload.indexOf(" to ")
            ).trim()
        )
        val newState: UserState =
            UserState.valueOf(
                payload.substring(payload.indexOf(" to ") + " to ".length).trim()
            )
        return UserStateEntry(index, timestamp, uid, oldState, newState)
    }
}