package app.editors.manager.mvp.models.states

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class PasscodeLockState(
    val passcode: String? = null,
    val fingerprintEnabled: Boolean = false,
    val failedUnlockCount: Int = 0,
    val attemptsLockIncrement: Int = 1,
    val attemptsUnlockTime: Long? = null
) {

    val enabled: Boolean
        get() = !passcode.isNullOrEmpty()

    val manyAttemptsLock: Boolean
        get() = attemptsUnlockTime != null && attemptsUnlockTime > System.currentTimeMillis()

    companion object {

        fun fromJson(json: String?): PasscodeLockState {
            return try {
                Json.decodeFromString(json ?: return PasscodeLockState())
            } catch (_: SerializationException) {
                PasscodeLockState()
            }
        }
    }
}

fun PasscodeLockState.toJson(): String {
    return Json.encodeToString(this)
}