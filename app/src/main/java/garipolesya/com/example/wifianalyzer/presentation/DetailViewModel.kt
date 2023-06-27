package garipolesya.com.example.wifianalyzer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import garipolesya.com.example.wifianalyzer.data.repository.ConnectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class DetailViewModel(
    private val passwordRepository: ConnectionRepository
) : ViewModel() {
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    fun tryToConnect(id: String, password: String) {
        viewModelScope.launch {
            passwordRepository.connectWifi(id, password)
                .flowOn(Dispatchers.Main)
                .collect {
                    _isConnected.postValue(it)
                }
        }
    }

}

