package garipolesya.com.example.wifianalyzer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WifiEntity::class], version = 1)
abstract class WifiDatabase : RoomDatabase() {
    abstract fun wifiDao(): WifiDao

    companion object {
        @Volatile
        private var INSTANCE: WifiDatabase? = null

        fun getInstance(context: Context): WifiDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WifiDatabase::class.java,
                        "wifi_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}