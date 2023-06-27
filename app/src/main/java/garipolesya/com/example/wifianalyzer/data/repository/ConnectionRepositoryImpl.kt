package garipolesya.com.example.wifianalyzer.data.repository

import android.net.*
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ConnectionRepositoryImpl(private val wifiManager: WifiManager) :
    ConnectionRepository {

    @Suppress("DEPRECATION")
    override fun connectWifi(id: String, password: String): Flow<Boolean> {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"$id\""
        wifiConfig.preSharedKey = "\"$password\""
        val netId = wifiManager.addNetwork(wifiConfig)
        return if (netId != -1) {
            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
            flow { emit(true) }
        } else {
            flow { emit(false) }
        }
    }

}
