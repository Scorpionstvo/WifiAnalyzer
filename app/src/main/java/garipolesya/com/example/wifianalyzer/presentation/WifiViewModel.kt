package garipolesya.com.example.wifianalyzer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import garipolesya.com.example.wifianalyzer.data.repository.SettingsWifiChecker
import garipolesya.com.example.wifianalyzer.data.repository.WifiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class WifiViewModel(
    private val repository: WifiRepository,
    private val settingsWifiChecker: SettingsWifiChecker
) : ViewModel() {

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> = _state

    private val _isExportSuccess = MutableLiveData<Boolean>()
    val isExportSuccess: LiveData<Boolean> = _isExportSuccess

    init {
        tryScan()
    }

    private fun observeWifiNetworks() {
        _state.postValue(UiState.Loading)
        viewModelScope.launch {
            repository.observeWifiNetworks()
                .flowOn(Dispatchers.Main)
                .catch { _state.postValue(UiState.Error(it)) }
                .collect {
                    _state.postValue(UiState.WifiList(it))
                }
        }
    }

    private fun observeWifiEnabled() {
        viewModelScope.launch {
            repository.observeWifiEnabled()
                .flowOn(Dispatchers.Main)
                .collect {
                    if (!it) _state.postValue(UiState.DisabledWifi)
                }
        }
    }

    fun tryScan() {
        if (!settingsWifiChecker.hasWifiPermission()) {
            _state.postValue(UiState.NeedPermissions)
        } else if (!settingsWifiChecker.hasLocationEnabled()) {
            _state.postValue(UiState.NeedLocationEnable)
        } else {
            observeWifiEnabled()
            observeWifiNetworks()
        }
    }

    fun exportData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.exportData()
                .flowOn(Dispatchers.Main)
                .collect {
                    _isExportSuccess.postValue(it)
                }
        }
    }

}
