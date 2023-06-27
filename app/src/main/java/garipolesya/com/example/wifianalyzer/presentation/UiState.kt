package garipolesya.com.example.wifianalyzer.presentation

import garipolesya.com.example.wifianalyzer.data.model.Wifi

sealed interface UiState {

    data class WifiList(
        val list: List<Wifi>
    ) : UiState

    object DisabledWifi : UiState

    object Loading : UiState

    data class Error(val t: Throwable) : UiState

    object NeedPermissions : UiState

    object NeedLocationEnable: UiState

}
