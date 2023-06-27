package garipolesya.com.example.wifianalyzer.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wifi_table")
data class WifiEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "wifi_name") val name: String,
    @ColumnInfo(name = "wifi_is_open") val isOpen: Boolean
)