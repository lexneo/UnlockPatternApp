package com.lexneoapps.unlockpatternapp.ui.checkpassword

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexneoapps.unlockpatternapp.data.PreferencesManager
import com.lexneoapps.unlockpatternapp.utils.PwUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "CheckPasswordViewModel"

@HiltViewModel
class CheckPasswordViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _events: MutableLiveData<CheckResEnum> = MutableLiveData()
    val events: LiveData<CheckResEnum>
        get() = _events

    //starting time of tap
    private var startTime = 0

    //ending time of tap
    private var endTime = 0

    //last ending time for pause calculation
    private var lastEndTime = 0

    //total time of tap
    private var totalTime = 0

    private lateinit var realPassword: String

    init {
        viewModelScope.launch {
            realPassword = preferencesManager.password.first()
        }
    }

    //livedata for current password
    private val _currentPassword = MutableLiveData<String>()
    val currentPassword: LiveData<String>
        get() = _currentPassword


    fun setStartTime() {
        startTime = SystemClock.elapsedRealtime().toInt()
        lastEndTime = endTime
    }

    fun setEndTime() {
        endTime = SystemClock.elapsedRealtime().toInt()
        saveTap()
    }

    private fun saveTap() {
        totalTime = endTime - startTime
        if (lastEndTime > 0) {
            _currentPassword.value = _currentPassword.value + (startTime - lastEndTime).toString() +
                    "," + totalTime.toString() + ","
        } else {
            _currentPassword.value = totalTime.toString() + ","
        }
    }

    fun checkClick() {
        if (_currentPassword.value.isNullOrEmpty()) {
            _events.value = CheckResEnum.EMPTY_PASSWORD
            restartCurrent()
        } else {
            checkIfPasswordMatch()
        }
    }

    private fun restartCurrent() {
        _currentPassword.value = ""
        startTime = 0
        endTime = 0
        totalTime = 0
    }

    fun restartPassword() {
        viewModelScope.launch {
            preferencesManager.updatePassword("")
            _events.value = CheckResEnum.PASSWORD_RESTARTED
        }
    }

    private fun checkIfPasswordMatch() {
        val currentPw = PwUtil.transformStringToIntList(_currentPassword.value.toString())
        val realPw = PwUtil.transformStringToIntList(realPassword)
        val pwsAreSame = PwUtil.checkIfSamePassword(currentPw, realPw)
        Log.d(TAG, "pwsAreSame $pwsAreSame")
        if (pwsAreSame) {
            _events.value = CheckResEnum.CORRECT
        } else {
            restartCurrent()
            _events.value = CheckResEnum.PASSWORD_DID_NOT_MATCH
        }
    }


    fun setToInitialState() {
        _events.value = null
        _currentPassword.value = ""
        startTime = 0
        endTime = 0
        lastEndTime = 0

    }

}

enum class CheckResEnum {
    EMPTY_PASSWORD,
    CORRECT,
    PASSWORD_DID_NOT_MATCH,
    PASSWORD_RESTARTED
}
