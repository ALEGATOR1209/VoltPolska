package ua.alegator1209.voltpolska.ui.screens.device

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import ua.alegator1209.voltpolska.data.DeviceRepository
import ua.alegator1209.voltpolska.ui.common.BatteryStatus
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
) : ViewModel() {
    private val POLLING_INTERVAL = 1000L

    var uiState by mutableStateOf(UiState())
        private set

    private var pollingJob: Job? = null

    init {
        startPolling()
    }

    private fun startPolling() {
        pollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                withContext(Dispatchers.Main) {
                    refreshDeviceState()
                }
                delay(POLLING_INTERVAL)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }

    private fun refreshDeviceState() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val deviceInfo = deviceRepository.getDeviceInfo()
                uiState = uiState.copy(
                    deviceName = deviceInfo.deviceName,
                    isLoading = false,
                    info = BatteryInfo(
                        status = when {
                            deviceInfo.electricCurrent >= 0.1 -> BatteryStatus.CHARGING
                            deviceInfo.electricCurrent <= -0.1 -> BatteryStatus.DISCHARGING
                            else -> BatteryStatus.STAND_BY
                        },
                        nominalCapacity = deviceInfo.nominalCapacity,
                        remainingCapacity = deviceInfo.remainingCapacity,
                        electricCurrent = deviceInfo.electricCurrent,
                        totalVoltage = deviceInfo.totalVoltage,
                        consumption = deviceInfo.totalVoltage * deviceInfo.electricCurrent,
                    )
                )
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }
    }
}
