package garipolesya.com.example.wifianalyzer.data.repository

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.core.app.ActivityCompat
import garipolesya.com.example.wifianalyzer.App
import garipolesya.com.example.wifianalyzer.data.model.Level
import garipolesya.com.example.wifianalyzer.data.model.Wifi
import garipolesya.com.example.wifianalyzer.data.db.WifiEntity
import garipolesya.com.example.wifianalyzer.data.db.WifiDao
import garipolesya.com.example.wifianalyzer.manager.WifiPasswordManager
import garipolesya.com.example.wifianalyzer.util.ssid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.util.*

class WifiRepositoryImpl(
    private val wifiManager: WifiManager,
    private val wifiDao: WifiDao, private val wifiPasswordManager: WifiPasswordManager
) : WifiRepository {

    override fun observeWifiNetworks(): Flow<List<Wifi>> {
        return callbackFlow {
            val scanResultsCallback =
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                            if (ActivityCompat.checkSelfPermission(
                                    App.applicationContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                val result = wifiManager.scanResults
                                val networks =
                                    result.map { T ->
                                        Wifi(
                                            T.BSSID,
                                            T.ssid(),
                                            isOpen(T.capabilities),
                                            getLevel(T.level),
                                            isWifiConnectedToAP(T.ssid(), T.BSSID)
                                        )
                                    }
                                trySend(networks)
                            }
                        }
                    }
                }
            App.applicationContext().registerReceiver(
                scanResultsCallback as BroadcastReceiver,
                IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            )
            wifiManager.startScan()
            awaitClose {
                App.applicationContext()
                    .unregisterReceiver(scanResultsCallback as BroadcastReceiver)
            }
        }
            .onEach {
               it.forEach { wifi -> wifiPasswordManager.generateAndSavePassword(wifi)  }
                val wifiEntityList = it.map { T ->
                    WifiEntity(T.id, T.name, T.isOpen)
                }
                withContext(Dispatchers.IO) { wifiDao.saveAll(wifiEntityList) }
            }
    }

    override fun observeWifiEnabled(): Flow<Boolean> {
        val wifiEnabledFlow = MutableStateFlow(wifiManager.isWifiEnabled)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                    wifiEnabledFlow.value = wifiManager.isWifiEnabled
                }
            }
        }
        App.applicationContext().registerReceiver(
            broadcastReceiver,
            IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        )
        return wifiEnabledFlow
    }

    override fun exportData(): Flow<Boolean> = flow {
        val wifiEntities = wifiDao.getAll()
        if (wifiEntities.isEmpty()) {
            emit(false)
        } else {
            val lines = wifiEntities.map { "${it.id},${it.name},${it.isOpen}" }
            val file = File(App.applicationContext().getExternalFilesDir(null), "wifi_table")
            file.writeLines(lines)
            emit(true)
        }
    }

    private fun isOpen(capabilities: String): Boolean {
        if (capabilities.isEmpty()) return true
        val cap = capabilities.uppercase(Locale.getDefault())
        return !cap.contains("WEP") &&
                !cap.contains("WPA") &&
                !cap.contains("WPA2")
    }

    private fun getLevel(level: Int): Level {
        return if (level >= -50) Level.STRONG
        else if (level in -49 downTo -70) Level.AVERAGE
        else Level.WEAK
    }

   private fun isWifiConnectedToAP(ssid: String, bssid: String): Boolean {
        @Suppress("DEPRECATION") val connectionInfo = wifiManager.connectionInfo
        return connectionInfo != null && connectionInfo.ssid == "\"$ssid\"" && connectionInfo.bssid == bssid
    }

    private fun File.writeLines(lines: List<String>) {
        val writer = FileWriter(this)
        for (line in lines) {
            writer.append(line).append('\n')
        }
        writer.close()
    }

}
