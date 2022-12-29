package ua.alegator1209.voltpolska.ui.screens.start

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import ua.alegator1209.voltpolska.data.ScanRepository
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
  private val scanRepository: ScanRepository,
) : ViewModel() {
  var scanState by mutableStateOf(ScanState.NOT_STARTED)
    private set

  var devices by mutableStateOf(listOf<BluetoothDevice>())
    private set

  fun startScan() {
    devices = listOf()

    if (!scanRepository.isBluetoothEnabled) {
      println("Bluetooth disabled")
      return
    }

    if (!scanRepository.isGpsEnabled) {
      println("Location disabled")
      return
    }

    scanState = ScanState.IN_PROGRESS
    viewModelScope.launch(SupervisorJob()) {
      launch {
        scanRepository.startScan()
          .map { it.device }
          .collect { device ->
            devices = devices.filter { device.address != it.address } + device
          }
      }.join()

      scanState = ScanState.FINISHED
    }
  }

  fun stopScan() {
    scanRepository.stopScan()
    scanState = ScanState.FINISHED
  }
}
