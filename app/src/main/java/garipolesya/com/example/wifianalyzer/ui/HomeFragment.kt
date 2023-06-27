package garipolesya.com.example.wifianalyzer.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import garipolesya.com.example.wifianalyzer.R
import garipolesya.com.example.wifianalyzer.data.model.Wifi
import garipolesya.com.example.wifianalyzer.databinding.FragmentHomeBinding
import garipolesya.com.example.wifianalyzer.presentation.UiState
import garipolesya.com.example.wifianalyzer.presentation.WifiViewModel
import garipolesya.com.example.wifianalyzer.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var wifiAdapter: WifiAdapter? = null
    private val viewModel by viewModel<WifiViewModel>()
    private val handler = Handler(Looper.getMainLooper())
    private var dialogWasShown = false

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(), ::onGotLocationPermissionResult
    )

    private val a = Runnable {
        viewModel.tryScan()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarMenu()
        initWifiList()
    }

    private fun initToolbarMenu() {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_export -> exportData()
            }; true
        }
    }

    private fun exportData() {
        viewModel.isExportSuccess.observe(viewLifecycleOwner) {
            if (it) showToast(R.string.export_success) else showToast(R.string.export_unsuccessfully)
        }
        viewModel.exportData()
    }

    private fun initWifiList() {
        wifiAdapter = WifiAdapter { wifi ->
            if (wifi.isConnected) {
                showToast(R.string.network_already_connected_message)
            } else if (!wifi.isOpen) showDetail(wifi.id, wifi.name)
        }
        binding.networksRv.adapter = wifiAdapter

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.NeedPermissions -> requestPermission()
                is UiState.NeedLocationEnable -> if (
                    !dialogWasShown) showDialogNeedToEnableLocations(
                ) else showLocationDisabledMessage()
                is UiState.DisabledWifi -> showDisabledWifiMessage()
                is UiState.Loading -> showLoadingProcess()
                is UiState.WifiList -> updateWifiList(it.list)
                is UiState.Error -> showToast(
                    it.t.message ?: resources.getString(R.string.unknown_error_message)
                )
            }
        }
    }

    private fun showDetail(id: String, name: String) {
        if (childFragmentManager.findFragmentByTag(DetailBottomSheet.DETAIL_TAG) == null)
            DetailBottomSheet.newInstance(id, name)
                .show(childFragmentManager, DetailBottomSheet.DETAIL_TAG)
    }

    private fun showLoadingProcess() {
        binding.tvMessageInfo.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun requestPermission() {
        binding.tvMessageInfo.isVisible = false
        binding.progressBar.isVisible = false
        requestLocationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun onGotLocationPermissionResult(granted: Map<String, Boolean>) {
        if (granted.values.all { it }) {
            viewModel.tryScan()
        } else {
            binding.progressBar.isVisible = false
            binding.tvMessageInfo.isVisible = true
            binding.tvMessageInfo.setText(R.string.permission_denied_message)
        }
    }

    private fun showDialogNeedToEnableLocations() {
        dialogWasShown = true
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_message)
            .setPositiveButton(
                R.string.dialog_button_ok
            ) { _, _ ->
                startActivity(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                )
            }.setNegativeButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
        handler.postDelayed(a, 5000)
    }

    private fun showLocationDisabledMessage() {
        binding.tvMessageInfo.isVisible = true
        binding.tvMessageInfo.setText(R.string.location_disabled_message)
    }

    private fun showDisabledWifiMessage() {
        wifiAdapter?.submitList(null)
        binding.progressBar.isVisible = false
        binding.tvMessageInfo.isVisible = true
        binding.tvMessageInfo.setText(R.string.disabled_wifi_message)
    }

    private fun updateWifiList(list: List<Wifi>) {
        binding.progressBar.isVisible = false
        binding.tvMessageInfo.isVisible = false
        val sortedList = list.sorted()
        wifiAdapter?.submitList(sortedList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        wifiAdapter = null
        _binding = null
    }

}
