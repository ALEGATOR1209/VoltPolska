package ua.alegator1209.voltpolska.ui.screens.device

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.alegator1209.voltpolska.data.DeviceRepository
import ua.alegator1209.voltpolska.domain.models.DeviceInfo
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
) : ViewModel() {
    var deviceState by mutableStateOf<DeviceUiState>(DeviceUiState.Loading)
        private set

    sealed class DeviceUiState {
        object Loading : DeviceUiState()
        object Error : DeviceUiState()
        data class Data(val data: DeviceInfo) : DeviceUiState()
    }

    init {
        refreshDeviceState()
    }

    fun refreshDeviceState() {
        deviceState = DeviceUiState.Loading
        viewModelScope.launch {
            deviceState = try {
                DeviceUiState.Data(deviceRepository.getDeviceInfo())
            } catch (e: IllegalStateException) {
                DeviceUiState.Error
            }
        }
    }
}
