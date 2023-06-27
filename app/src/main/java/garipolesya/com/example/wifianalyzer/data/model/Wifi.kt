package garipolesya.com.example.wifianalyzer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class Wifi(
    val id: String,
    val name: String,
    val isOpen: Boolean,
    val level: Level,
    val isConnected: Boolean
) : Comparable<Wifi> {
    override fun compareTo(other: Wifi): Int {
        return compareValuesBy(other, this, { it.isConnected }, { it.level.rawValue })
    }
}

@Parcelize
enum class Level(val rawValue: Int) : Parcelable {
    WEAK(1), AVERAGE(2), STRONG(3);
}

