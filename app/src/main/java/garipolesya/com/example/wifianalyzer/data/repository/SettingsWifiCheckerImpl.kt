package garipolesya.com.example.wifianalyzer.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity

class SettingsWifiCheckerImpl(private val context: Context) : SettingsWifiChecker {

    override fun hasWifiPermission(): Boolean {
        return context.checkSelfPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun hasLocationEnabled(): Boolean {
        return (context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as? LocationManager)?.getProviders(
            true
        )
            ?.isNotEmpty()
            ?: false
    }

}
