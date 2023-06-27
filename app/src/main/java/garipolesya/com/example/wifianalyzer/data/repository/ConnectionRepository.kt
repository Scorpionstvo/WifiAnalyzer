package garipolesya.com.example.wifianalyzer.data.repository

import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {

    fun connectWifi(id: String, password: String): Flow<Boolean>

}
