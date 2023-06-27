package garipolesya.com.example.wifianalyzer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import garipolesya.com.example.wifianalyzer.data.model.Wifi
import garipolesya.com.example.wifianalyzer.databinding.ItemWifiBinding

class WifiAdapter(private val onItemClick: (Wifi) -> Unit) :
    ListAdapter<Wifi, WifiViewHolder>(DiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWifiBinding.inflate(inflater, parent, false)
        return WifiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun onBindViewHolder(
        holder: WifiViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
                holder.bind(getItem(position), payloads)
        }
    }

    private object DiffUtilCallback : DiffUtil.ItemCallback<Wifi>() {

        override fun areItemsTheSame(oldItem: Wifi, newItem: Wifi) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Wifi, newItem: Wifi) =
            oldItem == newItem

        override fun getChangePayload(oldItem: Wifi, newItem: Wifi): Any {
            val diffBundle = Bundle()
            if (oldItem.isConnected != newItem.isConnected)
                diffBundle.putBoolean(CONNECTED_BUNDLE_KEY, newItem.isConnected)
            if (oldItem.level != newItem.level) diffBundle.putParcelable(
                LEVEL_BUNDLE_KEY,
                newItem.level
            )
            return diffBundle
        }
    }

    companion object {
        const val CONNECTED_BUNDLE_KEY = "connected bundle key"
        const val LEVEL_BUNDLE_KEY = "level bundle key"
    }

}
