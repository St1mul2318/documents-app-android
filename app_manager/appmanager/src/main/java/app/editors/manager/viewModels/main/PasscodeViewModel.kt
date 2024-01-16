package app.editors.manager.viewModels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.editors.manager.managers.tools.PreferenceTool
import app.editors.manager.managers.utils.KeyStoreUtils
import app.editors.manager.mvp.models.states.PasscodeLockState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PasscodeViewModelFactory(private val preferenceTool: PreferenceTool) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PasscodeViewModel::class.java)) {
            PasscodeViewModel(preferenceTool) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

class PasscodeViewModel(private val preferenceTool: PreferenceTool) : ViewModel() {

    companion object {

        private const val PASSCODE_MAX_FAILED_ATTEMPTS = 3
        private const val PASSCODE_MAX_DISABLING_INCREMENT = 10
        private const val PASSCODE_FAILED_LOCKING_STEP = 30_000
    }

    private val _passcodeState: MutableStateFlow<PasscodeLockState> = MutableStateFlow(preferenceTool.passcodeLock)
    val passcodeState: StateFlow<PasscodeLockState> = _passcodeState.asStateFlow()

    fun setPasscode(passcode: String) {
        updateState { copy(passcode = KeyStoreUtils.encryptData(passcode)) }
    }

    fun resetPasscode() {
        preferenceTool.passcodeLock = PasscodeLockState()
        _passcodeState.value = PasscodeLockState()
    }

    fun onFingerprintEnable(enabled: Boolean) {
        updateState { copy(fingerprintEnabled = enabled) }
    }

    fun onDisablingReset() {
        updateState { copy(attemptsUnlockTime = null, attemptsLockIncrement = 1, failedUnlockCount = 0) }
    }

    fun onFailedConfirm() {
        var attempts = preferenceTool.passcodeLock.failedUnlockCount
        if (++attempts >= PASSCODE_MAX_FAILED_ATTEMPTS) {
            val increment = preferenceTool.passcodeLock.attemptsLockIncrement
            val enablingTime = increment * PASSCODE_FAILED_LOCKING_STEP
            if (increment <= PASSCODE_MAX_DISABLING_INCREMENT) {
                updateState { copy(attemptsLockIncrement = increment + 1) }
            }
            updateState {
                copy(
                    attemptsUnlockTime = System.currentTimeMillis() + enablingTime,
                    failedUnlockCount = 0
                )
            }
        } else {
            updateState { copy(failedUnlockCount = attempts) }
        }
    }

    private fun updateState(block: PasscodeLockState.() -> PasscodeLockState) {
        preferenceTool.passcodeLock = preferenceTool.passcodeLock.block()
        _passcodeState.update(block)
    }
}