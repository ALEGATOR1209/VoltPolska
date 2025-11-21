package ua.alegator1209.voltpolska.ui.screens.start

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import ua.alegator1209.voltpolska.data.DeviceRepository
import ua.alegator1209.voltpolska.data.ScanRepository
import ua.alegator1209.voltpolska.utils.fire
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")
class StartViewModel @Inject constructor(
  private val scanRepository: ScanRepository,
  private val deviceRepository: DeviceRepository,
) : ViewModel() {
  private val SCAN_TIMEOUT = 60
  private var devices = mutableListOf<BluetoothDevice>()
  private var scanJob: Job? = null
  private val mutex = Mutex()

  var uiState by mutableStateOf(UiState())
  private val _deviceConnectedEvent = MutableSharedFlow<Unit>()
  val deviceConnectedEvent = _deviceConnectedEvent.asSharedFlow()

  fun setDeviceName(name: String) {
    uiState = uiState.copy(deviceName = name)
  }

  fun startScan() {
    devices.clear()

    viewModelScope.launch {
      uiState = uiState.copy(
        isSearchInProgress = true,
        timerValue = SCAN_TIMEOUT,
      )

      scanJob = launch(Dispatchers.Default) {
        scanRepository.startScan(uiState.deviceName)
          .map { it.device }
          .filter { it.name != null }
          .filter {
            println("FOUND DEVICE: ${it.name}")
            deviceRepository.isVoltPolskaDevice(it)
          }
          .collect { device ->
            mutex.lock()

            devices.removeIf { it.address == device.address }
            devices += device

            uiState = uiState.copy(devices = devices
              .map {
                UiState.Device(it.name, it.address)
              }
            )

            mutex.unlock()
          }
      }

      launch(Dispatchers.Default) {
        while (uiState.timerValue > 0) {
          delay(1000L)

          mutex.lock()
          uiState = uiState.copy(timerValue = uiState.timerValue - 1)
          mutex.unlock()
        }

        stopScan()
      }
    }
  }

  private suspend fun stopScan() {
    scanRepository.stopScan()
    scanJob?.cancelAndJoin()
    scanJob = null
    uiState = uiState.copy(isSearchInProgress = false)
  }

  fun connectDevice(device: UiState.Device) {
    viewModelScope.launch {
      stopScan()
      uiState = uiState.copy(isLoading = true)

      val bluetoothDevice = devices.find { it.address == device.address }

      if (bluetoothDevice == null) {
        devices.clear()
        uiState = uiState.copy(
          isLoading = false,
          devices = listOf(),
        )
        return@launch
      }

      val deviceConnected = deviceRepository.connect(bluetoothDevice)

      if (deviceConnected) {
        _deviceConnectedEvent.fire()
      } else {
        uiState = uiState.copy(
          isLoading = false,
          error = UiState.Error.DeviceConnectionError,
        )
      }
    }
  }
}
