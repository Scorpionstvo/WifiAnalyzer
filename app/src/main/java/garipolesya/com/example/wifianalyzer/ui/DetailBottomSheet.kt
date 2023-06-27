package garipolesya.com.example.wifianalyzer.ui

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import garipolesya.com.example.wifianalyzer.R
import garipolesya.com.example.wifianalyzer.databinding.BottomSheetPasswordBinding
import garipolesya.com.example.wifianalyzer.manager.WifiPasswordManager
import garipolesya.com.example.wifianalyzer.presentation.DetailViewModel
import garipolesya.com.example.wifianalyzer.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetPasswordBinding? = null
    private val binding get() = _binding!!
    private var behavior: BottomSheetBehavior<*>? = null
    private val viewModel by viewModel<DetailViewModel>()
    private var passwordVisible = false

    companion object {
        const val DETAIL_TAG = "detail tag"
        private const val WIFI_ID = "wifi id key"
        private const val WIFI_NAME = "wifi id name"
        private const val MIN_PASSWORD_LENGTH = 6

        fun newInstance(wifiId: String, wifiName: String) = DetailBottomSheet().apply {
            arguments = Bundle().apply {
                putString(WIFI_ID, wifiId)
                putString(WIFI_NAME, wifiName)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            setupBottomSheet(it)
        }
        @Suppress("DEPRECATION")
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )
            ?: return
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        behavior = BottomSheetBehavior.from(bottomSheet)
        behavior?.skipCollapsed = true
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getString(WIFI_ID)
        val name = arguments?.getString(WIFI_NAME)
        binding.tvName.text = name
        viewModel.isConnected.observe(viewLifecycleOwner) {
            val displayName = name?.ifEmpty { "Unknown network" }
            viewModel.isConnected.observe(viewLifecycleOwner) { connected ->
                val resultMessage = if (connected) getString(
                    R.string.success_connected_message,
                    displayName
                ) else getString(R.string.unsuccessfully_connected_message, displayName)
                showToast(resultMessage)
                dismiss()
            }
        }
        setupInput()
        if (id != null && name != null) setupButton(id)
    }

    private fun setupInput() {
        binding.inputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.btnConnect.isEnabled = p0.toString().length >= MIN_PASSWORD_LENGTH
            }
        })

        binding.tiPassword.setEndIconOnClickListener {
            if (passwordVisible) {
                binding.inputPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.tiPassword.endIconDrawable?.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.grey_700
                    )
                )
                passwordVisible = false
            } else {
                binding.tiPassword.endIconDrawable?.setTint(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                binding.inputPassword.transformationMethod = null
                passwordVisible = true
            }
        }
    }

    private fun setupButton(id: String) {
        binding.btnConnect.setOnClickListener {
            val password = binding.inputPassword.text.toString()
            val wifiPasswordManager = WifiPasswordManager()
            if (wifiPasswordManager.checkPassword(id, password)) {
                viewModel.tryToConnect(id, password)
            } else binding.inputPassword.error = resources.getString(R.string.error_message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
