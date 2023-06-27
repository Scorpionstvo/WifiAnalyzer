package garipolesya.com.example.wifianalyzer.manager

import garipolesya.com.example.wifianalyzer.data.model.Wifi
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class WifiPasswordManager {

    private val passwordMap: HashMap<String, String> = hashMapOf()

    fun checkPassword(networkId: String, password: String): Boolean {
        return passwordMap[networkId] == password
    }

    fun generateAndSavePassword(wifi: Wifi) {
        val password = generatePassword(wifi)
        passwordMap[wifi.id] = password
    }

    private fun generatePassword(wifi: Wifi): String {
        val input = "${wifi.name}${wifi.id}".toByteArray(StandardCharsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(input).joinToString("") { "%02x".format(it) }
    }

}
