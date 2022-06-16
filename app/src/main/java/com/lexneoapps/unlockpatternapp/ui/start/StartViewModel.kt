package com.lexneoapps.unlockpatternapp.ui.start

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

const val TAG = "StartViewModel"

@HiltViewModel
class StartViewModel @Inject constructor(private val preferencesManager: PreferencesManager) :
    ViewModel() {

    private val _events: MutableLiveData<ResEnum> = MutableLiveData()
    val events: LiveData<ResEnum>
        get() = _events

    init {
        viewModelScope.launch {
            if(passwordExists()){
                _events.value = ResEnum.PASSWORD_EXISTS
            }
        }
    }


    //starting time of tap
    private var startTime = 0

    //ending time of tap
    private var endTime = 0

    //last ending time for pause calculation
    private var lastEndTime = 0

    //total time of tap
    private var totalTime = 0


    //controls the state of save button and saving function
    private val _confirmed = MutableLiveData(false)
    val confirmed: LiveData<Boolean>
        get() = _confirmed

    //controls the state of show button and functionality of visible taps
    private val _showPassword = MutableLiveData(false)
    val showPassword: LiveData<Boolean>
        get() = _showPassword

    //livedata for first password
    private val _firstPassword = MutableLiveData<String>()
    val firstPassword: LiveData<String>
        get() = _firstPassword

    //livedata for second (confirmed) password
    private val _secondPassword = MutableLiveData<String>()
    val secondPassword: LiveData<String>
        get() = _secondPassword

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
        if (_confirmed.value == false) {
            if (lastEndTime > 0) {
                _firstPassword.value = _firstPassword.value + (startTime - lastEndTime).toString() +
                        "," + totalTime.toString() + ","
            } else {
                _firstPassword.value = totalTime.toString() + ","
            }
        } else {
            if (lastEndTime > 0) {
                _secondPassword.value =
                    _secondPassword.value + (startTime - lastEndTime).toString() +
                            "," + totalTime.toString() + ","
            } else {
                _secondPassword.value = totalTime.toString() + ","
            }
        }
    }

    fun saveClick() {
        if (_firstPassword.value.isNullOrEmpty() || (_secondPassword.value.isNullOrEmpty() && _confirmed.value == true)) {
            _events.value = ResEnum.EMPTY_PASSWORD
            return
        }
        if (_confirmed.value == false && !_firstPassword.value.isNullOrEmpty()) {
            _confirmed.value = true
            startTime = 0
            endTime = 0
            lastEndTime = 0
        } else {
            savePassword()
        }
    }

    private fun savePassword() {
        val firstPassword = PwUtil.transformStringToIntList(_firstPassword.value.toString())
        val secondPassword = PwUtil.transformStringToIntList(_secondPassword.value.toString())
        val pwsAreSame = PwUtil.checkIfSamePassword(firstPassword, secondPassword)
        Log.d(TAG, "pwsAreSame $pwsAreSame")
        if (pwsAreSame) {
            viewModelScope.launch {
                preferencesManager.updatePassword(_firstPassword.value.toString())
                _events.value = ResEnum.SAVED
            }

        } else {
            _events.value = ResEnum.PASSWORD_DID_NOT_MATCH
        }
    }

    //resets password
    fun changePassword() {
        _confirmed.value = false
        startTime = 0
        endTime = 0
        lastEndTime = 0
        _firstPassword.value = ""
        _secondPassword.value = ""
        viewModelScope.launch {
            preferencesManager.updatePassword("")
            _events.value = ResEnum.PASSWORD_RESTARTED


        }
    }


    fun setShowPassword() {
        _showPassword.value = !_showPassword.value!!
    }

    private suspend fun passwordExists() : Boolean {
        return preferencesManager.password.first().isNotEmpty()
    }




    fun setToInitialState() {
        _events.value = null
        _confirmed.value = false
        startTime = 0
        endTime = 0
        lastEndTime = 0
        _firstPassword.value = ""
        _secondPassword.value = ""

    }

    enum class ResEnum {
        EMPTY_PASSWORD,
        SAVED,
        PASSWORD_DID_NOT_MATCH,
        PASSWORD_RESTARTED,
        PASSWORD_EXISTS

    }


}