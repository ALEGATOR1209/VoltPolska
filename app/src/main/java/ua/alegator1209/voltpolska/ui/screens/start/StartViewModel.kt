package ua.alegator1209.voltpolska.ui.screens.start

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.alegator1209.voltpolska.data.DeviceRepository
import ua.alegator1209.voltpolska.data.ScanRepository
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")
class StartViewModel @Inject constructor(
  private val scanRepository: ScanRepository,
  private val deviceRepository: DeviceRepository,
) : ViewModel() {
  var uiState by mutableStateOf(UiState())

  data class UiState(
    val deviceName: String = "",
    val isSearchInProgress: Boolean = false,
    val timerValue: Int = 0,
    val devices: List<Device> = listOf(),
    val error: Error = Error.None,
    val isLoading: Boolean = false,
  ) {
    data class Device(val name: String, val address: String)
    enum class Error {
      None,
      BluetoothPermissionNeeded,
      LocationPermissionNeeded,
      BluetoothAndLocationPermissionNeeded,
      BluetoothDisabled,
      LocationDisabled,
    }
  }

//
//  fun startScan() {
//    devices = listOf()
//
//    if (!scanRepository.isBluetoothEnabled) {
//      println("Bluetooth disabled")
//      return
//    }
//
//    if (!scanRepository.isGpsEnabled) {
//      println("Location disabled")
//      return
//    }
//
//    scanState = ScanState.IN_PROGRESS
//    viewModelScope.launch(SupervisorJob()) {
//      launch {
//        scanRepository.startScan()
//          .map { it.device }
//          .filter { it.name != null }
//          .collect {
//            scanRepository.stopScan()
//            checkDevice(it)
//            scanRepository.startScan()
//          }
//      }.join()
//
//      scanState = ScanState.FINISHED
//    }
//  }
//
//  private fun checkDevice(device: BluetoothDevice) {
//    viewModelScope.launch {
//      if (deviceRepository.isVoltPolskaDevice(device)) {
//        devices = devices
//          .filter { it.address != device.address }
//          .plus(device)
//          .sortedBy { "${it.name}__${it.address}" }
//      }
//    }
//  }
//
//  fun stopScan() {
//    scanRepository.stopScan()
//    scanState = ScanState.FINISHED
//  }
//
//  fun connectDevice(device: BluetoothDevice) {
//    stopScan()
//    connectionState = ConnectionState.IN_PROGRESS
//    viewModelScope.launch {
//      val deviceConnected = deviceRepository.connect(device)
//
//      connectionState = if (deviceConnected) ConnectionState.CONNECTED else ConnectionState.NOT_CONNECTED
//    }
//  }
}
