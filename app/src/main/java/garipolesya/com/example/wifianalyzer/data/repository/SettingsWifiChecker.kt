package garipolesya.com.example.wifianalyzer.data.repository

interface SettingsWifiChecker {

    fun hasWifiPermission(): Boolean

    fun hasLocationEnabled(): Boolean

}
