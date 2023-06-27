package garipolesya.com.example.wifianalyzer.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import garipolesya.com.example.wifianalyzer.R
import garipolesya.com.example.wifianalyzer.data.model.Level
import garipolesya.com.example.wifianalyzer.data.model.Wifi
import garipolesya.com.example.wifianalyzer.databinding.ItemWifiBinding
import garipolesya.com.example.wifianalyzer.ui.WifiAdapter.Companion.CONNECTED_BUNDLE_KEY
import garipolesya.com.example.wifianalyzer.ui.WifiAdapter.Companion.LEVEL_BUNDLE_KEY
import garipolesya.com.example.wifianalyzer.util.parcelable

class WifiViewHolder(private val binding: ItemWifiBinding) : RecyclerView.ViewHolder(binding.root) {
    private val context = binding.root.context

    fun bind(wifi: Wifi) {
        setName(wifi.name)
        setupLevelIcon(wifi.level)
        setupPasswordIcon(wifi.isOpen, wifi.isConnected)
        setupColor(wifi.isConnected)
    }

    private fun setName(name: String) {
        binding.tvWifiName.text =
            name.ifEmpty { context.resources.getString(R.string.unknown_network) }
    }

    private fun setupLevelIcon(level: Level) {
        val iconLevel = when (level) {
            Level.STRONG -> R.drawable.ic_wifi
            Level.AVERAGE -> R.drawable.ic_wifi_medium
            Level.WEAK -> R.drawable.ic_wifi_low
        }
        binding.ivWifiIcon.setImageResource(iconLevel)
    }

    private fun setupPasswordIcon(isOpen: Boolean, isConnected: Boolean) {
        val icon = if (isConnected) {
            R.drawable.ic_check
        } else {
            if (isOpen) {
                null
            } else R.drawable.ic_lock
        }
        if (icon != null) binding.ivPasswordIcon.setImageResource(icon)
        binding.ivPasswordIcon.isVisible = !isOpen || isConnected
    }

    private fun setupColor(isConnected: Boolean) {
        val textColor = if (isConnected) R.color.blue_500 else R.color.black
        binding.tvWifiName.setTextColor(ContextCompat.getColor(context, textColor))
        if (isConnected) {
            val connectedColor = ContextCompat.getColor(context, R.color.blue_500)
            binding.ivPasswordIcon.setColorFilter(connectedColor)
            binding.ivWifiIcon.setColorFilter(connectedColor)
        } else {
            binding.ivPasswordIcon.colorFilter = null
            binding.ivWifiIcon.colorFilter = null
        }
    }

    fun bind(
        wifi: Wifi,
        payloads: List<Any>
    ) {
        val bundle = payloads.last() as Bundle
        for (key in bundle.keySet()) {
            when (key) {
                LEVEL_BUNDLE_KEY -> {
                    val newLevelValue = bundle.parcelable<Level>(LEVEL_BUNDLE_KEY)
                   if (newLevelValue != null) setupLevelIcon(newLevelValue)
                }
                CONNECTED_BUNDLE_KEY -> {
                    val newIsConnectedValue = bundle.getBoolean(CONNECTED_BUNDLE_KEY)
                    setupColor(newIsConnectedValue)
                    setupPasswordIcon(wifi.isOpen, newIsConnectedValue)
                }
            }

        }
    }

}
