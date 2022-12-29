package ua.alegator1209.voltpolska.ui.screens.start

import android.annotation.SuppressLint
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
import ua.alegator1209.voltpolska.data.DeviceRepository
import ua.alegator1209.voltpolska.data.ScanRepository
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")
class StartViewModel @Inject constructor(
  private val scanRepository: ScanRepository,
  private val deviceRepository: DeviceRepository,
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
          .collect(this@StartViewModel::checkDevice)
      }.join()

      scanState = ScanState.FINISHED
    }
  }

  private fun checkDevice(device: BluetoothDevice) {
    viewModelScope.launch {
      if (deviceRepository.isVoltPolskaDevice(device)) {
        devices = devices
          .filter { it.address != device.address }
          .plus(device)
          .sortedBy { "${it.name}__${it.address}" }
      }
    }
  }

  fun stopScan() {
    scanRepository.stopScan()
    scanState = ScanState.FINISHED
  }
}
