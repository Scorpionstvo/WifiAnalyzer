package garipolesya.com.example.wifianalyzer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WifiDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveAll(wifi: List<WifiEntity>)

    @Query("SELECT * FROM wifi_table")
    suspend fun getAll(): List<WifiEntity>

    @Query("SELECT * FROM wifi_table WHERE id = :id")
    suspend fun getItem(id: String): WifiEntity

}
