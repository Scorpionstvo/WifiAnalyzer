package garipolesya.com.example.wifianalyzer.data.repository

import garipolesya.com.example.wifianalyzer.data.model.Wifi
import kotlinx.coroutines.flow.Flow


interface WifiRepository {

    fun observeWifiNetworks(): Flow<List<Wifi>>

    fun observeWifiEnabled(): Flow<Boolean>

    fun exportData(): Flow<Boolean>

}
